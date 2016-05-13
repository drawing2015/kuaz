package io.openmg.kuaz.storage.log.kcvs;


import io.openmg.kuaz.storage.StaticBuffer;
import io.openmg.kuaz.storage.log.util.AbstractMessage;

import java.time.Instant;

/**
 * Implementation of {@link AbstractMessage} for {@link KCVSLog}.
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class KCVSMessage extends AbstractMessage {

    public KCVSMessage(StaticBuffer payload, Instant timestamp, String senderId) {
        super(payload, timestamp, senderId);
    }
}
