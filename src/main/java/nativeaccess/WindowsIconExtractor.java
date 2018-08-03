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

package nativeaccess;

import com.sun.jna.Callback;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import core.DllLoader;
import core.IconEngine;
import core.IconReceiver;

import java.awt.image.BufferedImage;


/**
 * A class that extracts system icons from windows.  This class relies on the WindowsIconExtractor.dll
 * file for extracting the correct icon from the system.  The icon that is extracted is windows size
 * Jumbo (256x256).
 */
public class WindowsIconExtractor {

    /**
     * Registers the WindowsIconExtractor library so that it may be called into from this class,
     * and the library is able to call back into this class with the extracted HICON.
     */
    static {
        int tries = 0;
        while (tries++ < 3) {
            try {
                Native.register(WindowsIconExtractor.class, "WindowsIconExtractor");
                break;
            } catch (UnsatisfiedLinkError e) {
                DllLoader.loadDll(IconEngine.os);
            }
        }
    }

    public static native void getIcon(String path, Callback callback);

    /**
     * Calls WindowsIconExtractor library to extract the large icon for the supplied file path
     * and supplies it with the callback address to send the extracted icon.
     *
     * @param filePath The path of the file for which an icon is to be extracted.
     */
    public static void getIcon(String filePath, IconReceiver receiver) {
        getIcon(filePath, new Callback() {
            public void callback(WinDef.HICON icon) {
                BufferedImage buffImage = drawIcon(icon);
                User32.INSTANCE.DestroyIcon(icon);
                receiver.bufferedImage = buffImage;
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
