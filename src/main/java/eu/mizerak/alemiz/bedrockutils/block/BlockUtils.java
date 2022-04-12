package eu.mizerak.alemiz.bedrockutils.block;

import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import eu.mizerak.alemiz.bedrockutils.BedrockUtils;
import lombok.extern.log4j.Log4j2;

import java.util.List;


@Log4j2
public class BlockUtils {

    public static void main(String[] args) {
        // generateBlockPalette(new BlockPaletteCreator407(), "runtime_block_states_407.dat");
        // generateBlockPalette(new BlockPaletteCreator419(), "runtime_block_states_419.dat");
        // generateBlockPalette(new BlockPaletteCreator428(), "runtime_block_states_428.dat");
        // generateBlockPalette(new BlockPaletteCreator440(), "runtime_block_states_440.dat");
        // generateBlockPalette(new BlockPaletteCreator448(), "runtime_block_states_448.dat");
        // generateBlockPalette(new BlockPaletteCreator465(), "runtime_block_states_465.dat");
        // generateBlockPalette(new BlockPaletteCreator471(), "runtime_block_states_471.dat");
        // generateBlockPalette(new BlockPaletteCreator475(), "runtime_block_states_475.dat");
        // generateBlockPalette(new BlockPaletteCreator486(), "runtime_block_states_486.dat");
        generateBlockPalette(new BlockPaletteCreator503(), "runtime_block_states_503.dat");

        // compareBlockPalettes(new BlockPaletteCreator503(), new BlockPaletteCreator486(), true);
    }

    public static void generateBlockPalette(BlockPaletteCreator blockCreator, String saveFile) {
        BlockPalette blockPalette = blockCreator.createBlockPalette();
        blockPalette.printUnmatchedStates();
        blockPalette.save(saveFile);
    }

    public static void compareBlockPalettes(BlockPaletteCreator blockCreator, BlockPaletteCreator comparing, boolean findExtraStates) {
        log.info("Comparing {} to {}", blockCreator.getClass().getSimpleName(), comparing.getClass().getSimpleName());
       List<NbtMap> unmatchedStates = blockCreator.compareStatesTo(comparing);
       log.info("Found {} unmatched states!", unmatchedStates.size());
       unmatchedStates.forEach(state -> log.warn("Not matched state: {}", state));

       if (findExtraStates) {
           log.info("Looking for extra states in {}", comparing.getClass().getSimpleName());
           List<NbtMap> extraStates = blockCreator.findExtraStatesIn(comparing);
           log.info("Found {} extra states!", extraStates.size());
           extraStates.forEach(state -> log.warn("Extra state: {}", state));
       }
    }

    public static void saveCanonicalPalette(String canonicalFile, String saveFile) {
        NbtList<NbtMap> blockStates = (NbtList<NbtMap>) BedrockUtils.loadNetworkCompound(canonicalFile);

        NbtMap blockPalette = NbtMap.builder()
                .putList("blocks", NbtType.COMPOUND, blockStates)
                .build();
        BedrockUtils.saveCompound(blockPalette, saveFile);
    }
}
