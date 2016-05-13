package io.openmg.kuaz.diskstorage.configuration.backend;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.openmg.kuaz.core.TitanException;
import io.openmg.kuaz.diskstorage.BackendException;
import io.openmg.kuaz.diskstorage.configuration.Configuration;

import io.openmg.kuaz.diskstorage.util.time.TimestampProvider;
import io.openmg.kuaz.diskstorage.Entry;
import io.openmg.kuaz.diskstorage.StaticBuffer;
import io.openmg.kuaz.diskstorage.configuration.ConcurrentWriteConfiguration;
import io.openmg.kuaz.diskstorage.configuration.ReadConfiguration;
import io.openmg.kuaz.diskstorage.configuration.WriteConfiguration;
import io.openmg.kuaz.diskstorage.keycolumnvalue.*;
import io.openmg.kuaz.diskstorage.util.BackendOperation;
import io.openmg.kuaz.diskstorage.util.BufferUtil;
import io.openmg.kuaz.diskstorage.util.StaticArrayBuffer;
import io.openmg.kuaz.diskstorage.util.StaticArrayEntry;
import io.openmg.kuaz.graphdb.database.serialize.DataOutput;
import io.openmg.kuaz.graphdb.database.serialize.StandardSerializer;

import io.openmg.kuaz.util.system.IOUtils;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.openmg.kuaz.graphdb.configuration.GraphDatabaseConfiguration.TIMESTAMP_PROVIDER;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class KCVSConfiguration implements ConcurrentWriteConfiguration {

    private final BackendOperation.TransactionalProvider txProvider;
    private final TimestampProvider times;
    private final KeyColumnValueStore store;
    private final String identifier;
    private final StaticBuffer rowKey;
    private final StandardSerializer serializer;

    private Duration maxOperationWaitTime = Duration.ofMillis(10000L);

    public KCVSConfiguration(BackendOperation.TransactionalProvider txProvider, Configuration config,
                             KeyColumnValueStore store, String identifier) throws BackendException {
        Preconditions.checkArgument(txProvider!=null && store!=null && config!=null);
        Preconditions.checkArgument(StringUtils.isNotBlank(identifier));
        this.txProvider = txProvider;
        this.times = config.get(TIMESTAMP_PROVIDER);
        this.store = store;
        this.identifier = identifier;
        this.rowKey = string2StaticBuffer(this.identifier);
        this.serializer = new StandardSerializer();
    }

    public void setMaxOperationWaitTime(Duration waitTime) {

        Preconditions.checkArgument(Duration.ZERO.compareTo(waitTime) < 0,
                "Wait time must be nonnegative: %s", waitTime);
        this.maxOperationWaitTime = waitTime;
    }



    /**
     * Reads the configuration property for this StoreManager
     *
     * @param key Key identifying the configuration property
     * @return Value stored for the key or null if the configuration property has not (yet) been defined.
     * @throws io.openmg.kuaz.diskstorage.BackendException
     */
    @Override
    public <O> O get(final String key, final Class<O> datatype) {
        StaticBuffer column = string2StaticBuffer(key);
        final KeySliceQuery query = new KeySliceQuery(rowKey,column, BufferUtil.nextBiggerBuffer(column));
        StaticBuffer result = BackendOperation.execute(new BackendOperation.Transactional<StaticBuffer>() {
            @Override
            public StaticBuffer call(StoreTransaction txh) throws BackendException {
                List<Entry> entries = store.getSlice(query,txh);
                if (entries.isEmpty()) return null;
                return entries.get(0).getValueAs(StaticBuffer.STATIC_FACTORY);
            }

            @Override
            public String toString() {
                return "getConfiguration";
            }
        }, txProvider, times, maxOperationWaitTime);
        if (result==null) return null;
        return staticBuffer2Object(result, datatype);
    }

    public<O> void set(String key, O value, O expectedValue) {
        set(key,value,expectedValue,true);
    }

    /**
     * Sets a configuration property for this StoreManager.
     *
     * @param key   Key identifying the configuration property
     * @param value Value to be stored for the key
     * @throws io.openmg.kuaz.diskstorage.BackendException
     */
    @Override
    public <O> void set(String key, O value) {
        set(key,value,null,false);
    }

    public <O> void set(String key, O value, O expectedValue, final boolean checkExpectedValue) {
        final StaticBuffer column = string2StaticBuffer(key);
        final List<Entry> additions;
        final List<StaticBuffer> deletions;
        if (value!=null) { //Addition
            additions = new ArrayList<Entry>(1);
            deletions = KeyColumnValueStore.NO_DELETIONS;
            StaticBuffer val = object2StaticBuffer(value);
            additions.add(StaticArrayEntry.of(column, val));
        } else { //Deletion
            additions = KeyColumnValueStore.NO_ADDITIONS;
            deletions = Lists.newArrayList(column);
        }
        final StaticBuffer expectedValueBuffer;
        if (checkExpectedValue && expectedValue!=null) {
            expectedValueBuffer = object2StaticBuffer(expectedValue);
        } else {
            expectedValueBuffer = null;
        }

        BackendOperation.execute(new BackendOperation.Transactional<Boolean>() {
            @Override
            public Boolean call(StoreTransaction txh) throws BackendException {
                if (checkExpectedValue)
                    store.acquireLock(rowKey,column,expectedValueBuffer,txh);
                store.mutate(rowKey, additions, deletions, txh);
                return true;
            }

            @Override
            public String toString() {
                return "setConfiguration";
            }
        }, txProvider, times, maxOperationWaitTime);
    }

    @Override
    public void remove(String key) {
        set(key,null);
    }

    @Override
    public WriteConfiguration copy() {
        throw new UnsupportedOperationException();
    }

    private Map<String,Object> toMap() {
        Map<String,Object> entries = Maps.newHashMap();
        List<Entry> result = BackendOperation.execute(new BackendOperation.Transactional<List<Entry>>() {
            @Override
            public List<Entry> call(StoreTransaction txh) throws BackendException {
                return store.getSlice(new KeySliceQuery(rowKey, BufferUtil.zeroBuffer(1), BufferUtil.oneBuffer(128)),txh);
            }

            @Override
            public String toString() {
                return "setConfiguration";
            }
        },txProvider, times, maxOperationWaitTime);

        for (Entry entry : result) {
            String key = staticBuffer2String(entry.getColumnAs(StaticBuffer.STATIC_FACTORY));
            Object value = staticBuffer2Object(entry.getValueAs(StaticBuffer.STATIC_FACTORY), Object.class);
            entries.put(key,value);
        }
        return entries;
    }

    public ReadConfiguration asReadConfiguration() {
        final Map<String,Object> entries = toMap();
        return new ReadConfiguration() {
            @Override
            public <O> O get(String key, Class<O> datatype) {
                Preconditions.checkArgument(!entries.containsKey(key) || datatype.isAssignableFrom(entries.get(key).getClass()));
                return (O)entries.get(key);
            }

            @Override
            public Iterable<String> getKeys(final String prefix) {
                return Lists.newArrayList(Iterables.filter(entries.keySet(),new Predicate<String>() {
                    @Override
                    public boolean apply(@Nullable String s) {
                        assert s!=null;
                        return StringUtils.isBlank(prefix) || s.startsWith(prefix);
                    }
                }));
            }

            @Override
            public void close() {
                //Do nothing
            }
        };
    }

    @Override
    public Iterable<String> getKeys(String prefix) {
        return asReadConfiguration().getKeys(prefix);
    }

    @Override
    public void close() {
        try {
            store.close();
            txProvider.close();
            IOUtils.closeQuietly(serializer);
        } catch (BackendException e) {
            throw new TitanException("Could not close configuration store",e);
        }
    }

    private StaticBuffer string2StaticBuffer(final String s) {
        ByteBuffer out = ByteBuffer.wrap(s.getBytes(Charset.forName("UTF-8")));
        return StaticArrayBuffer.of(out);
    }

    private String staticBuffer2String(final StaticBuffer s) {
        return new String(s.as(StaticBuffer.ARRAY_FACTORY),Charset.forName("UTF-8"));
    }

    private<O> StaticBuffer object2StaticBuffer(final O value) {
        if (value==null) throw Graph.Variables.Exceptions.variableValueCanNotBeNull();
        if (!serializer.validDataType(value.getClass())) throw Graph.Variables.Exceptions.dataTypeOfVariableValueNotSupported(value);
        DataOutput out = serializer.getDataOutput(128);
        out.writeClassAndObject(value);
        return out.getStaticBuffer();
    }

    private<O> O staticBuffer2Object(final StaticBuffer s, Class<O> datatype) {
        Object value = serializer.readClassAndObject(s.asReadBuffer());
        Preconditions.checkArgument(datatype.isInstance(value),"Could not deserialize to [%s], got: %s",datatype,value);
        return (O)value;
    }

}