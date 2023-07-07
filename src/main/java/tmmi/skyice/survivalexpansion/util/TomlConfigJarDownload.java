package tmmi.skyice.survivalexpansion.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class TomlConfigJarDownload {
    public static final String resourcePath= "mods/toml-config-1.0.2.jar";
    public static final String modPath = "mods/fabric-toml-config-1.0.2.jar";
    public static final String tempModPath = "mods/fabric-toml-config-1.0.2.temp";

    public static void Download() {
        if (new File(modPath).exists()) {
            return;
        }
        LogUtil.info("-------------------------------------------------");
        LogUtil.info("|  Downloading toml-config-1.0.2.jar............. |");
        LogUtil.info("-------------------------------------------------");
        String resultUrl = "https://github.com/Fndream/toml-config/releases/download/v1.0.2/toml-config-1.0.2.jar";
        try {
            URL url = new URL(resultUrl);
            URLConnection urlConnection = url.openConnection();

            BufferedInputStream input = new BufferedInputStream(urlConnection.getInputStream());
            BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(resourcePath));
            try (input; writer) {
                byte[] bytes = input.readAllBytes();
                writer.write(bytes);
                writer.flush();
            }
            addFabricModJson();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LogUtil.warn("Download toml-config-1.0.2.jar successfully! Please restart the server!");
        System.exit(0);
    }

    private static void addFabricModJson() throws IOException {
        File file = new File(resourcePath);
        if (!file.exists()) {
            return;
        }

        JarFile jarFile = new JarFile(file);
        File tempJarFile = new File(tempModPath);
        try (jarFile) {
            JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(tempJarFile));
            try (jarOut) {
                JarEntry fabricModJsonEntry = new JarEntry("fabric.mod.json");
                jarOut.putNextEntry(fabricModJsonEntry);

                String json = """
                        {
                          "schemaVersion": 1,
                          "id": "tomlconfig",
                          "version": "1.0",
                          "name": "Toml Config"
                        }""";
                jarOut.write(json.getBytes());

                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    jarOut.putNextEntry(jarEntry);
                    InputStream inputStream = jarFile.getInputStream(jarEntry);
                    try (inputStream) {
                        byte[] bytes = inputStream.readAllBytes();
                        jarOut.write(bytes);
                    }
                }
            }
        }
        file.delete();
        tempJarFile.renameTo(new File(modPath));
    }

}
