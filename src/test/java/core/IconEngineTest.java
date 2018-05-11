package core;

import NativeAccess.WindowsIconExtractor;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


public class IconEngineTest {

    @Test
    void getIconImage() throws URISyntaxException, IOException {
        URL resource = IconEngineTest.class.getClassLoader().getResource("test_icon.png");
        File testFile = new File(resource.toURI());
        WindowsIconExtractor mockExtractor = mock(WindowsIconExtractor.class);
        when(mockExtractor.getIcon(testFile.getPath())).thenReturn(ImageIO.read(testFile));


    }

}
