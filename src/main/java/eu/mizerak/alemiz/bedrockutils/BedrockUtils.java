package eu.mizerak.alemiz.bedrockutils;

import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NBTOutputStream;
import com.nukkitx.nbt.NbtUtils;

import java.io.*;

public class BedrockUtils {

    public static void saveCompound(Object tag, String fileName) {
        File file = new File(System.getProperty("user.dir"), fileName);
        try (OutputStream stream = new FileOutputStream(file);
             NBTOutputStream outputStream = NbtUtils.createWriter(stream)) {
            outputStream.writeTag(tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveCompoundCompressed(Object tag, String fileName) {
        File file = new File(System.getProperty("user.dir"), fileName);
        try (OutputStream stream = new FileOutputStream(file);
             NBTOutputStream outputStream = NbtUtils.createGZIPWriter(stream)) {
            outputStream.writeTag(tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object loadNetworkCompound(String fileName) {
        File file = new File(System.getProperty("user.dir"), fileName);
        try (InputStream stream = new FileInputStream(file);
             NBTInputStream inputStream = NbtUtils.createNetworkReader(stream)) {
            return inputStream.readTag();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
