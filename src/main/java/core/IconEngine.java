/*
 *    Copyright 2018 Kyle Hickey
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package core;

import nativeaccess.WindowsIconExtractor;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class IconEngine {

    /**
     * The os type.  Determines which of the native extractors will be used to extract the icon.
     */
    private static String os = getOs();

    /**
     * Determines the host os and returns an os code String to be used in by this class.
     * @return An os code String that indicates the host os.
     */
    private static String getOs() {
        String name = System.getProperty("os.name").toLowerCase();
        if (name.contains("windows")) {
            return "win32";
        } else if (name.contains("mac")) {
            return "mac";
        } else if (name.contains("nix")) {
            return "linux";
        } else {
            throw new UnsupportedOSException("This operating system is not supported: " + os);
        }
    }

    /**
     * Quarries the host file system for the icon the system uses to represent the supplied file/directory and returns
     * a fully drawn JavaFX Image of the icon using the supplied parameters.
     * @param file The file or directory for which the icon is requested.
     * @param requestedWidth The requested width that the returned image should have.
     * @param requestedHeight The requested height that the returned image should have.
     * @param preserveRatio Indicates whether to preserve the aspect ratio of the original icon when scaling the image
     *                      to fit the supplied requested width and height.
     * @param smooth Indicates whether to enable the images smooth scaling option when scaling the icon to fit the
     *               specified requested width and height.
     * @return A JavaFX Image of the icon the system uses to represent the supplied file/directory.  If the supplied
     *         file/directory does not exist or an icon cannot be retrieved from the system, this method returns null;
     * @throws IOException if the BufferedImage returned from the native icon extractor cannot be read.
     */
    public static Image getIconImage(File file, int requestedWidth, int requestedHeight, boolean preserveRatio,
                                     boolean smooth) throws IOException {
        byte[] bytes = getIconByteArray(file);
        if (bytes != null) {
            return new Image(new ByteArrayInputStream(bytes), requestedWidth, requestedHeight, preserveRatio,
                    smooth);
        } else {
            return null;
        }
    }

    /**
     * Quarries the host file system for the icon the system uses to represent the supplied file/directory and returns
     * a fully drawn JavaFX Image of the icon.
     * @param file The file or directory for which the icon is requested.
     * @return A JavaFX Image of the icon the system uses to represent the supplied file/directory.  If the supplied
     *         file/directory does not exist or an icon cannot be retrieved from the system, this method returns null;
     * @throws IOException if the BufferedImage returned from the native icon extractor cannot be read.
     */
    public static Image getIconImage(File file) throws IOException {
        byte[] bytes = getIconByteArray(file);
        if (bytes != null) {
            return new Image(new ByteArrayInputStream(bytes));
        } else {
            return null;
        }
    }

    /**
     * Quarries the host file system for the icon based on the supplied file and returns a byte array of the
     * icon image.
     * @param file The file or directory for which the icon is requested.
     * @return A byte array which contains the icon image bytes used by the system to represent the supplied file.  If
     * the supplied file/directory does not exist or an icon cannot be retrieved from the system, this method returns
     * null.
     * @throws IOException if the BufferedImage returned from the native icon extractor cannot be read.
     */
    public static byte[] getIconByteArray(File file) throws IOException {
        BufferedImage bufferedImage = getIconBufferedImage(file);
        if (bufferedImage != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", out);
            out.flush();
            return out.toByteArray();
        } else {
            return null;
        }
    }

    /**
     * Quarries the file system for the icon based on the supplied file and returns a BufferedImage of the icon image.
     * @param file The file or directory for which an icon is requested.
     * @return A BufferedImage which contains the icon the system uses to represent the supplied file/directory.  If
     * the supplied file/directory does not exist or the icon cannot be retrieved from the system, this method returns
     * null.
     */
    public static BufferedImage getIconBufferedImage(File file) {
        if (file.exists()) {
            switch (os) {
                case "win32":
                    return getIconWin(file.getPath());
                case "mac":
                    return getIconMac(file.getPath());
                case "linux":
                    return getIconLinux(file.getPath());
            }
        }
        return null;
    }

    /**
     * Retrieves an icon from Windows OS which represents the file/directory at the supplied path.
     * @param filePath The path of the file/directory for which an icon is requested.
     * @return A BufferedImage representing the icon used by Windows to represent the file/directory at the supplied
     * path.
     */
    private static BufferedImage getIconWin(String filePath) {
        IconReceiver iconReveiver = new IconReceiver();
        WindowsIconExtractor.getIcon(filePath, iconReveiver);
        return iconReveiver.bufferedImage;
    }

    /**
     * Retrieves an icon from MacOS which represents the file/directory at the supplied path.
     * @param filePath The path of the file/directory for which an icon is requested.
     * @return A BufferedImage representing the icon that MacOS uses to represent the file/directory at the supplied
     * path.
     */
    private static BufferedImage getIconMac(String filePath) {
        return null;
    }

    /**
     * Retrieves an icon from Linux which represents the file/directory at the supplied path.
     * @param filePath The path fo the file/directory for which an icon is requested.
     * @return A BufferedImage representing the icon that Linux uses to represent the file/directory at the supplied
     * path.
     */
    private static BufferedImage getIconLinux(String filePath) {
        return null;
    }

}
