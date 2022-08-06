package eu.mizerak.alemiz.bedrockutils.block.creator;

import com.nukkitx.blockstateupdater.BlockStateUpdater;
import com.nukkitx.blockstateupdater.BlockStateUpdater_1_19_20;

import java.util.List;

public class BlockPaletteCreator544 extends BlockPaletteCreator534 {

    @Override
    public List<BlockStateUpdater> getUpdaters() {
        List<BlockStateUpdater> updaters = super.getUpdaters();
        updaters.add(BlockStateUpdater_1_19_20.INSTANCE);
        return updaters;
    }

    @Override
    public String getPaletteFileName() {
        return "block/block_palette_544.nbt";
    }

    @Override
    public int getVersion() {
        return 17959425;
    }
}