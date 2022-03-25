package com.filesystem.server.exceptions;

import com.filesystem.server.entities.DataClientConnection;

public class DataClientRestApiException extends RuntimeException {
    public DataClientRestApiException(String apiEndpoint, DataClientConnection dataClientConnection) {
        super(String.format("An error occured during the call of %s on the DataClient with IPv4: %s and Port: %s ", apiEndpoint, dataClientConnection.getIpv4(), dataClientConnection.getPort()));
    }
}
