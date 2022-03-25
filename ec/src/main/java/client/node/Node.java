package client.node;

public class Node {

    private String name;
    private String id;
    private String parentId;

    public Node(String name, String id, String parentId) {
        this.name = name;
        this.id = id;
        this.parentId = parentId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.name;
    }
}
