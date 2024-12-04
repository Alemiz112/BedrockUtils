package eu.mizerak.alemiz.bedrockutils.block.creator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.mizerak.alemiz.bedrockutils.block.state.BlockDefinition;
import org.cloudburstmc.blockstateupdater.BlockStateUpdater;
import org.cloudburstmc.blockstateupdater.BlockStateUpdaters;
import org.cloudburstmc.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.nbt.NbtUtils;
import eu.mizerak.alemiz.bedrockutils.block.BlockPalette;
import eu.mizerak.alemiz.bedrockutils.block.state.BlockState;
import eu.mizerak.alemiz.bedrockutils.block.LegacyBlockMapping;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static eu.mizerak.alemiz.bedrockutils.block.BlockUtils.cleanBlockState;

public abstract class BlockPaletteCreator {

    private final CompoundTagUpdaterContext context;

    public BlockPaletteCreator() {
        this.context = this.createContext();
    }

    public abstract BlockPalette createBlockPalette();

    public int includeMissingBlockStates(BlockPalette palette) {
        return 0;
    }

    protected abstract NbtMap createUpdaterState(String identifier, short damage);

    protected abstract NbtMapBuilder createStateNbt(BlockState blockState, int runtimeId, boolean stateOverload);

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
                comparingStates.add(cleanBlockState(state));
            }
        }

        for (NbtMap state : this.getBlockPalette()) {
            if (!comparingStates.remove(state)) {
                NbtMapBuilder builder = state.toBuilder();
                if (!strict) {
                    cleanBlockState(builder);
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
            currentStates.add(cleanBlockState(state));
        }
        
        for (NbtMap state : palette.getBlockPalette()) {
            if (!currentStates.remove(state)) {
                NbtMapBuilder builder = this.updateBlockState(state, palette.getVersion()).toBuilder();
                cleanBlockState(builder);
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

    public List<BlockDefinition> getBlockDefinitions() {
        List<NbtMap> palette = this.getBlockPalette();
        Map<String, List<BlockState>> blocks = new TreeMap<>();

        for (NbtMap state : palette) {
            BlockState blockState = new BlockState(state.getString("name"), 0, (short) 0, state);
            blocks.computeIfAbsent(blockState.getIdentifier(), i -> new ArrayList<>()).add(blockState);
        }

        List<BlockDefinition> definitions = new ArrayList<>();
        for (Map.Entry<String, List<BlockState>> entry : blocks.entrySet()) {
            definitions.add(new BlockDefinition(entry.getKey(), entry.getValue()));
        }
        return definitions;
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
            Gson gson = new GsonBuilder().setLenient().create();
            json = gson.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
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
