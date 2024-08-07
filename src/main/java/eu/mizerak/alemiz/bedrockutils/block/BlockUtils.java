package eu.mizerak.alemiz.bedrockutils.block;

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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class BlockUtils {

    private static final Pattern PATTERN = Pattern.compile("\\d+$");

    public static void main(String[] args) {
        List<BlockPaletteCreator> creators = new ArrayList<>();
        creators.add(new BlockPaletteCreator407());
        creators.add(new BlockPaletteCreator419());
        creators.add(new BlockPaletteCreator428());
        creators.add(new BlockPaletteCreator440());
        creators.add(new BlockPaletteCreator448());
        creators.add(new BlockPaletteCreator465());
        creators.add(new BlockPaletteCreator471());
        creators.add(new BlockPaletteCreator475());
        creators.add(new BlockPaletteCreator486());
        creators.add(new BlockPaletteCreator503());
        creators.add(new BlockPaletteCreator527());
        creators.add(new BlockPaletteCreator534());
        creators.add(new BlockPaletteCreator544());
        creators.add(new BlockPaletteCreator554()); // equals to previous
        creators.add(new BlockPaletteCreator557()); // equals to previous
        creators.add(new BlockPaletteCreator560()); // first 1.20 block states
        creators.add(new BlockPaletteCreator567());
        creators.add(new BlockPaletteCreator575()); // wool color is prepended to the name
        creators.add(new BlockPaletteCreator582()); // each log and fence type has own identifier now
        creators.add(new BlockPaletteCreator588());
        creators.add(new BlockPaletteCreator594()); // shulker box, concrete have own type per color
        creators.add(new BlockPaletteCreator617()); // stained_glass, stained_glass_pane, concrete_powder, stained_hardened_clay have own type per color
        creators.add(new BlockPaletteCreator622()); // FACING_TO_CARDINAL updates for chests
        creators.add(new BlockPaletteCreator630()); // planks, stone blocks have own type now
        creators.add(new BlockPaletteCreator649()); // hard_stained_glass, hard_stained_glass_pane have own type per color
        creators.add(new BlockPaletteCreator662()); // double_wooden_slab, leaves, leaves2, wood, wooden_slab have own type
        creators.add(new BlockPaletteCreator671()); // sapling, red_flower, coral_fan, coral_fan_dead have own type
        creators.add(new BlockPaletteCreator685()); // tallgrass, double plant, coral block, stone slabs were split
        creators.add(new BlockPaletteCreator712()); // a lot was change :)

        // generateAllBlockPalettes(creators);

        BlockPaletteCreator latest = creators.get(creators.size() - 1);
        int version = getBedrockVersion(latest);

        // Generate a Nukkit friendly block palette
        generateBlockPalette(latest, "runtime_block_states_" + version + ".dat");

        // Compare block states between latest and previous palette
        BlockPaletteCreator previous = creators.get(creators.size() - 2);
        // compareBlockPalettes(latest, previous);

        // Generate a pretty block palette dump
        createPaletteDump(latest, "block_properties.txt");

        // findExtraStates(latest, creators.get(creators.size() - 2));

        int vanilla = CompoundTagUpdaterContext.makeVersion(1, 21, 20);
        System.out.println(latest.getVersion() - vanilla);

        System.out.println(BlockStateUpdaters.getLatestVersion());
    }

    public static void generateBlockPalette(BlockPaletteCreator blockCreator, String saveFile) {
        BlockPalette blockPalette = blockCreator.createBlockPalette();
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
