package eu.mizerak.alemiz.bedrockutils;

import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtUtils;

import java.io.*;

public class BedrockUtils {

    public static void saveCompound(Object tag, String fileName) {
        File file = new File(System.getProperty("user.dir"), fileName);
        file.getParentFile().mkdirs();
        try (OutputStream stream = new FileOutputStream(file);
             NBTOutputStream outputStream = NbtUtils.createWriter(stream)) {
            outputStream.writeTag(tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveCompoundCompressed(Object tag, String fileName) {
        File file = new File(System.getProperty("user.dir"), fileName);
        file.getParentFile().mkdirs();

        try (OutputStream stream = new FileOutputStream(file);
             NBTOutputStream outputStream = NbtUtils.createGZIPWriter(stream)) {
            outputStream.writeTag(tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object loadNetworkCompound(String fileName) {
        File file = new File(System.getProperty("user.dir"), fileName);
        file.getParentFile().mkdirs();
        try (InputStream stream = new FileInputStream(file);
             NBTInputStream inputStream = NbtUtils.createNetworkReader(stream)) {
            return inputStream.readTag();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveBytes(byte[] bytes, String fileName) {
        File file = new File(System.getProperty("user.dir"), fileName);
        file.getParentFile().mkdirs();
        try (OutputStream stream = new FileOutputStream(file)) {
            stream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
