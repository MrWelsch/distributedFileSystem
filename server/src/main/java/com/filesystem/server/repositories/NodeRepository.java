package com.filesystem.server.repositories;

import com.filesystem.server.entities.DataClientConnection;
import com.filesystem.server.entities.Node;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The Node-Repository interface contains the standard-implementation of all CRUD operations of MongoDB for the
 * Node-Entity and also contains all custom queries needed by the system.
 */
@Repository
public interface NodeRepository extends MongoRepository<Node, String> {

    /**
     * Checks if a with a given Name already exists.
     *
     * @param name the name to be checked
     * @return boolean, true if exists else false
     */
    boolean existsNodeByName(String name);

    /**
     * Returns a list of all Nodes with the matching name.
     *
     * @param name the name to be searched
     * @return list of all matching Nodes
     */
    List<Node> findAllByName(String name);

    /**
     * Returns all Nodes which contain a reference to the given DataClientConenction.
     *
     * @param dataClientConnection the DataClientConenction to be checked
     * @return list of all matching Nodes
     */
    List<Node> findAllByDataClientConnection(DataClientConnection dataClientConnection);

    /**
     * Returns all Nodes which contain a reference to the given DataClientConenction and have no ParentNode.
     * (Returns the Root-Node of a DataClient)
     *
     * @param dataClientConnection the DataClientConenction to be checked
     * @return list of all matching Nodes
     */
    List<Node> findAllByDataClientConnectionAndParentNodeIsNull(DataClientConnection dataClientConnection);

    /**
     * Returns all Nodes which have no ParentNode. (Returns all Root-Nodes)
     *
     * @return list of all Root-Nodes
     */
    List<Node> findAllByParentNodeIsNull();

}
