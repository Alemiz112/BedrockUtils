package eu.mizerak.alemiz.bedrockutils.block;

import eu.mizerak.alemiz.bedrockutils.block.state.BlockState;
import org.cloudburstmc.nbt.NbtList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import eu.mizerak.alemiz.bedrockutils.BedrockUtils;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
@Data
public class BlockPalette {
    private final List<BlockState> blockStates = new ArrayList<>();
    private final List<BlockState> unmatchedStates = new ArrayList<>();

    public void addBlockState(BlockState blockState) {
        if (!blockStates.contains(blockState)) {
            this.blockStates.add(blockState);
        }
    }

    public void save(String saveFile) {
        List<NbtMap> nbtStates = new ArrayList<>();
        this.blockStates.forEach(state -> nbtStates.add(state.getBlockState()));
        NbtList<NbtMap> blockPalette = new NbtList<>(NbtType.COMPOUND, nbtStates);
        BedrockUtils.saveCompoundCompressed(blockPalette, saveFile);
    }

    public void saveVanilla(String saveFile) {
        List<NbtMap> nbtStates = new ArrayList<>();
        this.blockStates.forEach(state -> nbtStates.add(state.getBlockState()));

        NbtMap blockPalette = NbtMap.builder()
                .putList("blocks", NbtType.COMPOUND, nbtStates)
                .build();
        BedrockUtils.saveCompoundCompressed(blockPalette, saveFile);
    }

    public BlockPalette sort(Comparator<BlockState> comparator) {
        this.blockStates.sort(comparator);
        return this;
    }

    /*public BlockPalette compareTo(BlockPalette compareTo) {
        BlockPalette mergedPalette = new BlockPalette();

        List<BlockState> states = new ArrayList<>(this.blockStates);
        states.addAll(this.unmatchedStates);

        for (BlockState currentState : states) {
            boolean contains = compareTo.blockStates.contains(currentState) ||
                    compareTo.unmatchedStates.contains(currentState);
            if (contains) {
                mergedPalette.blockStates.add(currentState);
            } else {
                mergedPalette.unmatchedStates.add(currentState);
            }
        }
        return mergedPalette;
    }*/

    public BlockPalette printUnmatchedStates() {
        for (BlockState state : this.unmatchedStates) {
            log.warn("Unmatched state: {}", state.getBlockState() == null ? state.getIdentifier() : state.getBlockState());
        }
        log.warn("Total unmatched states: {}", this.unmatchedStates.size());
        return this;
    }


    public BlockPalette printStates() {
        for (BlockState state : this.blockStates) {
            log.info(state);
        }
        return this;
    }
}
