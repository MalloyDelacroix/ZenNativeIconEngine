# ZenNativeIconEngine
A utility intended for javafx projects to get native file system icons for supplied file paths.  The icon engine can return a native icon as a JavaFX Image, a byte array, or a BufferedImage.  All that is needed is a path to an existing file or directory.  

The icons returned from the icon engine are large (256x256 - the max available on windows), good quality, and complete icons.  Icons also work for high dpi displays.

## Using Zen Native Icon Engine
To use the icon engine, package the project into a jar file (or grab a prebuilt jar file from the release section) and add it to your projects class path.  

Below is a usage example:

```java
import core.IconEngine;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;


public class Controller {

    @FXML
    private ImageView imageView;

    @FXML
    void initialize() {
        File file = new File("C:\\Program Files\\JetBrains\\IntelliJ IDEA 2018.2\\bin\\idea.exe");
        imageView.setImage(loadImage(file));
    }

    private Image loadImage(File file) {
        try {
            return IconEngine.getIconImage(file);
        } catch (IOException e) {
            // handle exception
            return null;
        }
    }

}
```

## Support
At the moment the package only supports Windows systems.  Linux support is on the way.  

Contribution from any MacOS developers that are willing and capable of adding Mac support to this project would be greatly appreciated.
