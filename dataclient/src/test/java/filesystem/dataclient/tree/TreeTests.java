package filesystem.dataclient.tree;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertEquals;

// tests only run on linux! (because of /tmp directory)
@SpringBootTest
class TreeTests {

	private final TreeService treeService;

    @Autowired
    public TreeTests(TreeService treeService) {
        this.treeService = treeService;
    }

	@Test
	public void initializeTree() {
		Path testPath = Paths.get("/tmp");
		Tree testTree = treeService.initTree(testPath);
		assertEquals(testTree.getRoot().getFile().toPath(), testPath);
	}
}
