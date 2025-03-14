package eu.mizerak.alemiz.bedrockutils.block.creator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.cloudburstmc.blockstateupdater.*;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import eu.mizerak.alemiz.bedrockutils.block.BlockPalette;
import eu.mizerak.alemiz.bedrockutils.block.state.BlockState;
import eu.mizerak.alemiz.bedrockutils.block.LegacyBlockMapping;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
public class BlockPaletteCreator407 extends BlockPaletteCreator {

    @Override
    public List<BlockStateUpdater> getUpdaters() {
        List<BlockStateUpdater> updaters = new ArrayList<>();
        updaters.add(BlockStateUpdaterBase.INSTANCE);
        updaters.add(BlockStateUpdater_1_10_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_12_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_13_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_14_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_15_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_16_0.INSTANCE);
        return updaters;
    }

    @Override
    public BlockPalette createBlockPalette() {
        List<NbtMap> blockPalette = this.getBlockPalette();

        // Create stateToRuntimeId map using provided states
        Map<NbtMap, Integer> stateToRuntimeId = new HashMap<>();
        for (int i = 0; i < blockPalette.size(); i++) {
            NbtMap state = blockPalette.get(i).getCompound("block");
            stateToRuntimeId.put(state, i);
        }

        JsonObject requiredStates = this.getRequiredBlockStates();
        LegacyBlockMapping blockIdMap = this.getIdentifier2LegacyMap();
        List<BlockState> createdStates = new ArrayList<>();

        for (String blockIdentifier : requiredStates.keySet()) {
            JsonArray blockValues = requiredStates.getAsJsonArray(blockIdentifier);
            for (JsonElement element : blockValues) {
                String identifier = "minecraft:" + blockIdentifier;
                int blockId = blockIdMap.getBlockId("_" + identifier); // check for legacy block id first
                if (blockId == -1) {
                    blockId = blockIdMap.getBlockId(identifier);
                }

                if (blockId == -1) {
                    log.warn("Can not find blockId for " + identifier);
                    continue;
                }

                short damage = element.getAsShort();
                NbtMap blockState = this.createUpdaterState(identifier, damage);
                createdStates.add(new BlockState(identifier, blockId, damage, blockState));
            }
        }

        BlockPalette palette = new BlockPalette();
        for (BlockState blockState : createdStates) {
            if (!stateToRuntimeId.containsKey(blockState.getBlockState())) {
                palette.getUnmatchedStates().add(blockState);
            } else {
                // Runtime id will be incrementally allocated when server starts
                NbtMap nukkitState = this.createStateNbt(blockState, 0, false).build();
                palette.getBlockStates().add(new BlockState(blockState.getIdentifier(), blockState.getBlockId(), blockState.getData(), nukkitState));
            }
        }

        // This is dump but because of Nukkit assuming "minecraft:air" has runtimeId 0
        // we need to reorder the palette.
        // At least this was removed after 1.16.210
        palette.sort((state1, state2) -> {
            String identifier1 = state1.getIdentifier();
            String identifier2 = state2.getIdentifier();
            return identifier1.equals(identifier2) ? 0 :
                    (identifier1.equals("minecraft:air") ? -1 :
                            (identifier2.equals("minecraft:air") ? 1 : 0));
        });
        return palette;
    }

    @Override
    protected NbtMap createUpdaterState(String identifier, short damage) {
        NbtMap emptyState = NbtMap.builder()
                .putString("name", identifier)
                .putShort("val", damage)
                .putInt("version", 0)
                .build();

        NbtMapBuilder builder = this.updateBlockState(emptyState, 0).toBuilder();
        builder.putInt("version", this.getVersion()); // Make sure correct version is in nbt
        return builder.build();
    }

    @Override
    protected NbtMapBuilder createStateNbt(BlockState blockEntry, int runtimeId, boolean stateOverload) {
        NbtMapBuilder builder = NbtMap.builder();
        builder.putCompound("block", blockEntry.getBlockState());
        builder.putInt("id", blockEntry.getBlockId());
        builder.putShort("data", blockEntry.getData());
        return builder;
    }

    @Override
    public String getPaletteFileName() {
        return "block/block_palette_407.nbt";
    }

    @Override
    public int getVersion() {
        return 17825808;
    }
}
