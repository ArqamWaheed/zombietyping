# TypingMaster — Zombie Survival (Java)

Java Swing desktop game built for the **CS-212 OOP Semester Project** (NUST SEECS).
Pure JDK — no external libraries.

## Build & run
```bash
./build.sh   # compiles src/ → out/ and packages TypingMaster.jar
./run.sh     # builds (if needed) and launches the game
```
Or manually:
```bash
javac -d out $(find src -name "*.java")
java -cp out com.typingmaster.Main
```

## MVC layout
```
src/com/typingmaster/
├── Main.java                       — entry point, wires MVC
├── model/                          — game state + rules (no UI deps)
│   ├── Entity.java                 — abstract base
│   ├── Zombie.java  → Runner / Walker / Tank
│   ├── Bullet.java
│   ├── WordBank.java               — internal dictionary
│   └── Game.java                   — the world
├── view/
│   ├── Theme.java                  — color palette
│   └── GameView.java               — JPanel renderer (Java2D)
└── controller/
    └── GameController.java         — Swing Timer loop + key listener
```

## OOP concepts demonstrated
- **Encapsulation** — `Game` owns and protects state; consumers go through `start`, `update`, `typeChar`.
- **Inheritance** — `Runner`, `Walker`, `Tank` extend `Zombie`, which extends `Entity`. `Bullet` also extends `Entity`.
- **Polymorphism** — every `Entity` overrides `update(double dt)`; the loop just iterates and calls it.
- **Abstraction** — `Entity` is `abstract` and exposes the shared interface without committing to behavior.

## Controls
- Type the first letter of any word to **lock on** to that zombie.
- Finish typing the word to kill it. Word above your locked target turns red.
- **Enter** to start / restart.
