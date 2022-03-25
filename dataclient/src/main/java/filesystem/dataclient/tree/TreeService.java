package filesystem.dataclient.tree;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import filesystem.dataclient.node.Node;

@Service
public class TreeService {

    private final Tree tree;

    @Autowired
    public TreeService(Tree tree) {
        this.tree = tree;
    }

    
    /** 
     * @param rootPath
     * @return Tree
     */
    public Tree initTree(Path rootPath) {
        File rootFile = rootPath.toFile();
        Node root = new Node();
        root.setFile(rootFile);
        Tree.setPath(rootPath);
        this.tree.setRoot(root);
        expandNode(root);
        return tree;
    }

    
    /** 
     * @param node
     */
    private void expandNode(Node node) {
        File file = node.getFile();
        if (!Files.isReadable(file.toPath())) {
            return;
        }
        for (File child:file.listFiles()) {
            node.addChild(child);
            if (child.isDirectory()) {
                expandNode(node.getChild(child));
            }
        }
    }
}
