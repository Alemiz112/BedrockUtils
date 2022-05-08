package eu.mizerak.alemiz.bedrockutils.block;

import com.nukkitx.nbt.NbtMap;
import lombok.Data;

@Data
public class BlockState {
    private final String identifier;
    private final int blockId;
    private final short data;
    private final NbtMap blockState;
}
