package com.filesystem.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filesystem.server.entities.DataClientConnection;
import com.filesystem.server.entities.InitDataClientDTO;
import com.filesystem.server.entities.Node;
import com.filesystem.server.services.DataClientConnectionService;
import com.filesystem.server.services.DataClientService;
import com.filesystem.server.services.NodeService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DataClientConnectionController.class)
public class DataClientConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DataClientConnectionService dataClientConnectionService;
    @MockBean
    private NodeService nodeService;
    @MockBean
    private DataClientService dataClientService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAllDataClientConnection_No_DataClient_Test() throws Exception {
        List<DataClientConnection> expectedDataClientConnectionList = Arrays.asList();
        when(this.dataClientConnectionService.readAllDataClientConnection()).thenReturn(expectedDataClientConnectionList);

        this.mockMvc.perform(get("/api/dataclientconnections"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedDataClientConnectionList)));
    }

    @Test
    void getAllDataClientConnection_One_DataClient_Test() throws Exception {
        List<DataClientConnection> expectedDataClientConnectionList = createDataClientConnectionList(1);
        when(this.dataClientConnectionService.readAllDataClientConnection()).thenReturn(expectedDataClientConnectionList);

        this.mockMvc.perform(get("/api/dataclientconnections"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedDataClientConnectionList)));
    }

    @Test
    void getAllDataClientConnection_Multiple_DataClient_Test() throws Exception {
        List<DataClientConnection> expectedDataClientConnectionList = createDataClientConnectionList(20);
        when(this.dataClientConnectionService.readAllDataClientConnection()).thenReturn(expectedDataClientConnectionList);

        this.mockMvc.perform(get("/api/dataclientconnections"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedDataClientConnectionList)));
    }

    @Test
    void getDataClientConnection_Test() throws Exception {
        DataClientConnection expectedDataClientConnection = createDataClientConnection("data-client-1", "111.222.333.444", "8080");
        expectedDataClientConnection.setId("1");

        when(this.dataClientConnectionService.readDataClientConnectionById(expectedDataClientConnection.getId())).thenReturn(expectedDataClientConnection);

        this.mockMvc.perform(get(String.format("/api/dataclientconnections/%s", expectedDataClientConnection.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedDataClientConnection)));
    }

    @Test
    @Disabled
    void postDataClientConnection_DataClient_Doesnt_Exist_Test() throws Exception {
        DataClientConnection actualDataClientConnection = createDataClientConnection("data-client-1", "111.222.333.444", "8080");

        InitDataClientDTO initDataClientDTO = new InitDataClientDTO();
        initDataClientDTO.setDataClientConnection(actualDataClientConnection);
        initDataClientDTO.setInitPath("/home");

        DataClientConnection expectedDataClientConnection = (DataClientConnection) actualDataClientConnection.clone();
        expectedDataClientConnection.setId("1");

        Node newStructure = new Node();
        newStructure.setName("Test");

        when(this.dataClientConnectionService.existsDataClient(actualDataClientConnection)).thenReturn(false);
        when(this.dataClientConnectionService.saveDataClientConnection(actualDataClientConnection)).thenReturn(expectedDataClientConnection);
        when(this.dataClientService.initDataClient(expectedDataClientConnection, initDataClientDTO.getInitPath())).thenReturn(newStructure);

        this.mockMvc.perform(post("/api/dataclientconnections")
                        .content(objectMapper.writeValueAsString(initDataClientDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(expectedDataClientConnection)));

        verify(this.nodeService).initDataStructure(newStructure, expectedDataClientConnection);
    }

    @Test
    @Disabled
    void postDataClientConnection_DataClient_Does_Exist_Test() throws Exception {
        DataClientConnection actualDataClientConnection = createDataClientConnection("data-client-1", "111.222.333.444", "8080");

        InitDataClientDTO initDataClientDTO = new InitDataClientDTO();
        initDataClientDTO.setDataClientConnection(actualDataClientConnection);
        initDataClientDTO.setInitPath("/home");

        DataClientConnection expectedDataClientConnection = (DataClientConnection) actualDataClientConnection.clone();
        expectedDataClientConnection.setId("1");

        Node newStructure = new Node();
        newStructure.setName("Test");

        when(this.dataClientConnectionService.existsDataClient(actualDataClientConnection)).thenReturn(true);
        when(this.dataClientConnectionService.readDataClientConnectionByIpAndPort(actualDataClientConnection)).thenReturn(expectedDataClientConnection);
        when(this.dataClientService.initDataClient(expectedDataClientConnection, initDataClientDTO.getInitPath())).thenReturn(newStructure);

        this.mockMvc.perform(post("/api/dataclientconnections")
                        .content(objectMapper.writeValueAsString(initDataClientDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(expectedDataClientConnection)));

        verify(this.nodeService).initDataStructure(newStructure, expectedDataClientConnection);
    }

    @Test
    void deleteDataClientConnection_Test() throws Exception {
        DataClientConnection expectedDataClientConnection = createDataClientConnection("data-client-1", "111.222.333.444", "8080");
        expectedDataClientConnection.setId("1");

        when(this.dataClientConnectionService.readDataClientConnectionById(expectedDataClientConnection.getId())).thenReturn(expectedDataClientConnection);

        this.mockMvc.perform(delete(String.format("/api/dataclientconnections/%s", expectedDataClientConnection.getId())))
                .andExpect(status().isOk());

        verify(this.nodeService).deleteNodesForDataClientConnection(expectedDataClientConnection);
        verify(this.dataClientConnectionService).deleteDataClientConnection(expectedDataClientConnection);
    }

    private DataClientConnection createDataClientConnection(String name, String ipv4, String port) {
        DataClientConnection dataClientConnection = new DataClientConnection();
        dataClientConnection.setName(name);
        dataClientConnection.setIpv4(ipv4);
        dataClientConnection.setPort(port);

        return dataClientConnection;
    }

    private List<DataClientConnection> createDataClientConnectionList(int amount) {
        List<DataClientConnection> dataClientConnectionList = new ArrayList<>();

        for (int i = 1; i <= amount; i++) {
            dataClientConnectionList.add(createDataClientConnection(String.format("data-client-%s", amount), "111.222.333.444", "8080"));
        }
        return dataClientConnectionList;
    }

}
