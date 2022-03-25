package filesystem.dataclient.node;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.util.Iterator;
import java.nio.file.Files;
import java.nio.charset.Charset;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.io.FileUtils;
import java.util.Queue;
import java.util.ArrayDeque;
import java.io.IOException;
import filesystem.dataclient.tree.Tree;

@Service
public class NodeService {

    private final Tree tree;

    @Autowired
    public NodeService(Tree tree) {
        this.tree = tree;
    }

    
    /** 
     * @param realPath
     * @return Node
     */
    private Node findNode(Path realPath) {
        Queue<Node> queue = new ArrayDeque<Node>();
        queue.add(this.tree.getRoot());
        while(!queue.isEmpty()) {
            Node currentNode = queue.remove();
            if (currentNode.getFile().toPath().equals(realPath)) {
                return currentNode;
            } else {
                queue.addAll(currentNode.getChildren());
            }
        }
        return null;
    }

    
    /** 
     * @param node
     * @throws IOException
     */
    // careful, this removes the file/directory from disk without confirmation
    private void deleteNode(Node node) throws IOException {
        FileUtils.forceDelete(node.getFile());
        node.getParent().removeChild(node);
    }

    
    /** 
     * @param realPath
     * @throws IOException
     */
    public void deleteNode(Path realPath) throws IOException {
        Node node = findNode(realPath);
        deleteNode(node);
    }

    
    /** 
     * @param path
     * @return Node
     * @throws IOException
     */
    public Node createDirectory(Path path) throws IOException {
        Iterator<Path> pathIterator = path.iterator();
        Path directoryPath = Tree.getPath();
        while (pathIterator.hasNext()) {
            directoryPath = Paths.get(directoryPath.toString(), pathIterator.next().toString());
            Node directoryNode = this.findNode(directoryPath);
            if (directoryNode == null) {
                Files.createDirectory(directoryPath);
                File file = directoryPath.toFile();
                Node parent = this.findNode(directoryPath.getParent());
                parent.addChild(file);
            }
        }
        return this.findNode(directoryPath);
    }

    
    /** 
     * @param path
     * @param content
     * @return Node
     * @throws IOException
     */
    public Node createFile(Path path, String content) throws IOException {
        Path realPath = Tree.getRealPath(path);
        this.createDirectory(path.getParent());
        Files.createFile(realPath);
        File file = realPath.toFile();
        FileUtils.writeStringToFile(file, content, Charset.forName("utf-8"));
        Node parent = findNode(realPath.getParent());
        parent.addChild(file);
        return parent.getChild(file);
    }

    
    /** 
     * @param realPath
     * @return String
     * @throws IOException
     */
    public String readFile(Path realPath) throws IOException {
        return Files.readString(realPath, Charset.forName("utf-8"));
    }

    
    /** 
     * @param path
     * @param name
     * @return Node
     * @throws IOException
     */
    public Node renameNode(Path path, String name) throws IOException {
        Path realPath = Tree.getRealPath(path);
        Node node = findNode(realPath);
        File file = node.getFile();
        File newFile = Paths.get(file.getParent(), name).toFile();
        if (newFile.exists()) {
            throw new IOException("File with the path '" + newFile.getPath() + "' already exists");
        }
        file.renameTo(newFile);
        node.setFile(newFile);
        return node;
    }
}
