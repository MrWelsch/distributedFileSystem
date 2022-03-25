package com.filesystem.server.utils;

import com.filesystem.server.entities.Node;
import org.json.JSONObject;

/**
 * This class provide utility functions to parse and create Node-Entities from given JSON data.
 */
public final class DataClientServiceUtils {

    private DataClientServiceUtils() {
    }

    /**
     * Create Node-Structure from given JSON data.
     *
     * @param jsonData the String containing the JSON data
     * @return the Node containing the parsed JSON data
     */
    public static Node createNodeStructureFromJsonData(String jsonData) {
        return createChildNodeStructureFromJsonData(new JSONObject(jsonData));
    }

    private static Node createChildNodeStructureFromJsonData(JSONObject nodeData) {
        Node node = new Node();
        node.setName(nodeData.getString("name"));

        for (int i = 0; i < nodeData.getJSONArray("childNodes").length(); i++) {
            Node childNode  = createChildNodeStructureFromJsonData(nodeData.getJSONArray("childNodes").getJSONObject(i));
            node.addChildNode(childNode);
            childNode.setParentNode(node);
        }

        return node;
    }
}
