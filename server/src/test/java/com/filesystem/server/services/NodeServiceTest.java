package com.filesystem.server.services;

import com.filesystem.server.entities.DataClientConnection;
import com.filesystem.server.entities.Node;
import com.filesystem.server.exceptions.NodeDoesntExistById;
import com.filesystem.server.repositories.NodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NodeServiceTest {

    private NodeService nodeService;
    @Mock
    private NodeRepository nodeRepository;
    @Mock
    private DataClientService dataClientService;

    @BeforeEach
    void initUseCase() {
        nodeService = new NodeService(nodeRepository, dataClientService);
    }

    @Test
    void updateNodeName_NodeDoesntExistById_Exception_Test() {
        Node expectedNode = createNode("root", null, null);
        expectedNode.setId("1");
        String newName = "newRootName";

        when(this.nodeRepository.findById(expectedNode.getId())).thenThrow(new NodeDoesntExistById(expectedNode.getId()));

        assertThrows(NodeDoesntExistById.class, () -> this.nodeService.updateNodeName(expectedNode.getId(), newName));
    }

    @Test
    void updateNodeName_Test() throws CloneNotSupportedException {
        Node actualNode = createNode("root", null, null);
        actualNode.setId("1");

        Node expectedNode = (Node) actualNode.clone();
        expectedNode.setName("newRootName");

        when(this.nodeRepository.findById(actualNode.getId())).thenReturn(Optional.of(actualNode));
        when(this.nodeRepository.save(expectedNode)).thenReturn(expectedNode);

        assertEquals(expectedNode, this.nodeService.updateNodeName(actualNode.getId(), expectedNode.getName()));
    }

    private Node createNode(String name, Node parentNode, List<Node> childNodes) {
        Node node = new Node();
        node.setName(name);
        node.setParentNode(parentNode);
        if (childNodes != null)
            node.setChildNodes(childNodes);

        return node;
    }

    private Node createNode(String name, Node parentNode, List<Node> childNodes, DataClientConnection dataClientConnection) {
        Node node = new Node();
        node.setName(name);
        node.setParentNode(parentNode);
        if (childNodes != null)
            node.setChildNodes(childNodes);
        node.addDataClientConnection(dataClientConnection);

        return node;
    }

    private List<Node> createNodeList(int amount, Node parentNode) {
        List<Node> nodeList = new ArrayList<>();

        for (int i = 1; i <= amount; i++) {
            nodeList.add(createNode(String.format("node-%s", i), parentNode, null));
        }

        return nodeList;
    }

    private List<Node> createNodeList(int amount, Node parentNode, DataClientConnection dataClientConnection) {
        List<Node> nodeList = new ArrayList<>();

        for (int i = 1; i <= amount; i++) {
            nodeList.add(createNode(String.format("node-%s", i), parentNode, null, dataClientConnection));
        }

        return nodeList;
    }

}
