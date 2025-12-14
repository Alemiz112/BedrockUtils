# Bedrock Utils

This is a collection of some maybe useful tools for Bedrock Edition.
At the moment it mainly focuses on tools for working with Bedrock block palette.

## Nukkit Block Palette Generator
Using ``BlockUtils#main()`` a Nukkit compatible block palette can be generated from a Bedrock Edition block palette.

## Vanilla Block Palette Dumper

A vanilla-like block palette can be obtained from the game using the custom addon located in `BlockPaletteDumperAddon`. When loaded, the addon generates a JSON list of all known block states and sends it to an HTTP server running on `localhost:2001`. You can use the NodeJS app in `http_server/` to receive the data. It also will dump any custom blocks and their states as well.

### BDS Setup

This tool requires BDS (Bedrock Dedicated Server). To set it up:

1. Copy the `behavior_pack` directory into the `development_behavior_packs` folder of your BDS installation.

2. Enable the `@minecraft/server-net` module by modifying `config/default/permissions.json`:
```diff
 {
   "allowed_modules": [
     "@minecraft/server-gametest",
     "@minecraft/server",
     "@minecraft/server-ui",
     "@minecraft/server-admin",
     "@minecraft/server-editor",
+    "@minecraft/server-net"
   ]
 }
```

### HTTP Server Setup

1. Navigate to the `http_server/` directory.

2. Install the dependencies:
```bash
npm install
```

This will install the required packages from `package.json`

1. Start the server:
```bash
node index.js
```

The server will listen on port 2001 and save the received block palette data from BDS.