package com.filesystem.server.services;

import com.filesystem.server.entities.DataClientConnection;
import com.filesystem.server.exceptions.DataClientDoesntExistById;
import com.filesystem.server.repositories.DataClientConnectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataClientConnectionServiceTest {

    private DataClientConnectionService dataClientConnectionService;
    @Mock
    private DataClientConnectionRepository dataClientConnectionRepository;

    @BeforeEach
    void initUseCase() {
        dataClientConnectionService = new DataClientConnectionService(dataClientConnectionRepository);
    }

    @Test
    void readDataClientConnectionById_DataClientDoesntExistById_Exception_Test() {
        DataClientConnection expectedDataClientConnection = createDataClientConnection("data-client-1", "111.222.333.444", "8080");
        expectedDataClientConnection.setId("1");

        when(this.dataClientConnectionRepository.findById(expectedDataClientConnection.getId()))
                .thenThrow(new DataClientDoesntExistById(expectedDataClientConnection.getId()));

        assertThrows(DataClientDoesntExistById.class, () -> this.dataClientConnectionService.readDataClientConnectionById(expectedDataClientConnection.getId()));
    }

    @Test
    void readDataClientConnectionById_Exists_Test() {
        DataClientConnection expectedDataClientConnection = createDataClientConnection("data-client-1", "111.222.333.444", "8080");
        expectedDataClientConnection.setId("1");

        when(this.dataClientConnectionRepository.findById(expectedDataClientConnection.getId())).thenReturn(Optional.of(expectedDataClientConnection));

        assertEquals(expectedDataClientConnection, this.dataClientConnectionService.readDataClientConnectionById(expectedDataClientConnection.getId()));
    }

    @Test
    void readAllDataClientConnection_No_DataClients_Test() {
        List<DataClientConnection> expectedDataClientConnectionList = createDataClientConnectionList(0);

        when(this.dataClientConnectionRepository.findAll()).thenReturn(expectedDataClientConnectionList);

        assertEquals(expectedDataClientConnectionList, this.dataClientConnectionService.readAllDataClientConnection());
    }

    @Test
    void readAllDataClientConnection_One_DataClient_Test() {
        List<DataClientConnection> expectedDataClientConnectionList = createDataClientConnectionList(1);

        when(this.dataClientConnectionRepository.findAll()).thenReturn(expectedDataClientConnectionList);

        assertEquals(expectedDataClientConnectionList, this.dataClientConnectionService.readAllDataClientConnection());
    }

    @Test
    void readAllDataClientConnection_Multiple_DataClients_Test() {
        List<DataClientConnection> expectedDataClientConnectionList = createDataClientConnectionList(20);

        when(this.dataClientConnectionRepository.findAll()).thenReturn(expectedDataClientConnectionList);

        assertEquals(expectedDataClientConnectionList, this.dataClientConnectionService.readAllDataClientConnection());
    }

    @Test
    void saveDataClientConnection_IllegalArgumentException_Wrong_Ip_Test() {
        DataClientConnection dataClientConnection = createDataClientConnection("data-client-1","111.222.333.4441","8080");

        assertThrows(IllegalArgumentException.class, () -> this.dataClientConnectionService.saveDataClientConnection(dataClientConnection));
    }

    @Test
    @Disabled
    void saveDataClientConnection_Test() throws CloneNotSupportedException {
        DataClientConnection dataClientConnection = createDataClientConnection("data-client-1","111.222.333.444","8080");
        DataClientConnection expectedDataClientConnection = (DataClientConnection) dataClientConnection.clone();
        expectedDataClientConnection.setId("1");

        when(this.dataClientConnectionRepository.save(dataClientConnection)).thenReturn(expectedDataClientConnection);

        assertEquals(expectedDataClientConnection, this.dataClientConnectionService.saveDataClientConnection(dataClientConnection));
    }

    @Test
    void deleteDataClientConnection_Test() {
        DataClientConnection expectedDataClientConnection = createDataClientConnection("data-client-1","111.222.333.444","8080");
        expectedDataClientConnection.setId("1");

        this.dataClientConnectionService.deleteDataClientConnection(expectedDataClientConnection);

        verify(this.dataClientConnectionRepository).delete(expectedDataClientConnection);
    }

    @Test
    void findDataClientConnectionByIpv4AndPort_Test() {
        DataClientConnection expectedDataClientConnection = createDataClientConnection("data-client-1","111.222.333.444","8080");
        expectedDataClientConnection.setId("1");

        when(this.dataClientConnectionRepository.findDataClientConnectionByIpv4AndPort(expectedDataClientConnection.getIpv4(), expectedDataClientConnection.getPort()))
                .thenReturn(expectedDataClientConnection);

        assertEquals(expectedDataClientConnection, this.dataClientConnectionService.readDataClientConnectionByIpAndPort(expectedDataClientConnection));
    }

    @Test
    void existsDataClientConnectionByIpv4AndPort_True_Test() {
        DataClientConnection expectedDataClientConnection = createDataClientConnection("data-client-1","111.222.333.444","8080");
        expectedDataClientConnection.setId("1");

        when(this.dataClientConnectionRepository.existsDataClientConnectionByIpv4AndPort(expectedDataClientConnection.getIpv4(), expectedDataClientConnection.getPort()))
                .thenReturn(true);

        assertTrue(this.dataClientConnectionService.existsDataClient(expectedDataClientConnection));
    }

    @Test
    void existsDataClientConnectionByIpv4AndPort_False_Test() {
        DataClientConnection expectedDataClientConnection = createDataClientConnection("data-client-1","111.222.333.444","8080");
        expectedDataClientConnection.setId("1");

        when(this.dataClientConnectionRepository.existsDataClientConnectionByIpv4AndPort(expectedDataClientConnection.getIpv4(), expectedDataClientConnection.getPort()))
                .thenReturn(false);

        assertFalse(this.dataClientConnectionService.existsDataClient(expectedDataClientConnection));
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
