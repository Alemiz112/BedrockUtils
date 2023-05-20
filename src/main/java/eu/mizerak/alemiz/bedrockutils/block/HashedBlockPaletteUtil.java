package eu.mizerak.alemiz.bedrockutils.block;

import org.cloudburstmc.nbt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.TreeMap;

public class HashedBlockPaletteUtil {

    private static final int FNV1_32_INIT = 0x811c9dc5;
    private static final int FNV1_PRIME_32 = 0x01000193;

    public static void main(String[] args) throws Exception {
        NbtMap blockState = NbtMap.builder()
                .putString("name", "minecraft:coral_fan")
                .putCompound("states", NbtMap.builder()
                        .putString("coral_color", "blue")
                        .putInt("coral_fan_direction", 0)
                        .build())
                .build();

        System.out.printf("hash: %s%n", createHash(blockState));

        /*List<NbtMap> palette = new BlockPaletteCreator582().getBlockPalette();
        for (NbtMap block : palette) {
            int mojandId = block.getInt("network_id");
            int hash = createHash(block);
            String name = block.getString("name");

            if (mojandId != hash) {
                System.out.println(name + ": hash=" + hash + " mojangHash=" + mojandId);
                System.out.println(block);
            }
        }*/
    }

    public static int createHash(NbtMap block) {
        if (block.getString("name").equals("minecraft:unknown")) {
            return -2; // This is special case
        }

        NbtMap tag = NbtMap.builder()
                .putString("name", block.getString("name"))
                .putCompound("states", NbtMap.fromMap(
                        new TreeMap<>(block.getCompound("states"))))
                .build();

        byte[] bytes;
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             NBTOutputStream outputStream = NbtUtils.createWriterLE(stream)) {
            outputStream.writeTag(tag);
            bytes = stream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fnv1a_32(bytes);
    }

    public static int fnv1a_32(byte[] data) {
        int hash = FNV1_32_INIT;
        for (byte datum : data) {
            hash ^= (datum & 0xff);
            hash *= FNV1_PRIME_32;
        }
        return hash;
    }
}
