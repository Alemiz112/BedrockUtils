package eu.mizerak.alemiz.bedrockutils.block.state;

import org.cloudburstmc.nbt.NbtMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class BlockState {
    private final String identifier;
    private final int blockId;
    private final short data;
    private final NbtMap blockState;
}
