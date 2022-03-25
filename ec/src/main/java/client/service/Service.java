package client.service;

import client.node.Node;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;

public class Service {

    private List<Node> currentNodes;
    private String parentId;
    private String serverIP = "http://192.168.178.41:8080";
    private HttpClient client = HttpClient.newHttpClient();
    
    // for custom server user-input
    public void setServerIp(String serverIP) {
        this.serverIP = serverIP;
    }

    private Node getNodeFromName(String name) {
        for (Node node : this.currentNodes) {
            String nodeName = node.getName();
            if (nodeName.equals(name)) {
                return node;
            }
        }
        return null;
    }

    private void updateNodes(JSONArray newNodes, String parentId) {
        List<Node> nodes = new ArrayList<Node>();
        this.parentId = parentId;
        for (int i = 0; i < newNodes.length(); i++) {
            JSONObject child = newNodes.getJSONObject(i);
            String childName = child.getString("name");
            String childId = child.getString("id");
            Node childNode = new Node(childName, childId, this.parentId);
            nodes.add(childNode);
        }
        this.currentNodes = nodes;
    }

    private String sendToServer(HttpRequest request) {
        try {
            HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String[] moveDownById(String id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverIP + "/api/nodes/" + id))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            String response = this.sendToServer(request);
            JSONObject jsonBody = new JSONObject(response);
            String parentId = jsonBody.getString("id");
            JSONArray jsonChildren = jsonBody.getJSONArray("childNodes");
            this.updateNodes(jsonChildren, parentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.getCurrentNodes();
    }

    public void initDataclient(String ip, String port, String initPath) {
        JSONObject connection = new JSONObject();
        JSONObject dto = new JSONObject();
        connection.put("ipv4", ip);
        connection.put("port", port);
        dto.put("initPath", initPath);
        dto.put("dataClientConnection", connection);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverIP + "/api/dataclientconnections"))
                    .POST(BodyPublishers.ofString(dto.toString()))
                    .header("Content-Type", "application/json")
                    .build();
            this.sendToServer(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String[] getCurrentNodes() {
        List<String> names = new ArrayList<String>();
        for (Node node : this.currentNodes) {
            names.add(node.getName());
        }
        return names.toArray(String[]::new);
    }

    public String[] getRootNode() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverIP + "/api/nodes/root"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            String response = this.sendToServer(request);
            System.out.println(response.toString());
            JSONArray jsonBody = new JSONArray(response);
            JSONObject jsonRoot = jsonBody.getJSONObject(0);
            String rootId = jsonRoot.getString("id");
            JSONArray JSONChildren = jsonRoot.getJSONArray("childNodes");
            this.updateNodes(JSONChildren, rootId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.getCurrentNodes();
    }

    public String[] searchForNode(String name) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverIP + "/api/nodes/name/" + name))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            String response = this.sendToServer(request);
            System.out.println(response.toString());
            JSONArray jsonBody = new JSONArray(response);
            JSONObject jsonNode = jsonBody.getJSONObject(0);
            String jsonParentId = jsonNode.getJSONObject("parentNode").getString("id");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonNode);
            this.updateNodes(jsonArray, jsonParentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.getCurrentNodes();
    }

    public String[] moveUp() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverIP + "/api/nodes/parent/" + this.parentId))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            String response = this.sendToServer(request);
            JSONObject jsonBody = new JSONObject(response);
            String parentId = jsonBody.getString("id");
            JSONArray jsonChildren = jsonBody.getJSONArray("childNodes");
            this.updateNodes(jsonChildren, parentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.getCurrentNodes();
    }

    public String[] moveDown(String name) {
        Node node = this.getNodeFromName(name);
        if (node == null) {
            return this.getCurrentNodes();
        }
        return this.moveDownById(node.getId());
    }

    // aktuell nur directory, deshalb kein content
    public String[] createNode(String name) {
        JSONObject newNode = new JSONObject();
        newNode.put("name", name);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverIP + "/api/nodes/" + this.parentId + "/child"))
                    .POST(BodyPublishers.ofString(newNode.toString()))
                    .header("Content-Type", "application/json")
                    .build();
            String response = this.sendToServer(request);
            JSONObject jsonBody = new JSONObject(response);
            this.moveDownById(jsonBody.getString("id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.getCurrentNodes();
    }

    public String[] renameNode(String oldName, String newName) {
        Node node = this.getNodeFromName(oldName);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverIP + "/api/nodes/" + node.getId() + "/" + newName))
                    .method("PATCH", BodyPublishers.ofString(""))
                    .header("Content-Type", "application/json")
                    .build();
            this.sendToServer(request);
            node.setName(newName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.getCurrentNodes();
    }

    public String[] deleteNode(String name) {
        Node node = this.getNodeFromName(name);
        System.out.println(node.toString());
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverIP + "/api/nodes/" + node.getId()))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .build();
            this.sendToServer(request);
            System.out.println(request);
            this.currentNodes.remove(node);
            System.out.println(currentNodes.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.getCurrentNodes();
    }

    // for debugging
    public static void main(String[] args) {
        Service service = new Service();
        service.getRootNode();
        //service.initDataclient("172.028.032.143", "54183", "/tmp");
        //System.out.println(Arrays.toString(service.getRootNode()));
        // System.out.println(service.createNode("name"));
       // System.out.println(Arrays.toString(service.createNode("test"))); 
        //System.out.println(Arrays.toString(service.getRootNode()));
        //System.out.println(service.getCurrentNodes());
        service.deleteNode("test2"); 
        //service.renameNode("Test", "test2");
       // System.out.println(service.geTCurrentNodes());
    }

}
