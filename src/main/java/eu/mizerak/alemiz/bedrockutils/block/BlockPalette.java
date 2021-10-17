package eu.mizerak.alemiz.bedrockutils.block;

import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import eu.mizerak.alemiz.bedrockutils.BedrockUtils;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Log4j2
@Data
public class BlockPalette {
    private final List<NbtMap> blockStates = new ArrayList<>();
    private final List<NbtMap> unmatchedStates = new ArrayList<>();

    public void save(String saveFile) {
        NbtList<NbtMap> blockPalette = new NbtList<>(NbtType.COMPOUND, this.blockStates);
        BedrockUtils.saveCompound(blockPalette, saveFile);
    }

    public void saveVanilla(String saveFile) {
        NbtMap blockPalette = NbtMap.builder()
                .putList("blocks", NbtType.COMPOUND, this.blockStates)
                .build();
        BedrockUtils.saveCompound(blockPalette, saveFile);
    }

    public BlockPalette sort(Comparator<NbtMap> comparator) {
        this.blockStates.sort(comparator);
        return this;
    }

    public BlockPalette compareTo(BlockPalette compareTo) {
        BlockPalette mergedPalette = new BlockPalette();

        List<NbtMap> states = new ArrayList<>(this.blockStates);
        states.addAll(this.unmatchedStates);

        for (NbtMap currentState : states) {
            boolean contains = compareTo.blockStates.contains(currentState) ||
                    compareTo.unmatchedStates.contains(currentState);
            if (contains) {
                mergedPalette.blockStates.add(currentState);
            } else {
                mergedPalette.unmatchedStates.add(currentState);
            }
        }
        return mergedPalette;
    }

    public BlockPalette printUnmatchedStates() {
        for (NbtMap state : this.unmatchedStates) {
            log.warn("Unmatched state: " + state);
        }
        return this;
    }


    public BlockPalette printStates() {
        for (NbtMap state : this.blockStates) {
            log.info(state);
        }
        return this;
    }
}
