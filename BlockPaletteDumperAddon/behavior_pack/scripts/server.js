import {
    BlockStates,
    BlockTypes,
    BlockPermutation
} from "@minecraft/server";

import {
    http,
    HttpRequest,
    HttpRequestMethod
} from "@minecraft/server-net";

let data = {
    blocks: [],
    properties: BlockStates.getAll()
}

let blocks = BlockTypes.getAll();
for (let i = 0; i < blocks.length; i++) {
    let permutation = BlockPermutation.resolve(blocks[i].id);
    let defaultPermutation = permutation.getAllStates();
    let defaultPermutationString = JSON.stringify(defaultPermutation);
    let blockData = {
        blockId: blocks[i].id,
        possibleProperties: Object.keys(defaultPermutation),
        states: {}
    }
    let stateNames = Object.keys(defaultPermutation);
    for (let j = 0; j < stateNames.length; j++) {
        let stateName = stateNames[j];
        let state = BlockStates.get(stateName);
        let validValues = [state.validValues[0]]
        for (let k = 1; k < state.validValues.length; k++) {
            let str = JSON.stringify(permutation.withState(stateName, state.validValues[k]).getAllStates());
            if (str !== defaultPermutationString) {
                validValues.push(state.validValues[k])
            }
        }
        blockData.states[stateName] = validValues
    }
    data.blocks.push(blockData)
}

let request = new HttpRequest("http://localhost:2001");
request.setMethod(HttpRequestMethod.Post);
request.setBody(JSON.stringify(data));
http.request(request);

// console.warn(JSON.stringify(data));