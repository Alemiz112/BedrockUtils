package eu.mizerak.alemiz.bedrockutils.block.creator;

import org.cloudburstmc.blockstateupdater.BlockStateUpdater;
import org.cloudburstmc.blockstateupdater.BlockStateUpdater_1_21_60;

import java.util.List;

public class BlockPaletteCreator776 extends BlockPaletteCreator765 {

    @Override
    public List<BlockStateUpdater> getUpdaters() {
        List<BlockStateUpdater> updaters = super.getUpdaters();
        updaters.add(BlockStateUpdater_1_21_60.INSTANCE);
        return updaters;
    }

    @Override
    public String getPaletteFileName() {
        return "block/block_palette_776.nbt";
    }

    @Override
    public int getVersion() {
        return 18168865;
    }
}
