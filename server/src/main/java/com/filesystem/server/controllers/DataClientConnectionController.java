package com.filesystem.server.controllers;

import com.filesystem.server.entities.DataClientConnection;
import com.filesystem.server.entities.InitDataClientDTO;
import com.filesystem.server.entities.Node;
import com.filesystem.server.services.DataClientConnectionService;
import com.filesystem.server.services.DataClientService;
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

import java.util.List;

/**
 * Controller class to describe all endpoints regarding the DataClientConnections used by the virtual filesystem.
 */
@RestController
@RequestMapping(path = "/api/dataclientconnections")
public class DataClientConnectionController {

    private static final Logger logger = LoggerFactory.getLogger(DataClientConnectionController.class);

    private final DataClientConnectionService dataClientConnectionService;
    private final NodeService nodeService;
    private final DataClientService dataClientService;

    /**
     * Instantiates a new DataClientConnection-Controller and autowires the needed beans to work properly.
     *
     * @param dataClientConnectionService the DataClientConnection-Service
     * @param nodeService                 the Node-Service
     * @param dataClientService           the DataClient-Service
     */
    @Autowired
    public DataClientConnectionController(DataClientConnectionService dataClientConnectionService, NodeService nodeService, DataClientService dataClientService) {
        this.dataClientConnectionService = dataClientConnectionService;
        this.nodeService = nodeService;
        this.dataClientService = dataClientService;
    }

    /**
     * Post-mapping which creates and instantiates a new DataClientConnection by a given InitDataClientDTO which contains the information
     * the DataClientConnection and which path to initialize the structure with.
     *
     * @param initDataClientDTO the InitDataClient to instantiate and store
     * @return the stored DataClientConnection
     */
    @Operation(summary = "Creates a new Dataclient Connection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the stored information about the new Dataclient Connection",
                    content = {@Content(mediaType = "application/json")})
    })
    @PostMapping(value = "/{initDataClientDTO}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataClientConnection postDataClientConnection(@Parameter(description = "The new Dataclient Connection") @RequestBody InitDataClientDTO initDataClientDTO) {
        logger.info(String.format("Called: POST /api/dataclientconnections initDataClientDTO=%s", initDataClientDTO));

        DataClientConnection dataClientConnection = initDataClientDTO.getDataClientConnection();

        if (this.dataClientConnectionService.existsDataClient(dataClientConnection)) {
            dataClientConnection = this.dataClientConnectionService.readDataClientConnectionByIpAndPort(initDataClientDTO.getDataClientConnection());
        } else {
            dataClientConnection = this.dataClientConnectionService.saveDataClientConnection(dataClientConnection);
        }

        Node newStructure = this.dataClientService.initDataClient(dataClientConnection, initDataClientDTO.getInitPath());

        this.nodeService.initDataStructure(newStructure, dataClientConnection);

        return dataClientConnection;
    }

     /**
     * Get-mapping which returns the node which was referenced by the endpoints PathVariable.
     *
     * @param dataClientId the DataClient-Id to be returned
     * @return the corresponding DataClientConnection
     */
    @Operation(summary = "Returns Dataclient Connection referenced by the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the stored information about the Dataclient Connection",
                    content = {@Content(mediaType = "application/json")})
    })
    @GetMapping(value = "/{dataClientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataClientConnection getDataClientConnection(@Parameter(description = "id of the Dataclient Connection to be searched") @PathVariable String dataClientId) {
        logger.info(String.format("Called: GET /api/dataclientconnections/%s",dataClientId));

        return this.dataClientConnectionService.readDataClientConnectionById(dataClientId);
    }

    /**
     * Get-mapping which returns all stored DataClientConnections.
     *
     * @return all DataClientConnections
     */
    @Operation(summary = "Returns all Dataclient Connections")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the stored information about the Dataclient Connections",
                    content = {@Content(mediaType = "application/json")})
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DataClientConnection> getAllDataClientConnection() {
        logger.info("Called: GET /api/dataclientconnections");

        return this.dataClientConnectionService.readAllDataClientConnection();
    }

    /**
     * Delete-mapping which deletes a DataClientConnection by a given DataClientConnection-Id (referenced via PathVariable).
     *
     * @param dataClientId the DataClientConnection-Id
     */
    @Operation(summary = "Deletes Dataclient Connection referenced by the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Dataclient Connection with all nodes which are referenced only to this Dataclient Connection get deleted",
                    content = {@Content(mediaType = "application/json")})
    })
    @DeleteMapping(value = "/{dataClientId}")
    public void deleteDataClientConnection(@Parameter(description = "id of the Dataclient Connection to be deleted") @PathVariable String dataClientId) {
        logger.info(String.format("Called: DELETE /api/dataclientconnections/%s", dataClientId));

        DataClientConnection dataClientConnection = this.dataClientConnectionService.readDataClientConnectionById(dataClientId);

        this.nodeService.deleteNodesForDataClientConnection(dataClientConnection);
        this.dataClientConnectionService.deleteDataClientConnection(dataClientConnection);
    }

}
