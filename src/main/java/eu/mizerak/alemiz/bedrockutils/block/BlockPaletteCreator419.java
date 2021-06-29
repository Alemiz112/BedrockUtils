package eu.mizerak.alemiz.bedrockutils.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nukkitx.blockstateupdater.*;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
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
    public List<NbtMap> createBlockPalette() {
        List<NbtMap> blockPalette = this.getBlockPalette();

        // Create stateToRuntimeId map using provided states
        Map<NbtMap, Integer> stateToRuntimeId = new HashMap<>();
        for (int i = 0; i < blockPalette.size(); i++) {
            NbtMap state = blockPalette.get(i);
            stateToRuntimeId.put(state, i);
        }

        JsonObject requiredStates = this.getRequiredBlockStates();
        Map<String, Integer> blockIdMap = this.getIdentifier2LegacyMap();
        List<BlockEntry> createdStates = new ArrayList<>();

        for (String blockIdentifier : requiredStates.keySet()) {
            JsonArray blockValues = requiredStates.getAsJsonArray(blockIdentifier);
            for (JsonElement element : blockValues) {
                String identifier = "minecraft:" + blockIdentifier;
                if (!blockIdMap.containsKey(identifier)) {
                    log.warn("Can not find blockId for " + identifier);
                    continue;
                }

                int blockId = blockIdMap.get(identifier);
                short damage = element.getAsShort();
                NbtMap blockState = this.createUpdaterState(identifier, blockId, damage);
                createdStates.add(new BlockEntry(identifier, blockId, damage, blockState));
            }
        }

        List<NbtMap> blockStates = new ArrayList<>();
        for (BlockEntry blockEntry : createdStates) {
            NbtMap state = blockEntry.getBlockState();

            int runtimeId;
            if (!stateToRuntimeId.containsKey(state)) {
                log.error("Unmatched state " + state);
                NbtMap updateBlock = getFirstState(blockPalette, "minecraft:info_update");
                runtimeId = stateToRuntimeId.get(updateBlock);
            } else {
                runtimeId = stateToRuntimeId.get(state);
            }
            blockStates.add(this.createState(blockEntry, runtimeId));
        }
        return blockStates;
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
    protected NbtMap createState(BlockEntry blockEntry, int runtimeId) {
        NbtMapBuilder builder = blockEntry.getBlockState().toBuilder();
        builder.putInt("id", blockEntry.getBlockId());
        builder.putShort("data", blockEntry.getData());
        builder.putInt("runtimeId", runtimeId);
        return builder.build();
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
