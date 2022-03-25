package com.filesystem.server.services;

import com.filesystem.server.entities.DataClientConnection;
import com.filesystem.server.entities.Node;
import com.filesystem.server.exceptions.NodeAlreadyExistsException;
import com.filesystem.server.exceptions.NodeDoesntExistById;
import com.filesystem.server.repositories.NodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class contains the logic of the Node-Service. Here are all methods and functions to gather, modify and delete
 * Nodes.
 */
@Service
public class NodeService {

    private static final Logger logger = LoggerFactory.getLogger(NodeService.class);

    private final NodeRepository nodeRepository;
    private final DataClientService dataClientService;

    /**
     * Instantiates a new Node-Service and autowires all needed beans.
     *
     * @param nodeRepository                 the node repository
     * @param dataClientService              the data client service
     */
    @Autowired
    public NodeService(NodeRepository nodeRepository, DataClientService dataClientService) {
        this.nodeRepository = nodeRepository;
        this.dataClientService = dataClientService;
    }

    /**
     * Reads a Node by given Id from the database.
     *
     * @param nodeId the Node-Id
     * @return the Node
     */
    public Node readNode(String nodeId) {
        logger.debug("CALLED: readNode()");

        return this.nodeRepository.findById(nodeId).orElseThrow();
    }

    /**
     * Reads all Nodes from the database.
     *
     * @return list of Nodes
     */
    public List<Node> readAllNodes() {
        logger.debug("CALLED: readNodeByName()");

        return this.nodeRepository.findAll();
    }

    /**
     * Reads all Nodes from the database with the given Name.
     *
     * @param name the Name to be checked
     * @return list of all Nodes matching the given Name
     */
    public List<Node> readNodeByName(String name) {
        logger.debug("CALLED: readNodeByName() with name=" + name);

        return this.nodeRepository.findAllByName(name);
    }

    /**
     * Create a new node and append it to a given ParentNode.
     *
     * @param parentNode the ParentNode to appended the ChildNode to
     * @param newNode    the Node to be created
     * @return the stored node
     */
    public Node createNode(Node parentNode, Node newNode) {
        logger.debug("CALLED: createNode() with parentNode=" + parentNode.getId() + " newNode=" + newNode.getId());

        if (parentNode != null)
            newNode.setParentNode(parentNode);

        Node finalNewNode = newNode;
        List <Node> matchingNodes = this.nodeRepository.findAllByName(newNode.getName()).stream().filter(node -> node.getName().equals(finalNewNode.getName()) && node.getParentNode() == finalNewNode.getParentNode()).collect(Collectors.toList());

        if (matchingNodes.size() > 1) {
            throw new RuntimeException("The structure of the filesystem is inconsistent!");
        } else if (matchingNodes.size() == 1) {
            Node matchingNode = matchingNodes.get(0);
            matchingNode.addChildNodes(newNode.getChildNodes());
            return this.nodeRepository.save(matchingNode);
        } else {
            newNode = this.nodeRepository.save(newNode);
            parentNode.addChildNode(newNode);
            this.nodeRepository.save(parentNode);
            return newNode;
        }
    }

    /**
     * Save a Node to the database.
     *
     * @param node the Node to be saved
     * @return the saved Node
     */
    public Node saveNode(Node node) {
        logger.debug("CALLED: saveNode() with node=" + node.getId());

        if (node.getName() != "root") {
            throw new RuntimeException("Function should only be used for root-node creation!");
        }

        if (this.nodeRepository.existsNodeByName(node.getName())) {
            throw new NodeAlreadyExistsException(node);
        }

        return this.nodeRepository.save(node);
    }

