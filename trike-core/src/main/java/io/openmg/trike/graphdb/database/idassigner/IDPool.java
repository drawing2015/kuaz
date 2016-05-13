package io.openmg.trike.graphdb.database.idassigner;

public interface IDPool {

    public long nextID();

    public void close();

}
