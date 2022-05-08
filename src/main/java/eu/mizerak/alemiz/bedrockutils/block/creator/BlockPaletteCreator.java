package eu.mizerak.alemiz.bedrockutils.block.creator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nukkitx.blockstateupdater.BlockStateUpdater;
import com.nukkitx.blockstateupdater.BlockStateUpdaters;
import com.nukkitx.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.nbt.NbtUtils;
import eu.mizerak.alemiz.bedrockutils.block.BlockPalette;
import eu.mizerak.alemiz.bedrockutils.block.BlockState;

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
    protected abstract NbtMap createStateNbt(BlockState blockState, int runtimeId);

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

    public List<NbtMap> compareStatesTo(BlockPaletteCreator compareTo) {
        List<NbtMap> comparingStates = new ArrayList<>(compareTo.getBlockPalette());
        List<NbtMap> unmatchedStates = new ArrayList<>();

        for (NbtMap state : this.getBlockPalette()) {
            if (!comparingStates.remove(state)) {
                NbtMap updatedState = state.toBuilder()
                        .putInt("version", compareTo.getVersion())
                        .build();
                if (!comparingStates.remove(updatedState)) {
                    unmatchedStates.add(state);
                }
            }
        }
        return unmatchedStates;
    }

    public List<NbtMap> findExtraStatesIn(BlockPaletteCreator palette) {
        List<NbtMap> currentStates = new ArrayList<>(this.getBlockPalette());
        List<NbtMap> comparingStates = new ArrayList<>();
        for (NbtMap state : palette.getBlockPalette()) {
            if (!comparingStates.remove(state)) {
                NbtMap updatedState = this.updateBlockState(state, palette.getVersion())
                        .toBuilder()
                        .putInt("version", palette.getVersion())
                        .build();
                comparingStates.add(updatedState);
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
            return  ((NbtMap) NbtUtils.createGZIPReader(stream).readTag()).getList("blocks", NbtType.COMPOUND);
        } catch (Exception e) {
            throw new AssertionError("Error loading block palette " + this.getPaletteFileName(), e);
        }
    }

    public Map<String, Integer> getIdentifier2LegacyMap() {
        JsonObject blockIdJson;
        try (InputStream stream = BlockStateUpdaters.class.getClassLoader().getResourceAsStream("block/block_id_map.json")) {
            blockIdJson = (JsonObject) JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new AssertionError("Error loading block id map!");
        }

        Map<String, Integer> blockIdMap = new HashMap<>();
        for (String identifier : blockIdJson.keySet()) {
            int blockId = blockIdJson.get(identifier).getAsInt();
            blockIdMap.put(identifier, blockId);
        }
        return blockIdMap;
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
