package filesystem.dataclient.node;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.nio.file.Path;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import filesystem.dataclient.tree.Tree;

@Component
@JsonPropertyOrder({"name", "childNodes"})
public class Node {

    @JsonIgnore
    private File file = null;

    @JsonProperty("childNodes")
    private List<Node> children = new ArrayList<Node>();

    @JsonIgnore
    private Node parent = null;

    
    /** 
     * @return String
     */
    @JsonProperty("name")
    public String serializePath() {
        // uncomment to set name as path instead of just the filename:
        // return Tree.getPath().getRoot().resolve(Tree.getPath().relativize(this.file.toPath())).toString();
        Path path = Tree.getPath().getRoot().resolve(Tree.getPath().relativize(this.file.toPath()));
        if (path.getNameCount() == 0) {
            return path.toString();
        } else {
            return path.getFileName().toString();
        }
    }

    
    /** 
     * @return File
     */
    public File getFile() {
        return this.file;
    }

    
    /** 
     * @param file
     */
    public void setFile(File file) {
        this.file = file;
    }

    
    /** 
     * @return Node
     */
    public Node getParent() {
        return this.parent;
    }

    
    /** 
     * @param parent
     */
    // careful, does not set the child of the parent node
    public void setParent(Node parent) {
        this.parent = parent;
    }

    
    /** 
     * @param file
     * @return Node
     */
    public Node getChild(File file) {
        for (Node child:this.children) {
            if (child.getFile().equals(file)) {
                return child;
            }
        }
        return null;
    }

    
    /** 
     * @param file
     */
    public void addChild(File file) {
        Node child = new Node();
        child.setFile(file);
        child.setParent(this);
        this.children.add(child);
    }

    
    /** 
     * @param node
     */
    public void removeChild(Node node) {
        this.children.remove(node);
    }

    
    /** 
     * @return List<Node>
     */
    public List<Node> getChildren() {
        return children;
    }

    
    /** 
     * @param children
     */
    // careful, does not set the parents of the children
    public void setChildren(List<Node> children) {
        this.children = children;
    }
}
