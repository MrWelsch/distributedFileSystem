package com.filesystem.server.exceptions;

public class NodeDoesntExistById extends RuntimeException {

    public NodeDoesntExistById(String nodeId) {
        super(String.format("The node with ID= %s doesnt exist!", nodeId));
    }

}
