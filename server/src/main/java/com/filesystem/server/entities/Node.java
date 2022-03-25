package com.filesystem.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mongodb.lang.NonNull;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Document
@Data
@CompoundIndexes({
        @CompoundIndex(name = "name", unique = true, def = "{'name': 1, 'parentNode' : 1, 'childNodes' : 1}")
})
public class Node implements Cloneable {

    private static Logger logger = LoggerFactory.getLogger(Node.class);

    @Id
    public String id;
    @NonNull
    private String name;
    @DocumentReference
    @JsonIgnoreProperties({"childNodes", "parentNode"})
    private List<Node> childNodes = new ArrayList<>();
    @DocumentReference
    @JsonIgnoreProperties({"childNodes"})
    private Node parentNode;
    @DocumentReference
    @JsonIgnore
    private List<DataClientConnection> dataClientConnection = new ArrayList<>();

    public void addChildNode(Node newNode) {
        this.childNodes.add(newNode);
    }

    public void removeChildNode(Node node) {
        this.childNodes.remove(node);
    }

    public void addChildNodes(List<Node> newNodes) {
        for (Node node: newNodes) {
            List<Node> nodeToUpdate = this.childNodes.stream().filter(e -> e.getName().equals(node.getName())).collect(Collectors.toList());
            if (nodeToUpdate.size() == 0)
                this.childNodes.add(node);
        }
    }

    public void addDataClientConnection(DataClientConnection dataClientConnection) {
        if (!this.dataClientConnection.contains(dataClientConnection))
            this.dataClientConnection.add(dataClientConnection);
    }

    public void addDataClientConnections(List<DataClientConnection> dataClientConnections) {
        for (DataClientConnection newDataClientConnection: dataClientConnections) {
            this.addDataClientConnection(newDataClientConnection);
        }
    }

    public void updateChildNodes(Node updateNode) {
        for (Node newChildNode: updateNode.getChildNodes()) {
            if (this.getChildNodes().stream().noneMatch(e -> e.getName().equals(newChildNode.getName()))) {
                this.childNodes.add(newChildNode);
            } else {
                List<Node> existingChildNodes = this.getChildNodes().stream().filter(node -> node.getName().equals(newChildNode.getName())).collect(Collectors.toList());
                for (Node node: existingChildNodes) {
                    node.updateChildNodes(newChildNode);
                }
            }
        }
    }

    public String toDataClientPath() {
        return buildPath(this);
    }

    private String buildPath(Node node) {
        String path = this.getName();

        while (node.getParentNode() != null) {
            node = node.getParentNode();
            path = node.getName() + "/" + path;
        }

        return path;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
