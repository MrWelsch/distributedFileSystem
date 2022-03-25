package com.filesystem.server.exceptions;

public class DataClientDoesntExistById extends RuntimeException{

    public DataClientDoesntExistById(String dataClientConnectionId) {
        super(String.format("The DataClient with ID= %s doesnt exist!", dataClientConnectionId));
    }

}
