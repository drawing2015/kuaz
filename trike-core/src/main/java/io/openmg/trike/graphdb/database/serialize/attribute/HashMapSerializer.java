package io.openmg.trike.graphdb.database.serialize.attribute;

import io.openmg.trike.core.attribute.AttributeSerializer;
import io.openmg.trike.diskstorage.Backend;
import io.openmg.trike.diskstorage.ScanBuffer;
import io.openmg.trike.diskstorage.WriteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class HashMapSerializer implements AttributeSerializer<HashMap> {

    private static final Logger logger = LoggerFactory.getLogger(HashMapSerializer.class);

    private ByteArraySerializer serializer;

    public HashMapSerializer() {
        serializer = new ByteArraySerializer();
    }

    @Override
    public HashMap read(ScanBuffer buffer) {
        HashMap attribute = null;
        byte[] data = serializer.read(buffer);
        try (
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais)
        ) {
            attribute = (HashMap) ois.readObject();
        } catch (Exception e) {
            logger.warn("can't read buffer [{}] to hash map", data);
        }
        return attribute;
    }

    @Override
    public void write(WriteBuffer buffer, HashMap attribute) {
        try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
        ) {
            oos.writeObject(attribute);
            byte[] data = baos.toByteArray();
            serializer.write(buffer, data);
        } catch (Exception e) {
            logger.warn("can't writer hash map [{}] to scan buffer", attribute);
        }
    }
}