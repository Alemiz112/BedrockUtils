import {
    BlockStates,
    BlockTypes,
    BlockPermutation,
    world
} from "@minecraft/server";

import {
    http,
    HttpRequest,
    HttpRequestMethod
} from "@minecraft/server-net";

// wait until the world is loaded
world.afterEvents.worldLoad.subscribe(() => {
    let data = {
        blocks: [],
        properties: BlockStates.getAll()
    }
    for (let i = 0; i < data.properties.length; i++) {
        data.properties[i] = {
            "id": data.properties[i].id,
            "validValues": BlockStates.get(data.properties[i].id).validValues
        };
    }

    let blocks = BlockTypes.getAll();
    for (let i = 0; i < blocks.length; i++) {
        let permutation = BlockPermutation.resolve(blocks[i].id);
        let defaultPermutation = permutation.getAllStates();
        let blockData = {
            blockId: blocks[i].id,
            properties: Object.keys(defaultPermutation)
        }
        data.blocks.push(blockData)
    }

    let request = new HttpRequest("http://localhost:2001");
    request.setMethod(HttpRequestMethod.Post);
    request.setBody(JSON.stringify(data));
    http.request(request);
    //console.warn(JSON.stringify(data));
});

