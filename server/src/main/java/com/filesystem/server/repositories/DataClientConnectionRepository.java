package com.filesystem.server.repositories;

import com.filesystem.server.entities.DataClientConnection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * The DataClientConnection-Repository interface contains the standard-implementation of all CRUD operations of MongoDB for the
 * Node-Entity and also contains all custom queries needed by the system.
 */
@Repository
public interface DataClientConnectionRepository extends MongoRepository<DataClientConnection, String> {

    /**
     * Finds the DataClientConnection by a given IPv4-Address.
     *
     * @param ipv4 the IPv4-Address
     * @return the DataClientConnection
     */
    DataClientConnection findDataClientConnectionByIpv4(String ipv4);

    /**
     * Finds the DataClientConnection by IPv4-Address and Port.
     *
     * @param ipv4 the IPv4-Address
     * @param port the Port
     * @return the DataClientConnection
     */
    DataClientConnection findDataClientConnectionByIpv4AndPort(String ipv4, String port);

    /**
     * Checks if a DataClientConnection already exists in the database by given IPv4-Address.
     *
     * @param ipv4 the IPv4-Address
     * @return a boolean, true if exists else false
     */
    boolean existsDataClientConnectionByIpv4(String ipv4);

    /**
     * Checks if a DataClientConnection already exists in the database by given IPv4-Address and Port.
     *
     * @param ipv4 the IPv4-Address
     * @param port the Port
     * @return a boolean, true if exists else false
     */
    boolean existsDataClientConnectionByIpv4AndPort(String ipv4, String port);
}
