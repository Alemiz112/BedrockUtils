package eu.mizerak.alemiz.bedrockutils.block.comparator;

import eu.mizerak.alemiz.bedrockutils.block.BlockState;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;

public class AlphabetPaletteComparator implements Comparator<BlockState> {
    public static final AlphabetPaletteComparator INSTANCE = new AlphabetPaletteComparator();

    @Override
    public int compare(BlockState o1, BlockState o2) {
        return o1.getIdentifier().compareTo(o2.getIdentifier());
    }
}
