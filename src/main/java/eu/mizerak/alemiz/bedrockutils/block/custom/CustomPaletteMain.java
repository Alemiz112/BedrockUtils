package eu.mizerak.alemiz.bedrockutils.block.custom;

import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import eu.mizerak.alemiz.bedrockutils.block.BlockPalette;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomPaletteMain {

    private static final int GAME_VERSION = 503;
    private static final int TEST_VERSION = 17959425;

    public static void main(String[] args) {
        CustomPaletteLoader loader = new CustomPaletteLoader("block/block_palette_" + GAME_VERSION + ".nbt", GAME_VERSION);
        loader.loadPalette();

        loader.registerCustomState(NbtMap.builder()
                .putString("name", "test:nice_block_name")
                .putList("states", NbtType.COMPOUND)
                .putInt("version", TEST_VERSION)
                .build());

        BlockPalette palette = loader.finish();
        boolean equals = loader.compareTo(palette);
        log.info("Loaded palette matches vanilla: {}", equals);
        palette.saveVanilla("custom_palette.nbt");
    }
}
