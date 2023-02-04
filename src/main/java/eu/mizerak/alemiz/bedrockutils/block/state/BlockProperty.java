package eu.mizerak.alemiz.bedrockutils.block.state;

import lombok.Data;
import org.cloudburstmc.nbt.NbtType;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

@Data
public class BlockProperty<T> {
    private final String propertyName;
    private final NbtType<T> type;
    private final Set<T> values = new HashSet<>();

    public void addValue(T value) {
        this.values.add(value);
    }

    public String toStringPretty() {
        StringJoiner joiner = new StringJoiner(", ");
        for (T value : values) {
            joiner.add(value.toString());
        }
        return this.propertyName + "[" + joiner.toString() + "]";
    }
}
