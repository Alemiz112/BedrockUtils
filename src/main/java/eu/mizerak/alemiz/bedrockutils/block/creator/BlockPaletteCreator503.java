package eu.mizerak.alemiz.bedrockutils.block.creator;

import com.nukkitx.blockstateupdater.BlockStateUpdater;
import com.nukkitx.blockstateupdater.BlockStateUpdater_1_18_30;

import java.util.List;

public class BlockPaletteCreator503 extends BlockPaletteCreator486 {

    @Override
    public List<BlockStateUpdater> getUpdaters() {
        List<BlockStateUpdater> updaters = super.getUpdaters();
        updaters.add(BlockStateUpdater_1_18_30.INSTANCE);
        return updaters;
    }

    @Override
    public String getPaletteFileName() {
        return "block/block_palette_503.nbt";
    }

    @Override
    public int getVersion() {
        return 17959425;
    }
}