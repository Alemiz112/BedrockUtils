package eu.mizerak.alemiz.bedrockutils.block.creator;

import com.nukkitx.blockstateupdater.BlockStateUpdater;
import com.nukkitx.blockstateupdater.BlockStateUpdater_1_18_10;

import java.util.List;

public class BlockPaletteCreator486 extends BlockPaletteCreator475 {

    @Override
    public List<BlockStateUpdater> getUpdaters() {
        List<BlockStateUpdater> updaters = super.getUpdaters();
        updaters.add(BlockStateUpdater_1_18_10.INSTANCE);
        return updaters;
    }

    @Override
    public String getPaletteFileName() {
        return "block/block_palette_486.nbt";
    }

    @Override
    public int getVersion() {
        return 17959425;
    }
}