package com.filesystem.server;

import com.filesystem.server.entities.Node;
import com.filesystem.server.utils.DataClientServiceUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataClientServiceUtilsTest {

    @Test
    @Disabled
    void createNodeStructureFromJsonData_Test() {
        String jsonData = "{\"name\":\"root\", \"childNodes\":[{\"name\": \"childA\", \"childNodes\":[]}, {\"name\": \"childB\", \"childNodes\":[]}]}";

        Node expectedNode = new Node();
        expectedNode.setName("root");

        Node childA = new Node();
        childA.setName("childA");
        childA.setParentNode(expectedNode);
        expectedNode.addChildNode(childA);

        Node childB = new Node();
        childB.setName("childB");
        childB.setParentNode(expectedNode);
        expectedNode.addChildNode(childB);

        assertEquals(expectedNode, DataClientServiceUtils.createNodeStructureFromJsonData(jsonData));
    }
}
