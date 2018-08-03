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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Class responsible for loading the icon extractor dll for the operating system.  Dll files packaged in the jar
 * file with this utility must be extracted in order to be used.  This class handles this operation.
 */
public class DllLoader {

    private final static String LIB = System.getProperty("java.io.tmpdir") + File.separator + "SomeGuySoftware" +
            File.separator + "ZenNativeIconEngine" + File.separator + "lib" + File.separator;

    public static void loadDll(String system) {
        if (system.equals("win32")) {
            loadWindowsIconExtractorDll();
        } else {
            // TODO: add other dll loaders here when developed
            System.out.println("No dll loader for system");
        }
    }

    /**
     * Extracts the WindowsIconExtractor.dll file from the resource package to a temp directory on the Windows system
     * when this utility is packaged as a jar file.
     */
    private static void loadWindowsIconExtractorDll() {
        String dllName = "WindowsIconExtractor.dll";
        File windowsDll = new File(LIB + dllName);
        if (! windowsDll.exists()) {
            try {
                InputStream dllFileStream = DllLoader.class.getClassLoader().getResourceAsStream(dllName);
                File outFile = new File(LIB);
                OutputStream out = FileUtils.openOutputStream(outFile);
                IOUtils.copy(dllFileStream, out);
                dllFileStream.close();
                out.close();
                System.load(outFile.toString());
                System.out.println("Dll loaded");
            } catch (IOException e) {
                System.out.println("Failed to load dll");
            }
        } else {
            System.load(windowsDll.toString());
        }
    }

}
