package eu.mizerak.alemiz.bedrockutils.block;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.mizerak.alemiz.bedrockutils.block.state.BlockDefinition;
import org.cloudburstmc.blockstateupdater.BlockStateUpdaters;
import org.cloudburstmc.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;
import org.cloudburstmc.nbt.NbtList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import eu.mizerak.alemiz.bedrockutils.BedrockUtils;
import eu.mizerak.alemiz.bedrockutils.block.creator.*;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class BlockUtils {
    private static final Pattern PATTERN = Pattern.compile("\\d+$");

    private static final List<BlockPaletteCreator> CREATORS = new ArrayList<>();
    static {
        CREATORS.add(new BlockPaletteCreator407());
        CREATORS.add(new BlockPaletteCreator419());
        CREATORS.add(new BlockPaletteCreator428());
        CREATORS.add(new BlockPaletteCreator440());
        CREATORS.add(new BlockPaletteCreator448());
        CREATORS.add(new BlockPaletteCreator465());
        CREATORS.add(new BlockPaletteCreator471());
        CREATORS.add(new BlockPaletteCreator475());
        CREATORS.add(new BlockPaletteCreator486());
        CREATORS.add(new BlockPaletteCreator503());
        CREATORS.add(new BlockPaletteCreator527());
        CREATORS.add(new BlockPaletteCreator534());
        CREATORS.add(new BlockPaletteCreator544());
        CREATORS.add(new BlockPaletteCreator554()); // equals to previous
        CREATORS.add(new BlockPaletteCreator557()); // equals to previous
        CREATORS.add(new BlockPaletteCreator560()); // first 1.20 block states
        CREATORS.add(new BlockPaletteCreator567());
        CREATORS.add(new BlockPaletteCreator575()); // wool color is prepended to the name
        CREATORS.add(new BlockPaletteCreator582()); // each log and fence type has own identifier now
        CREATORS.add(new BlockPaletteCreator588());
        CREATORS.add(new BlockPaletteCreator594()); // shulker box, concrete have own type per color
        CREATORS.add(new BlockPaletteCreator617()); // stained_glass, stained_glass_pane, concrete_powder, stained_hardened_clay have own type per color
        CREATORS.add(new BlockPaletteCreator622()); // FACING_TO_CARDINAL updates for chests
        CREATORS.add(new BlockPaletteCreator630()); // planks, stone blocks have own type now
        CREATORS.add(new BlockPaletteCreator649()); // hard_stained_glass, hard_stained_glass_pane have own type per color
        CREATORS.add(new BlockPaletteCreator662()); // double_wooden_slab, leaves, leaves2, wood, wooden_slab have own type
        CREATORS.add(new BlockPaletteCreator671()); // sapling, red_flower, coral_fan, coral_fan_dead have own type
        CREATORS.add(new BlockPaletteCreator685()); // tallgrass, double plant, coral block, stone slabs were split
        CREATORS.add(new BlockPaletteCreator712()); // a lot was changed :)
        CREATORS.add(new BlockPaletteCreator729()); // structure_void, tnt, sponge, purpur_block, cobblestone_wall, and some edu blocks were split
        CREATORS.add(new BlockPaletteCreator748()); // skull and brown_mushroom_block blocks were flattened, and this should be the end of flattening YAY
        CREATORS.add(new BlockPaletteCreator765());
        CREATORS.add(new BlockPaletteCreator776()); // doors, gates use cardinal_direction now
    }

    public static void main(String[] args) {
        // generateAllBlockPalettes(creators);

        BlockPaletteCreator latest = CREATORS.get(CREATORS.size() - 1);
        int version = getBedrockVersion(latest);

        // Generate a Nukkit friendly block palette
        BlockPalette palette = generateBlockPalette(latest, "runtime_block_states_" + version + ".dat");
        // Compare block states between latest and previous palette
        BlockPaletteCreator previous = CREATORS.get(CREATORS.size() - 2);
        // compareBlockPalettes(latest, previous);
        // Build version with all vanilla default states
        withMissingBlockStates(latest, palette, "full_runtime_block_states_" + version + ".dat");

        // Generate a pretty block palette dump
        createPaletteDump(latest, "block_properties.txt");

        // findExtraStates(latest, creators.get(creators.size() - 2));

        int vanilla = CompoundTagUpdaterContext.makeVersion(1, 21, 60);
        System.out.println(1 + (latest.getVersion() - vanilla));

        System.out.println(BlockStateUpdaters.getLatestVersion());

        // updateBlockIdsJson(Paths.get("block_id_map.json"));
        // generateAllPalettes();
    }

    public static BlockPalette generateBlockPalette(BlockPaletteCreator blockCreator, String saveFile) {
        BlockPalette blockPalette = blockCreator.createBlockPalette();
        blockPalette.printUnmatchedStates();
        blockPalette.save(saveFile);
        blockPalette.saveVanilla(saveFile.replace("dat", "nbt"));
        return blockPalette;
    }

    public static void withMissingBlockStates(BlockPaletteCreator blockCreator, BlockPalette blockPalette, String saveFile) {
        int newStates = blockCreator.includeMissingBlockStates(blockPalette);
        log.info("Added {} missing vanilla states!", newStates);

        blockPalette.printUnmatchedStates();
        blockPalette.save(saveFile);
        blockPalette.saveVanilla(saveFile.replace("dat", "nbt"));
    }

    public static void generateAllBlockPalettes(List<BlockPaletteCreator> creators) {
        for (BlockPaletteCreator creator : creators) {
            int version = getBedrockVersion(creator);
            BlockPalette blockPalette = creator.createBlockPalette();
            blockPalette.printUnmatchedStates();
            blockPalette.save("runtime_block_states_" + version + ".dat");
        }
    }

    public static void compareBlockPalettes(BlockPaletteCreator blockCreator, BlockPaletteCreator comparing) {
        log.info("Comparing {} to {}", blockCreator.getClass().getSimpleName(), comparing.getClass().getSimpleName());
        List<NbtMap> unmatchedStates = blockCreator.compareStatesTo(comparing, false);
        log.info("Found {} unmatched states!", unmatchedStates.size());
        // This means that comparing palette does not contain state from blockCreator palette
        unmatchedStates.forEach(state -> log.warn("Not matched state: {}", state));
    }

    // Looks for extra states in comparing which are not in blockCreator
    public static void findExtraStates(BlockPaletteCreator blockCreator, BlockPaletteCreator comparing) {
        log.info("Looking for extra states in {}", comparing.getClass().getSimpleName());
        List<NbtMap> extraStates = blockCreator.findExtraStatesIn(comparing);
        log.info("Found {} extra states!", extraStates.size());
        extraStates.forEach(state -> log.warn("Extra state: {}", state));
    }

    public static void saveCanonicalPalette(String canonicalFile, String saveFile) {
        NbtList<NbtMap> blockStates = (NbtList<NbtMap>) BedrockUtils.loadNetworkCompound(canonicalFile);

        NbtMap blockPalette = NbtMap.builder()
                .putList("blocks", NbtType.COMPOUND, blockStates)
                .build();
        BedrockUtils.saveCompoundCompressed(blockPalette, saveFile);
    }

    public static void createPaletteDump(BlockPaletteCreator creator, String saveFile) {
        StringJoiner joiner = new StringJoiner("\n");
        for (BlockDefinition definition : creator.getBlockDefinitions()) {
            joiner.add(definition.toStringPretty());
        }
        BedrockUtils.saveBytes(joiner.toString().getBytes(StandardCharsets.UTF_8), saveFile);
    }

    public static void updateBlockIdsJson(Path newBlockIdsPath) {
        BlockPaletteCreator paletteCreator = CREATORS.get(0);

        LegacyBlockMapping legacyMapping = paletteCreator.getIdentifier2LegacyMap();

        JsonObject blockIdJson;
        try (InputStream stream = Files.newInputStream(newBlockIdsPath);
             InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            blockIdJson = (JsonObject) JsonParser.parseReader(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TreeMap<String, Integer> newMapping = new TreeMap<>();

        for (String identifier : blockIdJson.keySet()) {
            int blockId = blockIdJson.get(identifier).getAsInt();
            int legacyId = legacyMapping.getBlockId(identifier);
            if (legacyId == blockId) { // id and identifier are the same
                newMapping.put(identifier, blockId);
                continue;
            }

            newMapping.put(identifier, blockId); // add new mapping

            String legacyIdentifier = legacyMapping.getBlockIdentifier(blockId);
            if (legacyIdentifier == null && legacyId != -1) { // id not same, but identifier is
                newMapping.put("_" + identifier, legacyId); // add old mapping
            } else if (legacyIdentifier != null) { // id is same, but identifier is not
                if (legacyIdentifier.startsWith("_")) {
                    continue;
                }
                newMapping.put("_" + legacyIdentifier, blockId); // add old mapping
            }
        }

        legacyMapping.forEach((identifier, blockId) -> {
            if (!newMapping.containsKey(identifier) && !newMapping.containsKey("_" + identifier)) {
                log.info("Adding legacy block {} with id {}", identifier, blockId);
                newMapping.put((identifier.startsWith("_") ? "" : "_") + identifier, blockId);
            }
        });

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        BedrockUtils.saveBytes(gson.toJson(newMapping).getBytes(StandardCharsets.UTF_8), "block_id_map_new.json");
    }

    public static void generateAllPalettes() {
        for (BlockPaletteCreator creator : CREATORS) {
            int version = getBedrockVersion(creator);
            BlockPalette palette = generateBlockPalette(creator, "all/runtime_block_states_" + version + ".dat");
            withMissingBlockStates(creator, palette, "all/full_runtime_block_states_" + version + ".dat");
        }
    }

    public static int getBedrockVersion(BlockPaletteCreator creator) {
        Matcher matcher = PATTERN.matcher(creator.getClass().getSimpleName());
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return 0;
    }

    public static NbtMap cleanBlockState(NbtMap state) {
        if (state.containsKey("name_hash") || state.containsKey("network_id") || state.containsKey("block_id")) {
            return cleanBlockState(state.toBuilder()).build();
        }
        return state;
    }

    public static NbtMapBuilder cleanBlockState(NbtMapBuilder builder) {
        builder.remove("name_hash");
        builder.remove("network_id");
        builder.remove("block_id");
        return builder;
    }
}
