package com.filesystem.server.services;

import com.filesystem.server.entities.DataClientConnection;
import com.filesystem.server.exceptions.DataClientDoesntExistById;
import com.filesystem.server.repositories.DataClientConnectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains the logic of the DataClientConnection-Service. Here are all methods and functions to gather,
 * modify and delete DataClientConnections.
 */
@Service
public class DataClientConnectionService {

    private static final Logger logger = LoggerFactory.getLogger(DataClientConnectionService.class);

    private final DataClientConnectionRepository dataClientConnectionRepository;

    /**
     * Instantiates a new DataClientConnection-Service and autowires all needed beans.
     *
     * @param dataClientConnectionRepository DataClientConnection-Repository
     */
    @Autowired
    public DataClientConnectionService(DataClientConnectionRepository dataClientConnectionRepository) {
        this.dataClientConnectionRepository = dataClientConnectionRepository;
    }

    /**
     * Saves a given DataClientConnection-Object.
     *
     * @param newDataClientConnection the new DataClientConnection
     * @return the stored DataClientConnection
     */
    public DataClientConnection saveDataClientConnection(DataClientConnection newDataClientConnection) {
        logger.debug(String.format("CALLED: saveDataClientConnection() dataClientIp=%s dataClientName=%s", newDataClientConnection.getIpv4(), newDataClientConnection.getName()));

        if (!newDataClientConnection.getIpv4().matches("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)(\\.(?!$)|$)){4}$")) {
            throw new IllegalArgumentException("Falsche IP!");
        }

        return this.dataClientConnectionRepository.save(newDataClientConnection);
    }

    /**
     * Reads DataClientConnection by a given Id from the database.
     *
     * @param dataClientConnectionId the DataClientConnection-Id
     * @return the stored DataClientConnection
     */
    public DataClientConnection readDataClientConnectionById(String dataClientConnectionId) {
        logger.debug(String.format("CALLED: readDataClientConnectionById() dataClientConnectionId=%s", dataClientConnectionId));

        return this.dataClientConnectionRepository.findById(dataClientConnectionId).orElseThrow(() -> new DataClientDoesntExistById(dataClientConnectionId));
    }

    /**
     * Reads all DataClientConnections from the database.
     *
     * @return list of all DataClientConnections
     */
    public List<DataClientConnection> readAllDataClientConnection() {
        logger.debug("Called: readAllDataClientConnection()");

        return this.dataClientConnectionRepository.findAll();
    }

    /**
     * Deletes a DataClientConnection from the database.
     *
     * @param dataClientConnection the DataClientConnection to be deleted
     */
    public void deleteDataClientConnection(DataClientConnection dataClientConnection) {
        logger.debug(String.format("Called: deleteDataClientConnection() dataClientConnection=%s", dataClientConnection.getIpv4() + ";" + dataClientConnection.getPort()));

        this.dataClientConnectionRepository.delete(dataClientConnection);
    }

    /**
     * Reads a DataClientConnection by a given IPv4-Address.
     *
     * @param dataClientConnectionIp the DataClientConnection IPv4-Address
     * @return the stored DataClientConnection
     */
    public DataClientConnection readDataClientConnectionByIp(String dataClientConnectionIp) {
        logger.debug(String.format("Called: readDataClientConnectionByIp() dataClientConnectionIp=%s" ,dataClientConnectionIp));

        return this.dataClientConnectionRepository.findDataClientConnectionByIpv4(dataClientConnectionIp);
    }

    /**
     * Reads a DataClientConnection from the database by given IPv4-Address and Port.
     *
     * @param dataClientConnection the DataClientConnection containing the IPv4-Address and Port
     * @return the stored DataClientConnection
     */
    public DataClientConnection readDataClientConnectionByIpAndPort(DataClientConnection dataClientConnection) {
        logger.debug(String.format("Called: readDataClientConnectionByIpAndPort() dataClientConnection=%s", dataClientConnection.getIpv4() + ";" + dataClientConnection.getPort()));

        return this.dataClientConnectionRepository.findDataClientConnectionByIpv4AndPort(dataClientConnection.getIpv4(), dataClientConnection.getPort());
    }

    /**
     * Checks if a DataClientConenction already exists in the database by checking the IPv4-Address and Port given by
     * the DataClientConnection-Object.
     *
     * @param dataClientConnection the DataClientConnection
     * @return a boolean, true if exists else false
     */
    public boolean existsDataClient(DataClientConnection dataClientConnection) {
        logger.debug(String.format("Called: existsDataClient() dataClientConnection=%s", dataClientConnection.getIpv4() + ";" + dataClientConnection.getPort()));

        return this.dataClientConnectionRepository.existsDataClientConnectionByIpv4AndPort(dataClientConnection.getIpv4(), dataClientConnection.getPort());
    }

}
