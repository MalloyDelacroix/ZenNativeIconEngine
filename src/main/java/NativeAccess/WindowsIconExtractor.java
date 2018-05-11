package NativeAccess;

import com.sun.jna.Callback;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import java.awt.image.BufferedImage;


/**
 * A class that extracts system icons from windows.  This class relies on the WindowsIconExtractor.dll
 * file for extracting the correct icon from the system.  The icon that is extracted is windows size
 * Jumbo (256x256).
 *
 * To anyone who uses this application and happens to find themselves looking at this file:
 * I hope that you enjoy the look of the windows icons displayed by this app.  To get here was the
 * result of much hard and trying work and research.  I had to learn more about bitmaps than I want
 * to know, a lot of basic C and C++, a fair amount of the windows api that deals with icons (which is
 * much more than I would have expected), and how to use JNA.  I hope that this work is appreciated when
 * admiring these brilliant icons (with transparent background (another almost weeks worth of work))
 * and the splendor that they bring to your file browser viewing experience.
 */
public class WindowsIconExtractor {

    /**
     * Registers the WindowsIconExtractor library so that it may be called into from this class,
     * and the library is able to call back into this class with the extracted HICON.
     */
    static {
        Native.register(WindowsIconExtractor.class, "WindowsIconExtractor");
    }

    public static native BufferedImage getIcon(String path, Callback callback);

    /**
     * Calls WindowsIconExtractor library to extract the large icon for the supplied file path
     * and supplies it with the callback address to send the extracted icon.
     *
     * @param filePath The path of the file for which an icon is to be extracted.
     */
    public static BufferedImage getIcon(String filePath) {
        return getIcon(filePath, new Callback() {
            public BufferedImage callback(WinDef.HICON icon) {
                BufferedImage buffImage = drawIcon(icon);
                User32.INSTANCE.DestroyIcon(icon);
                return buffImage;
            }
        });
    }

    /**
     * Extracts the bitmap information from the system HICON and draws the pixels into a
     * BufferedImage.
     *
     * @param icon The HICON extracted from the system.
     * @return A BufferedImage containing the byte information to make the icon image.
     */
    private static BufferedImage drawIcon(WinDef.HICON icon) {
        final WinGDI.ICONINFO iconInfo = new WinGDI.ICONINFO();
        User32.INSTANCE.GetIconInfo(icon, iconInfo);
        if (iconInfo.hbmColor == null) {
            return null;
        }

        // This initial bitmap is only used to get width and height information
        WinGDI.BITMAP bitmap = new WinGDI.BITMAP();
        GDI32.INSTANCE.GetObject(iconInfo.hbmColor, bitmap.size(), bitmap.getPointer());
        bitmap.read();

        int width = bitmap.bmWidth.intValue();
        int height = bitmap.bmHeight.intValue();
        short depth = 32;  // supports bitmap with alpha channel

        final byte[] bytes = new byte[width * height * depth / 8];
        final Pointer bytesPtr = new Memory(bytes.length);

        final WinGDI.BITMAPINFO bitmapInfo = new WinGDI.BITMAPINFO();
        WinGDI.BITMAPINFOHEADER header = new WinGDI.BITMAPINFOHEADER();
        bitmapInfo.bmiHeader = header;
        header.biWidth = width;
        header.biHeight = height;
        header.biBitCount = depth;
        header.biPlanes = 1;
        header.biCompression = 0;
        header.write();
        bitmapInfo.write();

        final WinDef.HDC hdc = User32.INSTANCE.GetDC(null);
        GDI32.INSTANCE.GetDIBits(hdc, iconInfo.hbmColor, 0, height, bytesPtr, bitmapInfo, 0);
        bytesPtr.read(0, bytes, 0, bytes.length);
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Draw the individual pixels with alpha values into the BufferedImage
        int r, g, b, a, argb;
        int x = 0;
        int y = height - 1;
        for (int i = 0; i < bytes.length; i = i + 4) {
            b = bytes[i] & 0xFF;
            g = bytes[i + 1] & 0xFF;
            r = bytes[i + 2] & 0xFF;
            a = bytes[i + 3] & 0xFF;
            argb = (a << 24) | (r << 16) | (g << 8) | b;
            image.setRGB(x, y, argb);
            x = (x + 1) % width;
            if (x == 0)
                y--;
        }

        // Clean up the windows items that have been created
        User32.INSTANCE.ReleaseDC(null, hdc);
        GDI32.INSTANCE.DeleteObject(iconInfo.hbmColor);
        if (iconInfo.hbmMask != null) {
            GDI32.INSTANCE.DeleteObject(iconInfo.hbmMask);
        }
        User32.INSTANCE.DestroyIcon(icon);

        return image;
    }
}
