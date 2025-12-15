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

import * as GameTest from "@minecraft/server-gametest";

const BOUNDS = {
    minX: 1, maxX: 64,
    minY: 1, maxY: 127,
    minZ: 1, maxZ: 64
};

let currentPos = { x: BOUNDS.minX, y: BOUNDS.maxY, z: BOUNDS.minZ };

world.afterEvents.worldLoad.subscribe(async () => {
    await world.tickingAreaManager.createTickingArea("state-dumper-tool", { dimension: world.getDimension("overworld"), from: { x: BOUNDS.minX, y: BOUNDS.minY, z: BOUNDS.minZ }, to: { x: BOUNDS.maxX, y: BOUNDS.maxY, z: BOUNDS.maxZ } });
    console.warn("Ready!");
});

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


function rgbaToHex({ red, green, blue, alpha = 1 }) {
    const toHex = v => Math.round(v * 255).toString(16).padStart(2, "0");
    return `#${toHex(red)}${toHex(green)}${toHex(blue)}${toHex(alpha)}`;
}

function prepareStateData(test, pos, current, data) {
    //console.warn(current.type.id, stringifyState(current.getAllStates()), pos.x, pos.y, pos.z);
    data.tags = current.getTags();
    test.setBlockType(BlockTypes.get("minecraft:stone"), pos);
    test.setBlockPermutation(current, pos);
    let block = test.getBlock(pos);
    //console.warn(block.type.id);
    data.mapColor = rgbaToHex(block.getMapColor());
    data.isWaterlogged = block.isWaterlogged;
    data.localizationKey = block.localizationKey;
    data.isLiquid = block.isLiquid;
    data.isSolid = block.isSolid;
    let precipitationInteractions = block.getComponent("minecraft:precipitation_interactions");
    data.precipitationInteractions = {
          obstructsRain: precipitationInteractions.obstructsRain(),
          accumulatesSnow: precipitationInteractions.accumulatesSnow()
    };
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

async function dumpAll(test) {
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
    let pos = nextPos();
    let inc = 0;
    for (let i = 0; i < blocks.length; i++) {
        let permutation = BlockPermutation.resolve(blocks[i].id);
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
                    blockData.data[stateId] = prepareStateData(test, pos, current, blockData.data[stateId] || {});
                } catch(e) {
                    console.error(e);
                }
            }
        }
        if (blockData.properties.length == 0) {
            blockData.data[""] = prepareStateData(test, pos, permutation, {});
        }
        data.blocks.push(blockData)
        inc++;
        if (inc > 16) {
            // For some stupid reason, when we keep reusing the same position, it causes the server to crash
            // As a workaround, we use multiple locations 
            pos = nextPos();
        }
    }

    let request = new HttpRequest("http://localhost:2001");
    request.setMethod(HttpRequestMethod.Post);
    request.setBody(JSON.stringify(data));
    http.request(request);
    test.succeed();
}

GameTest.register("blockdumptool", "dump_all", dumpAll).maxTicks(1000)
    .structureName("blockdumptool:dump_all")
    .structureLocation({x: 0, y: 0, z: 0 });