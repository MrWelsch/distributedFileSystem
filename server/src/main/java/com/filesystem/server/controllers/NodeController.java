package com.filesystem.server.controllers;

import com.filesystem.server.entities.Node;
import com.filesystem.server.services.NodeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;

/**
 * Controller class to describe all endpoints regarding the Nodes contained in the virtual filesystem.
 */
@RestController
@RequestMapping(path = "/api/nodes")
public class NodeController {

    private static final Logger logger = LoggerFactory.getLogger(NodeController.class);

    private final NodeService nodeService;

    /**
     * Instantiates a new Node-Controller and autowires the needed beans to work properly.
     *
     * @param nodeService the Node-Service
     */
    @Autowired
    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    /**
     * Get-mapping returning the Node referenced by the Node-Id given as PathVariable.
     *
     * @param nodeId the Node-Id
     * @return the referenced Node
     */
    @Operation(summary = "Returns node referenced by the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the stored information about the node",
                    content = {@Content(mediaType = "application/json")})
    })
    @GetMapping(value = "/{nodeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Node getNode(@Parameter(description = "id of the node to be searched") @PathVariable String nodeId) {
        logger.info(String.format("Called: GET /api/nodes/%s", nodeId));

        return this.nodeService.readNode(nodeId);
    }

    /**
     * Get-mapping returning all Nodes which currently exist in the system.
     *
     * @return list of all existing Nodes
     */
    @Operation(summary = "Returns all Nodes which are currently stored in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "All nodes were collected and returned successfully",
                    content = {@Content(mediaType = "application/json")})
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Node> getAllNodes() {
        logger.info("Called: GET /api/nodes");

        return this.nodeService.readAllNodes();
    }

    /**
     * Get-mapping returning all root Nodes contained in the system. (All Nodes which have no ParentNode)
     *
     * @return list of all Root-Nodes
     */
    @Operation(summary = "Returns root node")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the stored information about the root node",
                    content = {@Content(mediaType = "application/json")})
    })
    @GetMapping(value = "/root", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Node> getRootNode() {
        logger.info("Called: GET /api/nodes/root");

        return this.nodeService.readRoot();
    }

    /**
     * Get-mapping to return all nodes with a given Name referenced by the PathVariable.
     *
     * @param nodeName the Node-Name
     * @return the list of Nodes
     */
    @Operation(summary = "Returns node referenced by the given name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the stored information about the node",
                    content = {@Content(mediaType = "application/json")})
    })
    @GetMapping(value = "/name/{nodeName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Node> getNodeFromName(@Parameter(description = "name of the node to be searched") @PathVariable String nodeName) {
        logger.info(String.format("Called: GET /api/nodes/name/%s", nodeName));

        return this.nodeService.readNodeByName(nodeName);
    }

    /**
     * Get-mapping to return the ParentNode for a given Node-Id.
     *
     * @param nodeId the Node-Id
     * @return the ParentNode of the referenced Node-Id
     */
    @Operation(summary = "Returns parent node from the node with the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the stored information about the parent node",
                    content = {@Content(mediaType = "application/json")})
    })
    @GetMapping(value = "/parent/{nodeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Node getParentNodeFromNodeId(@Parameter(description = "id of the node which parent node is searched") @PathVariable String nodeId) {
        logger.info(String.format("Called: GET /api/nodes/parent/%s", nodeId));

        return this.nodeService.readNode(nodeId).getParentNode();
    }

    /**
     * Put-mapping to update the name of a Node referenced by the given Node-Id. Name and Id are given by PathVariable.
     *
     * @param nodeId   the Node-Id
     * @param nodeName the Node-Name
     * @return the updated Node
     */
    @Operation(summary = "Replaces the node name of the given id with the given name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the stored information about the node",
                    content = {@Content(mediaType = "application/json")})
    })
    @PatchMapping (value = "/{nodeId}/{nodeName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Node putNodeName(@Parameter(description = "id of the node that should be renamed") @PathVariable String nodeId, @Parameter(description = "new name of the node") @PathVariable String nodeName) {
        logger.info(String.format("Called: PATCH /api/nodes/%s/%s", nodeId, nodeName));

        return this.nodeService.updateNodeName(nodeId, nodeName);
    }

    /**
     * Post-mapping to create a new ChildNode in a existing Node which is referenced by its Id.
     *
     * @param nodeId    the Node-Id
     * @param childNode the ChildNode
     * @return the created ChildNode
     */
    @Operation(summary = "Creates a new child node which parent node is the node with the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the stored information about the new node",
                    content = {@Content(mediaType = "application/json")})
    })
    @PostMapping(value = "/{nodeId}/child", produces = MediaType.APPLICATION_JSON_VALUE)
    public Node postChildNode(@Parameter(description = "id of the parent of the new child") @PathVariable String nodeId, @Parameter(description = "the new child node") @RequestBody Node childNode) {
        logger.info(String.format("Called: POST /api/nodes/%s/child", nodeId));

        return this.nodeService.createChildNode(nodeId, childNode);
    }

    /**
     * Delete-mapping to delete a Node referenced by Node-Id.
     *
     * @param nodeId the Node-Id to be deleted
     */
    @Operation(summary = "Deletes a node and all child nodes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Node deleted",
                    content = {@Content(mediaType = "application/json")})
    })
    @DeleteMapping(value ="/{nodeId}")
    public void deleteNode(@Parameter(description = "name of the node which should get deleted") @PathVariable String nodeId) {
        logger.info(String.format("Called: DELETE /api/nodes/%s", nodeId));

        this.nodeService.deleteNode(nodeId);
    }
}
