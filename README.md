# ZenNativeIconEngine
An easy to use package to get native file system icons for supplied file paths.  The icon engine can return an icon as a ready to use JavaFX image object, a byte array, or a BufferedImage object.

The icons returned from the icon engine are large (256x256 - the max available on windows), high quality, and complete icons that also display well on high dpi displays.

## Using Zen Native Icon Engine
Setup and use are as simple as adding the jar to your classpath, and giving the engine an existing file path.

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
        try {
            Image icon = IconEngine.getIconImage(file);
            imageView.setImage(icon);
        } catch (IOException e) {
            // handle exception
        }
    }

}
```

## Support
At the moment the package only supports Windows systems.  Linux support is on the way.  

Contribution from any MacOS developers that are willing and capable of adding Mac support to this project would be greatly appreciated.
