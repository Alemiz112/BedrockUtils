# Bedrock Utils

This is a collection of some maybe useful tools for Bedrock Edition.
At the moment it mainly focuses on tools for working with Bedrock block palette.

## Nukkit Block Palette Generator
Using ``BlockUtils#main()`` a Nukkit compatible block palette can be generated from a Bedrock Edition block palette.

## Vanilla Block Palette Dumper
A Vanilla-like block palette can be nowadays obtained from the game using a custom addon located in ``BlockPaletteDumperAddon``.

The addon will create a JSON list of all known block states and will try to send it to HTTP server running on ``localhost:2001``.
You can use `http_server/` NodeJS app to receive the data.