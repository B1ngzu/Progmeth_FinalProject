package Progmeth_project2.interfaces;

import java.io.IOException;

/**
 * Implemented by any game element whose state can be serialised to and
 * deserialised from a persistent storage location (file, database, etc.).
 */
public interface Persistable {

    /**
     * Persist the current state to the specified path.
     *
     * @param path file-system path to write data to
     * @throws IOException if the write operation fails
     */
    void save(String path) throws IOException;

    /**
     * Load previously persisted state from the specified path.
     *
     * @param path file-system path to read data from
     * @throws IOException if the read operation fails
     */
    void load(String path) throws IOException;
}