    /**
     * Delete a node by given id.
     *
     * @param nodeId the Node-Id
     */
    public void deleteNode(String nodeId) {
        logger.debug("CALLED: deleteNode() with nodeId=" + nodeId);

        try {
            Node node = this.nodeRepository.findById(nodeId).orElseThrow();

            for (DataClientConnection dataClientConnection: node.getDataClientConnection()) {
                this.dataClientService.deleteNode(dataClientConnection, node.toDataClientPath());
            }

            Node parentNode = node.getParentNode();
            parentNode.removeChildNode(node);
            this.nodeRepository.save(parentNode);

            traverseNodeDownAndDelete(node);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void traverseNodeDownAndDelete(Node node) {
        logger.debug("CALLED: traverseNodeDownAndDelete() with node=" + node.getId());

        for (Node childNode: node.getChildNodes())
            traverseNodeDownAndDelete(childNode);

        this.nodeRepository.delete(node);
    }

    /**
     * Initialize the Data-Structure of a given DataClientConnection on the server.
     *
     * @param rootNode             the Root-Node
     * @param dataClientConnection the DataClientConnection
     * @return the new Node-Structure
     */
    public Node initDataStructure(Node rootNode, DataClientConnection dataClientConnection) {
        logger.debug("CALLED: initDataStructure() with rootNode=" + rootNode.getName() + " dataClientConnection=" + dataClientConnection.getName());

        return traverseNodeDownAndSave(rootNode, dataClientConnection);
    }

    private Node traverseNodeDownAndSave(Node currentNode, DataClientConnection dataClientConnection) {
        logger.debug("CALLED: traverseNodeDownAndSave() with currentNode=" + currentNode.getName() + " dataClientConnection=" + dataClientConnection.getName());

        Node existingNode = getExistingNode(currentNode);
        existingNode.updateChildNodes(currentNode);
        existingNode.addDataClientConnection(dataClientConnection);
        existingNode = this.nodeRepository.save(existingNode);

        for (Node childNode: currentNode.getChildNodes()) {
            existingNode = this.nodeRepository.save(existingNode);
            childNode.setParentNode(existingNode);
            childNode.addDataClientConnection(dataClientConnection);

            Node existingChildNode = getExistingNode(childNode);
            existingChildNode.updateChildNodes(childNode);
            existingChildNode.addDataClientConnection(dataClientConnection);
            traverseNodeDownAndSave(childNode, dataClientConnection);

            this.nodeRepository.save(existingChildNode);
            existingNode = this.nodeRepository.save(existingNode);
        }

        return existingNode;
    }

    private Node getExistingNode(Node currentNode) {
        logger.debug("CALLED: getExistingNode() with currentNode=" + currentNode.getName());

        List<Node> nodes = this.nodeRepository.findAllByName(currentNode.getName());
        if (nodes.size() == 0) {
            return currentNode;
        }

        if (currentNode.getParentNode() == null) {
            return nodes.stream().filter(node -> node.getParentNode() == null).collect(Collectors.toList()).get(0);
        }

        nodes = nodes.stream().filter(node -> Objects.equals(node.getParentNode().getId(), currentNode.getParentNode().getId())).collect(Collectors.toList());
        if (nodes.size() > 0) {
            return nodes.get(0);
        }

        return currentNode;
    }

    /**
     * Update the Node-Name of a Node referenced by its Id.
     *
     * @param nodeId the Node-Id
     * @param name   the new Node-Name
     * @return the updated node
     */
    public Node updateNodeName(String nodeId, String name) {
        logger.debug("CALLED: updateNodeName() with nodeId=" + nodeId + " name=" + name);

        Node existingNode = this.nodeRepository.findById(nodeId).orElseThrow(() -> new NodeDoesntExistById(nodeId));

        for (DataClientConnection dataClientConnection: existingNode.getDataClientConnection()) {
            this.dataClientService.renameNode(dataClientConnection, existingNode.toDataClientPath(), name);
        }

        existingNode.setName(name);

        return this.nodeRepository.save(existingNode);
    }

    /**
     * Create a ChildNode under the given node (referenced by id).
     *
     * @param nodeId       the Node-Id to be updated
     * @param newChildNode the new ChildNode to be created
     * @return the stored ChildNode
     */
    public Node createChildNode(String nodeId, Node newChildNode) {
        logger.debug("CALLED: createChildNode() with nodeId=" + nodeId + " newChildNode=" + newChildNode.getName());

        Node existingNode = this.nodeRepository.findById(nodeId).orElseThrow();
        newChildNode.setParentNode(existingNode);
        newChildNode.addDataClientConnections(existingNode.getDataClientConnection());

        for (DataClientConnection dataClientConnection: newChildNode.getDataClientConnection()) {
            this.dataClientService.createNode(dataClientConnection, newChildNode.toDataClientPath());
        }

        existingNode.addChildNode(this.nodeRepository.save(newChildNode));
        existingNode = this.nodeRepository.save(existingNode);

        Node parentNode = newChildNode.getParentNode();
        while (parentNode != null) {
            parentNode = this.nodeRepository.save(parentNode).getParentNode();
        }

        return existingNode;
    }

    /**
     * Delete all nodes for a given DataClientConnection.
     *
     * @param dataClientConnection the DataClientConnection to delete all Nodes for
     */
    public void deleteNodesForDataClientConnection(DataClientConnection dataClientConnection) {
        logger.debug("CALLED: deleteNodesForDataClientConnection() with dataClientConnection=" + dataClientConnection.getName());

        Node rootNode = this.nodeRepository.findAllByDataClientConnectionAndParentNodeIsNull(dataClientConnection).get(0);
        traverseNodeDownAndDeleteForDataClientConnection(rootNode, dataClientConnection);
    }

    private void traverseNodeDownAndDeleteForDataClientConnection(Node node, DataClientConnection dataClientConnection) {
        logger.debug("CALLED: traverseNodeDownAndDeleteForDataClientConnection() with node=" + node.getName() + " dataClientConnection=" + dataClientConnection.getName());

        for (Node childNode: node.getChildNodes().stream().filter(childNode -> childNode.getDataClientConnection().contains(dataClientConnection)).collect(Collectors.toList())) {
            traverseNodeDownAndDeleteForDataClientConnection(childNode, dataClientConnection);
        }

        if (node.getDataClientConnection().size() == 1) {
            this.nodeRepository.delete(node);
        } else {
            node.getDataClientConnection().remove(dataClientConnection);
            this.nodeRepository.save(node);
        }
    }

    /**
     * Reads all Nodes which contain no Parent-Node (Root-Nodes).
     *
     * @return list of all Root-Nodes (Nodes with no ParentNode)
     */
    public List<Node> readRoot() {
        logger.debug("CALLED: readRoot()");

        return this.nodeRepository.findAllByParentNodeIsNull();
    }
}
