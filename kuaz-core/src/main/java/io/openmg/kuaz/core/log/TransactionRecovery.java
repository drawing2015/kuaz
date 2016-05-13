package io.openmg.kuaz.core.log;

import io.openmg.kuaz.core.TitanException;

/**
 * {@link TransactionRecovery} is a process that runs in the background and read's from the transaction
 * write-ahead log to determine which transactions have not been successfully persisted against all
 * backends. It then attempts to recover such transactions.
 * <p/>
 * This process is started via {@link io.openmg.kuaz.core.TitanFactory#}
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface TransactionRecovery {

    /**
     * Shuts down the transaction recovery process
     *
     * @throws TitanException
     */
    public void shutdown() throws TitanException;

}
