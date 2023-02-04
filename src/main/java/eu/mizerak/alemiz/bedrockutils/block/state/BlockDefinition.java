package eu.mizerak.alemiz.bedrockutils.block.state;

import lombok.Data;
import lombok.ToString;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.*;

@Data
@ToString(exclude = "blockStates")
public class BlockDefinition {
    private final String identifier;
    private final List<BlockState> blockStates;
    private final Collection<BlockProperty<?>> properties;

    public BlockDefinition(String identifier, List<BlockState> blockStates) {
        this.identifier = identifier;
        this.blockStates = blockStates;
        this.properties = this.buildProperties();
    }

    private Collection<BlockProperty<?>> buildProperties() {
        Map<String, BlockProperty<?>> properties = new TreeMap<>();

        for (BlockState blockState : this.blockStates) {
            NbtMap states = blockState.getBlockState().getCompound("states");
            for (Map.Entry<String, Object> entry : states.entrySet()) {
                String propertyName = entry.getKey();
                BlockProperty<Object> property = (BlockProperty<Object>) properties.get(propertyName);
                if (property == null) {
                    NbtType<?> type = NbtType.byClass(entry.getValue().getClass());
                    properties.put(propertyName, property = (BlockProperty<Object>) new BlockProperty<>(propertyName, type));
                }
                property.addValue(entry.getValue());
            }
        }
        return properties.values();
    }

    public String toStringPretty() {
        StringJoiner joiner = new StringJoiner(", ");
        for (BlockProperty<?> property : this.properties) {
            joiner.add(property.toStringPretty());
        }
        return this.identifier + "{" + joiner.toString() + "}";
    }
}
