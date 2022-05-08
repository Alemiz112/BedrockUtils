package eu.mizerak.alemiz.bedrockutils.block.custom;

import com.nukkitx.blockstateupdater.BlockStateUpdaters;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.nbt.NbtUtils;
import eu.mizerak.alemiz.bedrockutils.block.BlockPalette;
import eu.mizerak.alemiz.bedrockutils.block.BlockState;
import eu.mizerak.alemiz.bedrockutils.block.comparator.AlphabetPaletteComparator;
import eu.mizerak.alemiz.bedrockutils.block.comparator.HashedPaletteComparator;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@Data
@Log4j2
public class CustomPaletteLoader {

    private final String resourcePath;
    private final int version;
    private final List<NbtMap> vanillaStates = new LinkedList<>();
    private final BlockPalette palette = new BlockPalette();

    private boolean finished;

    public CustomPaletteLoader loadPalette() {
        for (NbtMap state : this.getBlockPalette()) {
            String identifier = state.getString("name");
            BlockState blockState = new BlockState(identifier, -1, (short) -1, state);
            this.palette.getBlockStates().add(blockState);
            this.vanillaStates.add(state);;
        }
        return this;
    }

    public CustomPaletteLoader registerCustomState(NbtMap state) {
        String identifier = state.getString("name");
        BlockState blockState = new BlockState(identifier, -1, (short) -1, state);
        return this.registerCustomState(blockState);
    }

    public CustomPaletteLoader registerCustomState(BlockState state) {
        this.palette.addBlockState(state);
        return this;
    }

    public BlockPalette finish() {
        this.finished = true;

        if (this.version >= 503) {
            return this.palette.sort(HashedPaletteComparator.INSTANCE);
        } else {
            return this.palette.sort(AlphabetPaletteComparator.INSTANCE);
        }
    }

    public boolean compareToVanilla() {
        if (!this.finished) {
            throw new IllegalStateException("Block palette was not generated yet!");
        }
        return this.compareTo(this.palette);
    }

    public boolean compareTo(BlockPalette blockPalette) {
        for (int runtimeId = 0; runtimeId < this.vanillaStates.size(); runtimeId++) {
            if (blockPalette.getBlockStates().size() <= runtimeId) {
                log.info("Provided palette is smaller than vanilla sample!");
                return false;
            }

            NbtMap vanillaState = this.vanillaStates.get(runtimeId);
            BlockState state = blockPalette.getBlockStates().get(runtimeId);
            if (state == null || !state.getBlockState().equals(vanillaState)) {
                return false;
            }
        }

        return true;
    }

    private List<NbtMap> getBlockPalette() {
        try (InputStream stream = BlockStateUpdaters.class.getClassLoader().getResourceAsStream(this.resourcePath)) {
            return ((NbtMap) NbtUtils.createGZIPReader(stream).readTag()).getList("blocks", NbtType.COMPOUND);
        } catch (Exception e) {
            throw new AssertionError("Error loading block palette " + this.resourcePath, e);
        }
    }
}
