package nativeaccess;

import NativeAccess.WindowsIconExtractor;
import core.IconEngineTest;
import core.IconReceiver;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;


public class WindowsIconExtractorTest {

    @Test
    void getIconTest() throws URISyntaxException {
        URL resource = IconEngineTest.class.getClassLoader().getResource("test_icon.png");
        File testFile = new File(resource.toURI());

        IconReceiver iconReceiver = new IconReceiver();
        WindowsIconExtractor.getIcon(testFile.getPath(), iconReceiver);
        assertNotNull(iconReceiver.bufferedImage);
    }

}
