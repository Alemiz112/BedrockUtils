package eu.mizerak.alemiz.bedrockutils.block.creator;

import org.cloudburstmc.blockstateupdater.BlockStateUpdater;
import org.cloudburstmc.blockstateupdater.BlockStateUpdater_1_20_50;

import java.util.List;

public class BlockPaletteCreator630 extends BlockPaletteCreator622 {

    @Override
    public List<BlockStateUpdater> getUpdaters() {
        List<BlockStateUpdater> updaters = super.getUpdaters();
        updaters.add(BlockStateUpdater_1_20_50.INSTANCE);
        return updaters;
    }

    @Override
    public String getPaletteFileName() {
        return "block/block_palette_630.nbt";
    }

    @Override
    public int getVersion() {
        return 18100737;
    }
}