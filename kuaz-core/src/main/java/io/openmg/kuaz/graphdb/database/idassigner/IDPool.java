package io.openmg.kuaz.graphdb.database.idassigner;

public interface IDPool {

    public long nextID();

    public void close();

}
