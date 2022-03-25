package filesystem.dataclient.tree;

import org.springframework.stereotype.Component;
import filesystem.dataclient.node.Node;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class Tree {

    @JsonIgnore
    private static Path path;

    @JsonUnwrapped
    private Node root;

    public Tree(Node root) {
        this.root = root;
    }

    
    /** 
     * @return Node
     */
    public Node getRoot() {
        return this.root;
    }

    
    /** 
     * @param root
     */
    public void setRoot(Node root) {
        this.root = root;
    }

    
    /** 
     * @return Path
     */
    public static Path getPath() {
        return Tree.path;
    }

    
    /** 
     * @param path
     */
    public static void setPath(Path path) {
        Tree.path = path;
    }

    
    /** 
     * @param path
     * @return Path
     */
    public static Path getRealPath(Path path) {
        return Paths.get(Tree.getPath().toString(), path.toString());
    }

    
    /** 
     * @return boolean
     */
    public static boolean isCreated() {
        if (Tree.path != null) {
            return true;
        } else {
            return false;
        }
    }
}
