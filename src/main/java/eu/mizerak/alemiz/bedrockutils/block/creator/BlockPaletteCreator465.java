package eu.mizerak.alemiz.bedrockutils.block.creator;

import com.nukkitx.blockstateupdater.BlockStateUpdater;
import com.nukkitx.blockstateupdater.BlockStateUpdater_1_17_30;

import java.util.List;

public class BlockPaletteCreator465 extends BlockPaletteCreator448 {

    @Override
    public List<BlockStateUpdater> getUpdaters() {
        List<BlockStateUpdater> updaters = super.getUpdaters();
        updaters.add(BlockStateUpdater_1_17_30.INSTANCE);
        return updaters;
    }

    @Override
    public String getPaletteFileName() {
        return "block/block_palette_465.nbt";
    }
}
