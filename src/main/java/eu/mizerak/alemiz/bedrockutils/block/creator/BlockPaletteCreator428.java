package eu.mizerak.alemiz.bedrockutils.block.creator;

import lombok.extern.log4j.Log4j2;
import org.cloudburstmc.blockstateupdater.BlockStateUpdater;
import org.cloudburstmc.blockstateupdater.BlockStateUpdater_1_16_210;

import java.util.List;

@Log4j2
public class BlockPaletteCreator428 extends BlockPaletteCreator419 {

    @Override
    public List<BlockStateUpdater> getUpdaters() {
        List<BlockStateUpdater> updaters = super.getUpdaters();
        updaters.add(BlockStateUpdater_1_16_210.INSTANCE);
        return updaters;
    }

    @Override
    public String getPaletteFileName() {
        return "block/block_palette_428.nbt";
    }

    @Override
    public int getVersion() {
        return 17879555;
    }
}
