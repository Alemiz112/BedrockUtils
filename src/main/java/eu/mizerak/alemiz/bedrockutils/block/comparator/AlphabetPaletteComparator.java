package eu.mizerak.alemiz.bedrockutils.block.comparator;

import eu.mizerak.alemiz.bedrockutils.block.state.BlockState;

import java.util.Comparator;

public class AlphabetPaletteComparator implements Comparator<BlockState> {
    public static final AlphabetPaletteComparator INSTANCE = new AlphabetPaletteComparator();

    @Override
    public int compare(BlockState o1, BlockState o2) {
        return o1.getIdentifier().compareToIgnoreCase(o2.getIdentifier());
    }
}
