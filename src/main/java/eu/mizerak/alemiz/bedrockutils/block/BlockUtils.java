package eu.mizerak.alemiz.bedrockutils.block;

import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import eu.mizerak.alemiz.bedrockutils.BedrockUtils;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class BlockUtils {

    public static void main(String[] args) {
        // generateBlockPalette(new BlockPaletteCreator407(), "runtime_block_states_407.dat");
        // generateBlockPalette(new BlockPaletteCreator419(), "runtime_block_states_419.dat");
        // generateBlockPalette(new BlockPaletteCreator428(), "runtime_block_states_428.dat");
        // generateBlockPalette(new BlockPaletteCreator440(), "runtime_block_states_440.dat");

        BlockPaletteCreator blockCreator = new BlockPaletteCreator440();
        blockCreator.saveVanilla(blockCreator.createBlockPalette(), "runtime_block_states_440.nbt");

    }

    public static void generateBlockPalette(BlockPaletteCreator blockCreator, String saveFile) {
        blockCreator.save(blockCreator.createBlockPalette(), saveFile);
    }

    public static void saveCanonicalPalette(String canonicalFile, String saveFile) {
        NbtList<NbtMap> blockStates = (NbtList<NbtMap>) BedrockUtils.loadNetworkCompound(canonicalFile);

        NbtMap blockPalette = NbtMap.builder()
                .putList("blocks", NbtType.COMPOUND, blockStates)
                .build();
        BedrockUtils.saveCompound(blockPalette, saveFile);
    }
}
