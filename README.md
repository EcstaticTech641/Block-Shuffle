# BlockShuffle

A Block Shuffle mini-game plugin for Minecraft Paper servers. Each round, all players are assigned a block to find and stand on.

---
## Usage

Run `/blockshuffle` (requires `blockshuffle.admin` permission, default: op) to open the game menu.

| Menu Item | Mode |
|---|---|
| Grass Block | Standard BlockShuffle |
| Stone | Simple BlockShuffle (easy materials) |
| Netherrack | Nether BlockShuffle |
| Lime Wool | Colour BlockShuffle |
| Book | Custom BlockShuffle (user-defined list) |

Run `/blockshuffle stop` to end a game in progress.

---
## Configuration

Edit `plugins/BlockShuffle/settings.yml` after first run to customise material lists and messages.

---
## Changelog

### v0.3.0

- Changed the timer system to increase when no player's find their block.
- Changed scoreboard to show round number instead of players' blocks found.

---
### v0.2.3

- Added announcement titles for the winner when a game ends naturally.
- Changed sound that plays when a player gains a point.
- Changed when the RGA conclusion command runs after a game finishes naturally.

### v0.2.2

- Changed sound that plays when player fails to find their block.
- Fixed formatting issue that showed red placeholder numbers on each row of the scoreboard.
- Fixed the RGA conclude command to be called when blockshuffle stop command is initiated by a player.

### v0.2.1

- Fixed bug that prevented sound effects for players finding their block and failing to find their block.
- Patch for formatting issue that showed red placeholder numbers on each row of the scoreboard.

### v0.2.0

- Added player's currently tasked block above the hotbar.
- Added indicator above hotbar when a player's block is found.
- Added scoreboard that tracks current players, how many points they have, how many blocks they have found, and whether or not they have found their current block.

### v0.1.2

- Added sound effects for when a player finds their block and fails to find their block.
- Changed logic of scoring system.

### v0.1.1

- Added RGA conclusion command integration.

### v0.1.0

- Recreated blockshuffle plugin.
- Changed when the timer decreases.

---
## Original Plugin

This plugin was initially made by Dream, and updated to Minecraft version 1.18 by OliverB21. Dream's plugin is not public, but OliverB21's plugin can be found below:

https://github.com/OliverB21/BlockShuffle

---
