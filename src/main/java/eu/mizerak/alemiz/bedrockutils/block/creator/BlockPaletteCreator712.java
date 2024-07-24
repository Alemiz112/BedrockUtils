package eu.mizerak.alemiz.bedrockutils.block.creator;

import org.cloudburstmc.blockstateupdater.BlockStateUpdater;
import org.cloudburstmc.blockstateupdater.BlockStateUpdater_1_21_10;
import org.cloudburstmc.blockstateupdater.BlockStateUpdater_1_21_20;

import java.util.List;

public class BlockPaletteCreator712 extends BlockPaletteCreator685 {

    @Override
    public List<BlockStateUpdater> getUpdaters() {
        List<BlockStateUpdater> updaters = super.getUpdaters();
        updaters.add(BlockStateUpdater_1_21_10.INSTANCE);
        updaters.add(BlockStateUpdater_1_21_20.INSTANCE);
        return updaters;
    }

    @Override
    public String getPaletteFileName() {
        return "block/block_palette_712.nbt";
    }

    @Override
    public int getVersion() {
        return 18158598;
    }
}
