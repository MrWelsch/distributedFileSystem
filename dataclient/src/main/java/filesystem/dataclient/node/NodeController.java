package filesystem.dataclient.node;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import filesystem.dataclient.tree.Tree;

@RestController
@RequestMapping(path = "/api/node")
public class NodeController {

    private final NodeService nodeService;
    private static Logger logger = LoggerFactory.getLogger(NodeController.class);

    @Autowired
    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    
    /** 
     * @param stringPath
     */
    @DeleteMapping
    public void deleteNode(@RequestBody String stringPath) {
        Path path = Paths.get(stringPath);
        logger.info("Called: DELETE /api/node with path: " + path.toString());
        if (!Tree.isCreated()) {
            logger.warn("Error: Tree not yet created");
            throw new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED);
        }
        Path realPath = Tree.getRealPath(path);
        if (Files.notExists(realPath)) {
            logger.warn("Error: File/Directory not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else if (!Files.isWritable(realPath)) {
            logger.warn("Error: No permission to delete file/directory");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        try {
            this.nodeService.deleteNode(realPath);
        } catch(IOException e) {
            logger.warn("Error: " + e.toString());
        }
    }

    
    /** 
     * @param json
     * @param type
     * @return Node
     */
    @PostMapping
    public Node postNode(@RequestBody JsonNode json, @RequestParam String type) {
        logger.info("Called: POST /api/node?type=" + type + " with JSON: " + json.toString());
        if (!Tree.isCreated()) {
            logger.warn("Error: Tree not yet created");
            throw new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED);
        }
        String path = json.get("path").asText();
        if (path == null) {
            logger.warn("Error: No path set");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        String content = json.get("content").asText();
        try {
            if (type.equals("file")) {
                return this.nodeService.createFile(Paths.get(path), content);
            } else if (type.equals("directory")) {
                return this.nodeService.createDirectory(Paths.get(path));
            } else {
                logger.warn("Error: Unknown type (" + type + ")");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } catch(IOException e) {
            logger.warn("Error: " + e.toString());
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    
    /** 
     * @param stringPath
     * @return String
     */
    @GetMapping
    public String getNode(@RequestBody String stringPath) {
        Path path = Paths.get(stringPath);
        logger.info("Called: GET /api/node with path: " + path.toString());
        if (!Tree.isCreated()) {
            logger.warn("Error: Tree not yet created");
            throw new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED);
        }
        Path realPath = Tree.getRealPath(path);
        if (Files.notExists(realPath)) {
            logger.warn("Error: File not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else if (!Files.isRegularFile(realPath)) {
            logger.warn("Error: Path has to be a file");
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } else if (!Files.isReadable(realPath)) {
            logger.warn("Error: No permission to read file");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        try {
            return this.nodeService.readFile(realPath);
        } catch(IOException e) {
            logger.warn("Error: " + e.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    /** 
     * @param json
     * @return Node
     */
    @PatchMapping
    public Node patchNode(@RequestBody JsonNode json) {
        logger.info("Called: PATCH /api/node with JSON: " + json.toString());
        if (!Tree.isCreated()) {
            logger.warn("Error: Tree not yet created");
            throw new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED);
        }
        String path = json.get("path").asText();
        if (path == null) {
            logger.warn("Error: No path set");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        String name = json.get("name").asText();
        if (name == null) {
            logger.warn("Error: No name set");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        try {
            return this.nodeService.renameNode(Paths.get(path), name);
        } catch(IOException e) {
            logger.warn("Error: " + e.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
