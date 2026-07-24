# Block-Shuffle Minigame Plugin

A modern, high-performance Block Shuffle minigame plugin built for Minecraft / Paper 26.1 running on Java 25. Includes native event-driven integration with Ronlab Game Assistant (RGA) (`com.ronlab:rga-api:1.11.0`) alongside traditional standalone command and GUI menu capabilities.

---

## Features

- **Paper 26.1 & Java 25 Ready**: Built on the Paper API standard with modern Adventure formatting, Scoreboard NumberFormat APIs, and JSpecify `@NullMarked` nullability annotations.
- **RGA API Integration**: Supports event-driven session lifecycle via `MinigameStartEvent` and `MinigameConcludeEvent`. Automatically handles session initialization, target world/player payload mapping, and score reporting.
- **Dynamic Timer Mechanics**: Automatically scales round duration (1 to 5 minutes) based on player success across rounds.
- **Visual Feedback**: Real-time boss bars, custom sidebars without raw score numbers, action bar target block indicators, and audio cues.
- **Standalone GUI & Commands**: Fully functional without external plugins via `/blockshuffle` menu and commands.

---

## Requirements & Compatibility

| Component | Target Version |
| :--- | :--- |
| **Java JDK** | 25+ |
| **Server Engine** | Paper 26.1.2-R0.1-SNAPSHOT or later |
| **Optional Companion** | RonlabGameAssistant 1.11.0+ |

---

## Building from Source

```bash
# Clone the repository
git clone https://github.com/Ronlab/Block-Shuffle.git
cd Block-Shuffle

# Build with Maven
mvn clean package
```

The output JAR will be located in `target/BlockShuffle-2.1.0.jar`.

---

## Usage & Commands

### Standalone Mode
- `/blockshuffle` - Opens the interactive game mode selector GUI (Grass, Nether, Colour, Custom modes).
- `/blockshuffle stop` - Stops the active game session and resets player scoreboards/bossbars.

### RGA Event Mode
When `RonlabGameAssistant` is present on the server, `BlockShufflePlugin` registers `RGAEventListener`:
- **Session Start**: Responds to `MinigameStartEvent` with `minigameId = "blockshuffle"`, parsing the targeted world and participant UUIDs.
- **Session Conclusion**: Responds to `MinigameConcludeEvent`, writing final scores into `event.getScores()` for leaderboard and reward processing.

See `docs/companion-integration.md` for complete integration details.

---

## License
Distributed under the MIT License.
