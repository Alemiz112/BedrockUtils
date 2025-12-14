import {
    BlockStates,
    BlockTypes,
    BlockPermutation,
    world,
    BlockComponentTypes,
    TickingAreaManager,
    system
} from "@minecraft/server";

import {
    http,
    HttpRequest,
    HttpRequestMethod
} from "@minecraft/server-net";

const BOUNDS = {
    minX: -32, maxX: 32,
    minY: 0, maxY: 320,
    minZ: -32, maxZ: 32
};

let currentPos = { x: BOUNDS.minX, y: BOUNDS.maxY, z: BOUNDS.minZ };

function nextPos() {
    currentPos.x++;
    if (currentPos.x > BOUNDS.maxX) {
        currentPos.x = BOUNDS.minX;
        currentPos.z++;
        if (currentPos.z > BOUNDS.maxZ) {
            currentPos.z = BOUNDS.minZ;
            currentPos.y--;
            if (currentPos.y < BOUNDS.minY) {
                throw new Error("Ran out of space in ticking area!");
            }
        }
    }
    return { ...currentPos };
}

// wait until the world is loaded
world.afterEvents.worldLoad.subscribe(async () => {
    let dimension = world.getDimension("overworld");
    await world.tickingAreaManager.createTickingArea("state-dumper-tool", { dimension, from: { x: BOUNDS.minX, y: BOUNDS.minY, z: BOUNDS.minZ }, to: { x: BOUNDS.maxX, y: BOUNDS.maxY, z: BOUNDS.maxZ } });
    let data = {
        blocks: [],
        properties: [],
    }
    let allStates = BlockStates.getAll();
    for (const i in allStates) {
        let entry = allStates[i];
        data.properties[i] = {
            id: entry.id,
            values: entry.validValues
        }
    }
    let blocks = BlockTypes.getAll();
    for (let i = 0; i < blocks.length; i++) {
        // For some stupid reason, when we keep reusing the same position, it causes the server to crash
        // As a workaround, we use multiple locations 
        const pos = nextPos();
        let permutation = BlockPermutation.resolve(blocks[i].id);
        dimension.setBlockType(pos, permutation.type);
        let blockData = {
            blockId: blocks[i].id,
            properties: Object.keys(permutation.getAllStates()),
            data: {}
        }
        for (const i in blockData.properties) {
            const key = blockData.properties[i];
            let possibleStates = BlockStates.get(key).validValues
            let current = permutation;
            for (let i = 0; i < possibleStates.length; i++) {
                const value = possibleStates[i];
                try {
                    current = current.withState(key, value);
                    let stateId = stringifyState(current.getAllStates());
                    blockData.data[stateId] = prepareStateData(dimension, pos, current, blockData.data[stateId] || {});
                } catch(e) {
                    console.error(e);
                }
            }
        }
        if (blockData.properties.length == 0) {
            blockData.data[""] = prepareStateData(dimension, pos, permutation, {});
        }
        data.blocks.push(blockData)
    }

    let request = new HttpRequest("http://localhost:2001");
    request.setMethod(HttpRequestMethod.Post);
    request.setBody(JSON.stringify(data));
    http.request(request);
    console.warn("Done!");
    //console.warn(JSON.stringify(data));
});

function rgbaToHex({ red, green, blue, alpha = 1 }) {
    const toHex = v => Math.round(v * 255).toString(16).padStart(2, "0");
    return `#${toHex(red)}${toHex(green)}${toHex(blue)}${toHex(alpha)}`;
}

let counter = 0;
function prepareStateData(dimension, pos, current, data) {
    console.warn(current.type.id, stringifyState(current.getAllStates()), counter++);
    data.tags = current.getTags();
    let t = {
    };
    const allStates = current.getAllStates();
    for (const key in allStates) {
        t[key] = allStates[key];
    }
    dimension.setBlockPermutation(pos, BlockPermutation.resolve(current.type.id, t));

    let component = dimension.getBlock(pos).getComponent("minecraft:map_color");
    if (component != null) {
        data.mapColor = rgbaToHex(component.color);
    }
    return data;
}

function stringifyState(state) {
    let str = "";
    for (const key in state) {
        let value = state[key];
        str += key + "=" + value + ";";
    }
    return str.slice(0, -1);
}