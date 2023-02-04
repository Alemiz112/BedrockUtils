package eu.mizerak.alemiz.bedrockutils.block.comparator;

import eu.mizerak.alemiz.bedrockutils.block.state.BlockState;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;

public class HashedPaletteComparator implements Comparator<BlockState> {
    public static final HashedPaletteComparator INSTANCE = new HashedPaletteComparator();

    private static final long FNV1_64_INIT = 0xcbf29ce484222325L;
    private static final long FNV1_PRIME_64 = 1099511628211L;

    @Override
    public int compare(BlockState o1, BlockState o2) {
        byte[] b1 = o1.getIdentifier().getBytes(StandardCharsets.UTF_8);
        byte[] b2 = o2.getIdentifier().getBytes(StandardCharsets.UTF_8);
        long hash1 = fnv164(b1);
        long hash2 = fnv164(b2);
        return Long.compareUnsigned(hash1, hash2);
    }

    public static long fnv164(byte[] data) {
        long hash = FNV1_64_INIT;
        for (byte datum : data) {
            hash *= FNV1_PRIME_64;
            hash ^= (datum & 0xff);
        }

        return hash;
    }
}
