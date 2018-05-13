package core;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class IconEngineTest {

    @Test
    void getIconImage() throws URISyntaxException, IOException {
        URL resource = IconEngineTest.class.getClassLoader().getResource("test_icon.png");
        File testFile = new File(resource.toURI());
//        System.out.println(String.format("Exists: %s\nPath: %s", testFile.exists(), testFile.getPath()));

        assertNotNull(IconEngine.getIconBufferedImage(testFile));
        assertNotNull(IconEngine.getIconImage(testFile));
    }

    @Test
    void getIconImageNonExistentFileReturn() throws IOException {
        File testFile = new File("C:/Path/To/NoWhere/fakefile.jpg");

        assertNull(IconEngine.getIconBufferedImage(testFile));
        assertNull(IconEngine.getIconImage(testFile));
    }

}
