package eu.mizerak.alemiz.bedrockutils.block.creator;

import org.cloudburstmc.blockstateupdater.BlockStateUpdater;
import org.cloudburstmc.blockstateupdater.BlockStateUpdater_1_20_70;

import java.util.List;

public class BlockPaletteCreator662 extends BlockPaletteCreator649 {

    @Override
    public List<BlockStateUpdater> getUpdaters() {
        List<BlockStateUpdater> updaters = super.getUpdaters();
        updaters.add(BlockStateUpdater_1_20_70.INSTANCE);
        return updaters;
    }

    @Override
    public String getPaletteFileName() {
        return "block/block_palette_662.nbt";
    }

    @Override
    public int getVersion() {
        return 18105860;
    }
}
