package filesystem.dataclient.tree;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping(path = "/api/tree")
public class TreeController {

    private Tree tree;
    private final TreeService treeService;
    private static Logger logger = LoggerFactory.getLogger(TreeController.class);

    @Autowired
    public TreeController(Tree tree, TreeService treeService) {
        this.tree = tree;
        this.treeService = treeService;
    }

    
    /** 
     * @param stringPath
     * @return Tree
     */
    @PostMapping
    public Tree postTree(@RequestBody String stringPath) {
        Path rootPath = Paths.get(stringPath);
        logger.info("Called: POST /api/tree with path: " + rootPath.toString());
        try {
            if (Files.notExists(rootPath)) {
                logger.warn("Error: Directory not found");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            } else if (!Files.isDirectory(rootPath)) {
                logger.warn("Error: Path has to be a directory");
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
            } else if (!Files.isReadable(rootPath)) {
                logger.warn("Error: No permission to read directory");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } catch(SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        this.tree = this.treeService.initTree(rootPath);
        return this.tree;
    }

    
    /** 
     * @return Tree
     */
    @GetMapping
    public Tree getTree() {
        logger.info("Called: GET /api/tree");
        if (!Tree.isCreated()) {
            logger.warn("Error: Tree not initialized yet");
            throw new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED);
        }
        return this.tree;
    }
}
