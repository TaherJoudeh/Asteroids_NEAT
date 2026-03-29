# Asteroids AI — NEAT Algorithm

An AI agent that learns to play the classic **Asteroids** arcade game using the **NEAT** (NeuroEvolution of Augmenting Topologies) algorithm, built entirely in Java with the custom [NEAT4J](https://github.com/TaherJoudeh/NEAT4J) library.

---

## Demo

> *(Add a GIF or link to your demo video here)*

---

## Features

- **Autonomous gameplay** — AI pilots the ship and dodges/destroys asteroids with no human input
- **Generational evolution** — A population of 400 agents evolves each generation using NEAT; best genomes are saved to disk automatically
- **16-whisker raycasting** — The ship senses its environment via 16 evenly-spaced raycasts covering a full 360°, with screen-wrap awareness
- **Real-time genome visualizer** — Watch the agent's neural network topology update live as it plays
- **Dual fitness graph** — Built-in graph panel with two display modes (node and bar chart) tracking population fitness across generations, with mouse-hover value inspection
- **Batch viewer** — Cycle through all 400 live agents with the arrow keys while training
- **Human vs AI mode** — Toggle between manual play and AI training via a single flag in `Driver.java`

---

## How It Works

### Sensing — 22 Inputs

Each agent receives the following inputs every game tick:

| # | Input | Description |
|---|---|---|
| 1 | Speed | Normalized speed magnitude ∈ [-1, 1] |
| 2–3 | Velocity direction | cos(θ) and sin(θ) of the current velocity vector |
| 4–5 | Facing direction | cos(θ) and sin(θ) of the ship's nose direction |
| 6–21 | Whisker distances | 16 raycasts evenly distributed around the ship (length 500px each), returning normalized intersection distance to the nearest asteroid edge. All 8 screen-wrap ghost positions are checked per asteroid edge |
| 22 | Shooting cooldown | Normalized shooting readiness ∈ [-1, 1] |

### Acting — 4 Outputs

| Output | Threshold | Action |
|---|---|---|
| `out[0]` | > 0.5 | Thrust (else coast/decelerate) |
| `out[1]` | > 0.5 | Shoot |
| `out[2]` | > 0.5 AND > `out[3]` | Rotate clockwise |
| `out[3]` | > 0.5 AND > `out[2]` | Rotate counter-clockwise |

### Fitness Function

```
accuracy_score  = 0.4 + 0.6 × accuracy²
fitness         = 1 + survivedSeconds
fitness        *= 1 + 500 × noDangerTimes        // big reward for staying safe
fitness        += 200 × asteroidsShot² × accuracy_score
if (neverThrusted) fitness *= 0.2               // penalise idle agents
cooldownProp    = shootingCooldownWasted / survivedSeconds
fitness        *= 0.2 + 0.8 × (1 − cooldownProp) // penalise trigger spam
```

Key design choices:
- **`noDangerTimes`** heavily rewards agents that maintain distance from asteroids rather than just surviving by luck
- **Quadratic asteroid score** encourages agents that destroy many asteroids, not just one
- **Accuracy multiplier** on wasted cooldown discourages spamming bullets randomly

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| NEAT Engine | [NEAT4J](https://github.com/TaherJoudeh/NEAT4J) — custom library |
| Rendering | Java2D (AWT Canvas) |
| Build / IDE | Eclipse |

---

## NEAT Configuration

| Parameter | Value |
|---|---|
| Population size | 400 |
| Inputs / Outputs | 22 / 4 |
| Max hidden nodes | 30 |
| Recurrent connections | Yes (prob 0.4) |
| Init connectivity | Partial, no direct (prob 0.5) |
| Selection | Tournament (size 3) |
| Survival threshold | 40% |
| Target species | 15 (dynamic compatibility threshold) |
| Elitism | 2 genomes, 3 species |
| Stagnation limit | 20 generations |
| Add connection prob | 0.04 |
| Add node prob | 0.01 |
| Weight range | [−8, 8] |
| Activation (hidden) | Sigmoid |
| Activation (output) | Sigmoid |
| Aggregation | Sum |

---

## Getting Started

### Prerequisites

- Java 17+
- Eclipse IDE (or any Java IDE)
- [NEAT4J](https://github.com/TaherJoudeh/NEAT4J) — add as a dependency or JAR to your build path

### Running

1. Clone this repository
2. Add NEAT4J as a dependency in your build path
3. Run `Driver.java` as a Java Application

```java
// Driver.java — configure before running
public final static boolean fullScreen = true,
    AI = true,       // true = AI training mode, false = human play
    VALIDATE = true; // true = load & run a saved genome from genomes/
```

Best genomes are written to `genomes/` automatically after each generation.

---

## Project Structure

```
Asteroids_NEAT/
├── src/
│   ├── game/
│   │   ├── Driver.java            # Entry point & mode flags
│   │   ├── CanvasFrame.java       # Game loop, rendering, NEAT lifecycle
│   │   ├── World.java             # Per-agent simulation instance
│   │   ├── GameplayBox.java       # Game area layout & coordinate utilities
│   │   ├── AIBox.java             # AI panel layout
│   │   └── Window.java            # JFrame / fullscreen management
│   ├── objects/
│   │   ├── Player.java            # Ship: inputs, actions, fitness, whiskers
│   │   ├── Asteroid.java          # Asteroid physics & random generation
│   │   ├── Bullet.java            # Projectile & collision detection
│   │   ├── GameObject.java        # Base class (vertices, teleport, bounds)
│   │   ├── GameObjectHandler.java # Scene graph, update & render loop
│   │   ├── Spawner.java           # Asteroid spawning & splitting logic
│   │   ├── Vector.java            # 2D vector math utilities
│   │   └── Vertex.java            # 2D point with rendering helpers
│   ├── graph/
│   │   ├── Graph.java             # Fitness history data structure
│   │   ├── GraphHandler.java      # Graph rendering, scaling & layout
│   │   ├── ShapeGraph.java        # Abstract graph data point
│   │   ├── NodeGraph.java         # Dot-style graph point
│   │   └── RectangleGraph.java    # Bar-style graph point
│   └── input/
│       ├── KeyInput.java          # Keyboard controls & shortcuts
│       └── MouseInput.java        # Mouse hover over graph nodes
└── genomes/                       # Saved best genomes per generation
```

---

## Keyboard Shortcuts (during training)

| Key | Action |
|---|---|
| `←` / `→` | Switch to previous/next active agent |
| `Shift + ←/→` | Scroll fitness graph left/right |
| `G` | Cycle fitness graph (node → bar → off) |
| `[` / `]` | Jump to first/last section of graph |
| `H` | Toggle whisker overlay |
| `E` | Toggle disabled connections in genome view |
| `K` | Kill the currently viewed agent |
| `P` | Pause / unpause |
| `Esc` | Exit |

---

## Dependency — NEAT4J

This project is powered by **NEAT4J**, a custom Java implementation of the NEAT algorithm built from scratch by the same author.

[NEAT4J Repository](https://github.com/TaherJoudeh/NEAT4J)

---

## License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## Author

**Taher Joudeh**
- GitHub: [@TaherJoudeh](https://github.com/TaherJoudeh)
- LinkedIn: *(add your LinkedIn URL)*

---

## References

- [Stanley & Miikkulainen (2002) — Evolving Neural Networks through Augmenting Topologies](http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf)
- The classic Asteroids arcade game by Atari (1979)
