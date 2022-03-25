package filesystem.dataclient.node;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import filesystem.dataclient.tree.TreeService;

// tests only run on linux! (because of /tmp directory)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class NodeTests {

    private final NodeService nodeService;
    private final TreeService treeService;

    @Autowired
    public NodeTests(NodeService nodeService, TreeService treeService) {
        this.nodeService = nodeService;
        this.treeService = treeService;
    }

    @BeforeAll
    public void setUp() {
        this.treeService.initTree(Paths.get("/tmp"));
    }

    @AfterAll
    public void cleanUp() throws IOException {
        this.nodeService.deleteNode(Paths.get("/tmp/dataclient_tests"));
    }

    @Test
    @Order(1)
    public void createDirectory() throws IOException {
        Node dir0 = this.nodeService.createDirectory(Paths.get("/dataclient_tests"));
        assertEquals(Paths.get("/tmp/dataclient_tests").toFile(), dir0.getFile());
    }

    @Test
    @Order(2)
    public void createDirectories() throws IOException {
        Node dir3 = this.nodeService.createDirectory(Paths.get("/dataclient_tests/dir1/dir2/dir3"));
        assertEquals(Paths.get("/tmp/dataclient_tests/dir1/dir2/dir3").toFile(), dir3.getFile());
    }

    @Test
    @Order(3)
    public void createFile() throws IOException {
        Node file1 = this.nodeService.createFile(Paths.get("/dataclient_tests/file1"), "this is a test file");
        assertEquals(Paths.get("/tmp/dataclient_tests/file1").toFile(), file1.getFile());
    }

    @Test
    @Order(4)
    public void readFile() throws IOException {
        String content1 = this.nodeService.readFile(Paths.get("/tmp/dataclient_tests/file1"));
        assertEquals("this is a test file", content1);
    }

    @Test
    @Order(5)
    public void deleteFile() throws IOException {
        Path file1Path = Paths.get("/tmp/dataclient_tests/file1");
        this.nodeService.deleteNode(file1Path);
        assertTrue(Files.notExists(file1Path));
    }
}
