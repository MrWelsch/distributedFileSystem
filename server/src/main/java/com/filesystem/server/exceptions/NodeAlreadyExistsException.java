package com.filesystem.server.exceptions;

import com.filesystem.server.entities.Node;

public class NodeAlreadyExistsException extends RuntimeException {

    public NodeAlreadyExistsException(Node node) {
        super(String.format("The node: %s already exists!", node));
    }

}
