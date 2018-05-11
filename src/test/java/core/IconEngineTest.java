package core;


import javafx.scene.image.Image;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;


public class IconEngineTest {

    @Test
    void getIconImage() throws URISyntaxException, IOException {
        URL resource = IconEngineTest.class.getClassLoader().getResource("test_icon.png");
        File testFile = new File(resource.toURI());

        assertTrue(IconEngine.getIconBufferedImage(testFile) instanceof BufferedImage);
        assertTrue(IconEngine.getIconImage(testFile) instanceof Image);
    }

}
