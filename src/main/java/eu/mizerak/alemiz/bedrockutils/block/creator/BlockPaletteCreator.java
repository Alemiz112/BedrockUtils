package eu.mizerak.alemiz.bedrockutils.block.creator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nukkitx.blockstateupdater.BlockStateUpdater;
import com.nukkitx.blockstateupdater.BlockStateUpdaters;
import com.nukkitx.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.nbt.NbtUtils;
import eu.mizerak.alemiz.bedrockutils.block.BlockPalette;
import eu.mizerak.alemiz.bedrockutils.block.BlockState;
import eu.mizerak.alemiz.bedrockutils.block.LegacyBlockMapping;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class BlockPaletteCreator {

    private final CompoundTagUpdaterContext context;

    public BlockPaletteCreator() {
        this.context = this.createContext();
    }

    public abstract BlockPalette createBlockPalette();
    protected abstract NbtMap createUpdaterState(String identifier, int blockId, short damage);
    protected abstract NbtMapBuilder createStateNbt(BlockState blockState, int runtimeId);

    public NbtMap updateBlockState(NbtMap tag, int version) {
        return this.context.update(tag, version);
    }

    protected CompoundTagUpdaterContext createContext() {
        CompoundTagUpdaterContext context = new CompoundTagUpdaterContext();
        this.getUpdaters().forEach(updater -> updater.registerUpdaters(context));
        return context;
    }

    protected NbtMap getFirstState(Collection<NbtMap> states, String identifier) {
        for (NbtMap state : states) {
            if (state.containsKey("block")) {
                NbtMap oldState = state.getCompound("block");
                if (identifier.equals(oldState.getString("name"))) {
                    return oldState;
                }
            } else if (identifier.equals(state.getString("name"))) {
                return state;
            }
        }
        return null;
    }

    public List<NbtMap> compareStatesTo(BlockPaletteCreator compareTo, boolean strict) {
        List<NbtMap> comparingStates = new ArrayList<>();
        List<NbtMap> unmatchedStates = new ArrayList<>();

        // Remove identifier hash (name_hash) that MS started to append
        if (strict) {
            comparingStates.addAll(compareTo.getBlockPalette());
        } else {
            for (NbtMap state : compareTo.getBlockPalette()) {
                if (state.containsKey("name_hash")) {
                    NbtMapBuilder builder = state.toBuilder();
                    builder.remove("name_hash");
                    comparingStates.add(builder.build());
                } else {
                    comparingStates.add(state);
                }
            }
        }

        for (NbtMap state : this.getBlockPalette()) {
            if (!comparingStates.remove(state)) {
                NbtMapBuilder builder = state.toBuilder();
                if (!strict) {
                    builder.remove("name_hash");
                }
                builder.putInt("version", compareTo.getVersion());
                if (!comparingStates.remove(builder.build())) {
                    unmatchedStates.add(state);
                }
            }
        }
        return unmatchedStates;
    }

    public List<NbtMap> findExtraStatesIn(BlockPaletteCreator palette) {
        List<NbtMap> currentStates = new ArrayList<>();
        List<NbtMap> comparingStates = new ArrayList<>();

        // Remove identifier hash (name_hash) that MS started to append
        for (NbtMap state : this.getBlockPalette()) {
            if (state.containsKey("name_hash")) {
                NbtMapBuilder builder = state.toBuilder();
                builder.remove("name_hash");
                currentStates.add(builder.build());
            } else {
                currentStates.add(state);
            }
        }
        
        for (NbtMap state : palette.getBlockPalette()) {
            if (!currentStates.remove(state)) {
                NbtMapBuilder builder = this.updateBlockState(state, palette.getVersion()).toBuilder();
                builder.remove("name_hash");
                builder.putInt("version", palette.getVersion());
                comparingStates.add(builder.build());
            }
        }

        for (NbtMap state : currentStates) {
            if (!comparingStates.remove(state)) {
                NbtMap updatedState = state.toBuilder()
                        .putInt("version", palette.getVersion())
                        .build();
                comparingStates.remove(updatedState);
            }
        }
        return comparingStates;
    }

    public List<NbtMap> getBlockPalette() {
        try (InputStream stream = BlockStateUpdaters.class.getClassLoader().getResourceAsStream(this.getPaletteFileName())) {
            return ((NbtMap) NbtUtils.createGZIPReader(stream).readTag()).getList("blocks", NbtType.COMPOUND);
        } catch (Exception e) {
            throw new AssertionError("Error loading block palette " + this.getPaletteFileName(), e);
        }
    }

    public LegacyBlockMapping getIdentifier2LegacyMap() {
        JsonObject blockIdJson;
        try (InputStream stream = BlockStateUpdaters.class.getClassLoader().getResourceAsStream("block/block_id_map.json")) {
            blockIdJson = (JsonObject) JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new AssertionError("Error loading block id map!");
        }

        Map<String, Integer> identifier2BlockId = new HashMap<>();
        Map<Integer, String> blockId2Identifier = new HashMap<>();

        for (String identifier : blockIdJson.keySet()) {
            int blockId = blockIdJson.get(identifier).getAsInt();
            identifier2BlockId.put(identifier, blockId);
            blockId2Identifier.put(blockId, identifier);
        }
        return new LegacyBlockMapping(identifier2BlockId, blockId2Identifier);
    }

    public JsonObject getRequiredBlockStates() {
        JsonObject json;
        try (InputStream stream = BlockStateUpdaters.class.getClassLoader().getResourceAsStream("block/required_block_states.json")) {
            json = (JsonObject) JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new AssertionError("Error loading required block states!");
        }

        if (json.has("minecraft")) {
            return json.getAsJsonObject("minecraft");
        }
        return json;
    }

    public abstract String getPaletteFileName();

    public abstract List<BlockStateUpdater> getUpdaters();
    public abstract int getVersion();
}
