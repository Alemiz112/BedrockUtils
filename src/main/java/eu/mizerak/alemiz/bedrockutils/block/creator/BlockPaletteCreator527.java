package eu.mizerak.alemiz.bedrockutils.block.creator;

import com.nukkitx.blockstateupdater.BlockStateUpdater;
import com.nukkitx.blockstateupdater.BlockStateUpdater_1_19_0;

import java.util.List;

public class BlockPaletteCreator527 extends BlockPaletteCreator503 {

    @Override
    public List<BlockStateUpdater> getUpdaters() {
        List<BlockStateUpdater> updaters = super.getUpdaters();
        updaters.add(BlockStateUpdater_1_19_0.INSTANCE);
        return updaters;
    }

    @Override
    public String getPaletteFileName() {
        return "block/block_palette_527.nbt";
    }

    @Override
    public int getVersion() {
        return 17959425;
    }
}