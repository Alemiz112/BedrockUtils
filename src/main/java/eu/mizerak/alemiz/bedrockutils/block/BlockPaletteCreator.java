package eu.mizerak.alemiz.bedrockutils.block;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nukkitx.blockstateupdater.BlockStateUpdater;
import com.nukkitx.blockstateupdater.BlockStateUpdaters;
import com.nukkitx.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.nbt.NbtUtils;
import eu.mizerak.alemiz.bedrockutils.BedrockUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BlockPaletteCreator {

    private final CompoundTagUpdaterContext context;

    public BlockPaletteCreator() {
        this.context = this.createContext();
    }

    public abstract List<NbtMap> createBlockPalette();
    protected abstract NbtMap createUpdaterState(String identifier, int blockId, short damage);
    protected abstract NbtMap createState(BlockEntry blockEntry, int runtimeId);

    public NbtMap updateBlockState(NbtMap tag, int version) {
        return this.context.update(tag, version);
    }

    public void save(List<NbtMap> blockStates, String saveFile) {
        NbtList<NbtMap> blockPalette = new NbtList<>(NbtType.COMPOUND, blockStates);
        BedrockUtils.saveCompound(blockPalette, saveFile);
    }

    public void saveVanilla(List<NbtMap> blockStates, String saveFile) {
        NbtMap blockPalette = NbtMap.builder()
                .putList("blocks", NbtType.COMPOUND, blockStates)
                .build();
        BedrockUtils.saveCompound(blockPalette, saveFile);
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
