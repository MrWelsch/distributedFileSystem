package com.filesystem.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filesystem.server.entities.DataClientConnection;
import com.filesystem.server.entities.Node;
import com.filesystem.server.services.NodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NodeController.class)
public class NodeControllerTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private NodeService nodeService;

    @Test
    void getAllNodes_No_Nodes_Test() throws Exception {
        List<Node> expectedNodeList = Arrays.asList();
        when(this.nodeService.readAllNodes()).thenReturn(expectedNodeList);

        this.mockMvc.perform(get("/api/nodes"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedNodeList)));
    }

    @Test
    void getAllNodes_Only_Root_Node_Test() throws Exception {
        List<Node> expectedNodeList = createNodeList(0);
        when(this.nodeService.readAllNodes()).thenReturn(expectedNodeList);

        this.mockMvc.perform(get("/api/nodes"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedNodeList)));
    }

    @Test
    void getAllNodes_Root_Node_One_ChildNode_Test() throws Exception {
        List<Node> expectedNodeList = createNodeList(1);
        when(this.nodeService.readAllNodes()).thenReturn(expectedNodeList);

        this.mockMvc.perform(get("/api/nodes"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedNodeList)));
    }

    @Test
    void getAllNodes_Root_Node_Multiple_ChildNode_Test() throws Exception {
        List<Node> expectedNodeList = createNodeList(20);
        when(this.nodeService.readAllNodes()).thenReturn(expectedNodeList);

        this.mockMvc.perform(get("/api/nodes"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedNodeList)));
    }

    @Test
    void getNode_Test() throws Exception {
        Node expectednode = createNode("root", null, null);
        expectednode.setId("1");

        when(this.nodeService.readNode(expectednode.getId())).thenReturn(expectednode);

        this.mockMvc.perform(get(String.format("/api/nodes/%s", expectednode.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectednode)));
    }

    private Node createNode(String name, Node parentNode, List<Node> childNodes) {
        Node node = new Node();
        node.setName(name);
        node.setParentNode(parentNode);
        if (childNodes != null)
            node.setChildNodes(childNodes);

        return node;
    }

    private List<Node> createNodeList(int amount) {
        List<Node> nodeList = new ArrayList<>();

        Node parentNode = createNode("root", null, null);

        for (int i = 1; i <= amount; i++) {
            nodeList.add(createNode(String.format("node-", i), parentNode, null));
        }

        parentNode.setChildNodes(nodeList);

        return nodeList;
    }

}
