package com.filesystem.server.services;

import com.filesystem.server.entities.DataClientConnection;
import com.filesystem.server.entities.Node;
import com.filesystem.server.exceptions.DataClientRestApiException;
import com.filesystem.server.utils.DataClientServiceUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * This class contains all functions to work with the REST-API of a DataClient.
 */
@Service
public class DataClientService {

    private static final Logger logger = LoggerFactory.getLogger(DataClientService.class);

    private final WebClient.Builder webClientBuilder;

    /**
     * Instantiates a new DataClient-Service.
     *
     * @param webClientBuilder a WebClient.Builder to create the WebClient from
     */
    public DataClientService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Initializes a DataClient and returns the Node-Structure provided by the DataClient.
     *
     * @param dataClientConnection DataClientConnection to initialize the DataClient from
     * @param path                 the path to use as Root-Node
     * @return the initialized Node-Structure
     */
    public Node initDataClient(DataClientConnection dataClientConnection, String path) {
        logger.debug(String.format("CALLED: initDataClient() dataClientConnection=%s path=%s", dataClientConnection.getIpv4() + ":" + dataClientConnection.getPort(), path));

        final String endpoint = "/api/tree";

        ResponseEntity<String> response = getWebClientForDataClient(dataClientConnection).post().uri(endpoint).bodyValue(path).retrieve().toEntity(String.class).block();

        if (response.getStatusCodeValue() != 200)
            throw new DataClientRestApiException(endpoint, dataClientConnection);

        return DataClientServiceUtils.createNodeStructureFromJsonData(response.getBody());
    }

    /**
     * Returns the Node-Structure of a DataClient.
     *
     * @param dataClientConnection DataClientConnection to read the Node-Strucutre from
     * @return the Node-Strucutre of the DataClient
     */
    public Node getDataClientStructure(DataClientConnection dataClientConnection) {
        logger.debug(String.format("CALLED: getDataClientStructure() dataClientConnection=%s", dataClientConnection.getIpv4() + ":" + dataClientConnection.getPort()));

        final String endpoint = "/api/tree";

        ResponseEntity<String> response = getWebClientForDataClient(dataClientConnection).get().uri(endpoint).retrieve().toEntity(String.class).block();

        if (response.getStatusCodeValue() != 200)
            throw new DataClientRestApiException(endpoint, dataClientConnection);

        return DataClientServiceUtils.createNodeStructureFromJsonData(response.getBody());
    }

    /**
     * Deletes a Node on a given DataClient.
     *
     * @param dataClientConnection the DataClientConnection to delete the Node from
     * @param path                 the path of the Node to be deleted
     * @return returns true if the request was successful, else false
     */
    public boolean deleteNode(DataClientConnection dataClientConnection, String path) {
        logger.debug(String.format("CALLED: deleteNode() dataClientConnection=%s path=%s", dataClientConnection.getIpv4() + ":" + dataClientConnection.getPort(), path));

        final String endpoint = "/api/node";

        ResponseEntity<Void> response = getWebClientForDataClient(dataClientConnection).method(HttpMethod.DELETE).uri(endpoint).bodyValue(path).retrieve().toEntity(Void.class).block();

        if (response.getStatusCodeValue() != 200)
            throw new DataClientRestApiException(endpoint, dataClientConnection);

        return true;
    }

    /**
     * Create a new Node on a given DataClient.
     *
     * @param dataClientConnection the DataClientConnection to create the Node on
     * @param nodeName             the Node-Name
     * @return the created Node
     */
    public Node createNode(DataClientConnection dataClientConnection, String nodeName) {
        logger.debug(String.format("CALLED: createNode() dataClientConnection=%s nodeName=%s", dataClientConnection.getIpv4() + ":" + dataClientConnection.getPort(), nodeName));

        final String endpoint = "/api/node?type=directory";

        ResponseEntity<String> response = getWebClientForDataClient(dataClientConnection).method(HttpMethod.POST).uri(endpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(String.format("{ \"path\": \"%s\", \"content\": \"\" }", nodeName.substring(2))).retrieve().toEntity(String.class).block();

        if (response.getStatusCodeValue() != 200)
            throw new DataClientRestApiException(endpoint, dataClientConnection);

        return DataClientServiceUtils.createNodeStructureFromJsonData(response.getBody());
    }

    /**
     * Returns a Node-Object for a given Node-Path from the DataClient.
     *
     * @param dataClientConnection the DataClientConnection to read the Node from
     * @param nodePath             the Node-Path to read
     * @return the Node from the DataClient
     */
    public Node getNode(DataClientConnection dataClientConnection, String nodePath) {
        logger.debug(String.format("CALLED: getNode() dataClientConnection=%s nodePath=%s", dataClientConnection.getIpv4() + ":" + dataClientConnection.getPort(), nodePath));

        final String endpoint = "/api/node";

        ResponseEntity<String> response = getWebClientForDataClient(dataClientConnection).method(HttpMethod.GET).uri(endpoint).bodyValue(nodePath).retrieve().toEntity(String.class).block();

        if (response.getStatusCodeValue() != 200)
            throw new DataClientRestApiException(endpoint, dataClientConnection);

        return DataClientServiceUtils.createNodeStructureFromJsonData(response.getBody());
    }

    private WebClient getWebClientForDataClient(DataClientConnection dataClientConnection) {
        logger.debug(String.format("CALLED: getWebClientForDataClient() dataClientConnection=%s ", dataClientConnection.getIpv4() + ":" + dataClientConnection.getPort()));

        return this.webClientBuilder.baseUrl(String.format("http://%s:%s", dataClientConnection.getIpv4(), dataClientConnection.getPort())).build();
    }

    /**
     * Rename a Node on a given DataClient.
     *
     * @param dataClientConnection the DataClientConnection to work with
     * @param nodePath             the Node-Path to the Node to be renamed
     * @param newNodeName          the new Node-Name
     * @return the renamed Node
     */
    public Node renameNode(DataClientConnection dataClientConnection, String nodePath, String newNodeName) {
        logger.debug(String.format("CALLED: renameNode() dataClientConnection=%s nodePath=%s newNodeName=%s", dataClientConnection.getIpv4() + ":" + dataClientConnection.getPort(), nodePath, newNodeName));

        JSONObject requestBody = new JSONObject();
        requestBody.put("path", nodePath);
        requestBody.put("name", newNodeName);

        final String endpoint = "/api/node";

        ResponseEntity<String> response = getWebClientForDataClient(dataClientConnection).method(HttpMethod.PATCH).uri(endpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(requestBody.toString()).retrieve().toEntity(String.class).block();

        if (response.getStatusCodeValue() != 200)
            throw new DataClientRestApiException(endpoint, dataClientConnection);

        return DataClientServiceUtils.createNodeStructureFromJsonData(response.getBody());
    }

}
