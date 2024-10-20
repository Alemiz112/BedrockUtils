package eu.mizerak.alemiz.bedrockutils.block;

import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.function.BiConsumer;

@AllArgsConstructor
public class LegacyBlockMapping {
    private final Map<String, Integer> identifier2BlockId;
    private final Map<Integer, String> blockId2Identifier;

    public int getBlockId(String identifier) {
        return this.identifier2BlockId.getOrDefault(identifier, -1);
    }

    public String getBlockIdentifier(int blockId) {
        return this.blockId2Identifier.get(blockId);
    }

    public void forEach(BiConsumer<String, Integer> consumer) {
        this.identifier2BlockId.forEach(consumer);
    }
}
