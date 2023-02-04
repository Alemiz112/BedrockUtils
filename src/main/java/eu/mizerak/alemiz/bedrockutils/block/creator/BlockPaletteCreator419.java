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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class BlockPaletteCreator419 extends BlockPaletteCreator {

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
        List<NbtMap> blockPalette = new ArrayList<>();
        // Create stateToRuntimeId map using provided states
        Map<NbtMap, Integer> stateToRuntimeId = new HashMap<>();

        int rid = 0;
        for (NbtMap state : this.getBlockPalette()) {
            NbtMap blockState;
            if (state.containsKey("name_hash")) {
                NbtMapBuilder builder = state.toBuilder();
                builder.remove("name_hash");
                blockState = builder.build();
            } else {
                blockState = state;
            }

            blockPalette.add(blockState);
            stateToRuntimeId.put(blockState, rid);
            rid++;
        }

        JsonObject requiredStates = this.getRequiredBlockStates();
        LegacyBlockMapping blockIdMap = this.getIdentifier2LegacyMap();
        List<BlockState> createdStates = new ArrayList<>();

        for (String blockIdentifier : requiredStates.keySet()) {
            JsonArray blockValues = requiredStates.getAsJsonArray(blockIdentifier);
            for (JsonElement element : blockValues) {
                String identifier = "minecraft:" + blockIdentifier;
                int blockId = blockIdMap.getBlockId(identifier);
                if (blockId == -1) {
                    log.warn("Can not find blockId for " + identifier);
                    continue;
                }

                short damage = element.getAsShort();
                NbtMap blockState = this.createUpdaterState(identifier, blockId, damage);
                createdStates.add(new BlockState(identifier, blockId, damage, blockState));
            }
        }

        BlockPalette palette = new BlockPalette();

        for (BlockState blockState : createdStates) {
            NbtMap state = blockState.getBlockState();
            String identifier = state.getString("name");

            boolean stateOverload = false;
            if (!identifier.equals(blockIdMap.gteBlockIdentifier(blockState.getBlockId()))) {
                int blockId = blockIdMap.getBlockId(identifier);
                if (blockId != -1 && blockState.getBlockId() != blockId) {
                    stateOverload = true;
                }
            }

            int runtimeId;
            if (stateToRuntimeId.containsKey(state)) {
                runtimeId = stateToRuntimeId.get(state);
            } else {
                palette.getUnmatchedStates().add(blockState);
                runtimeId = stateToRuntimeId.get(getFirstState(blockPalette, "minecraft:info_update"));
            }

            NbtMapBuilder nukkitState = this.createStateNbt(blockState, runtimeId);
            nukkitState.putBoolean("stateOverload", stateOverload);

            blockState = blockState.toBuilder()
                    .blockState(nukkitState.build())
                    .build();
            palette.getBlockStates().add(blockState);
        }
        return palette;
    }

    @Override
    protected NbtMap createUpdaterState(String identifier, int blockId, short damage) {
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
    protected NbtMapBuilder createStateNbt(BlockState blockState, int runtimeId) {
        NbtMapBuilder builder = blockState.getBlockState().toBuilder();
        builder.putInt("id", blockState.getBlockId());
        builder.putShort("data", blockState.getData());
        builder.putInt("runtimeId", runtimeId);
        return builder;
    }

    @Override
    public String getPaletteFileName() {
        return "block/block_palette_419.nbt";
    }

    @Override
    public int getVersion() {
        return 17825808;
    }
}
