package Progmeth_project2.interfaces;

import java.io.IOException;

/**
 * Implemented by any game element whose state can be serialised to and
 * deserialised from a persistent storage location (file, database, etc.).
 */
public interface Persistable {

    /**
     * Serialises this element's state to the given file path.
     *
     * @param path destination file path
     * @throws IOException if writing fails
     */
    void save(String path) throws IOException;

    /**
     * Deserialises this element's state from the given file path.
     *
     * @param path source file path
     * @throws IOException if reading fails
     */
    void load(String path) throws IOException;
}
