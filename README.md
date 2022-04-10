# Software Engineering Project AY 2021-2022, Group AM26: Eriantys

<img src="https://craniointernational.com/2021/wp-content/uploads/2021/06/ERIANTYS-BOX-3D.png" height=256px alt="Eriantys Box Image"/>

Eriantys is a board game whose reimplementation using the Java language is the final test of the **Software Engineering** course.

**Teacher** Alessandro Margara

### Project requirements
The project consists in the reimplementation of the board game **Eriantys** created by Cranio Creations. The game has to be implemented using Java, and must run on a distributed architecture.

The final version will include:
* The initial UML diagram;
* The final UML diagram, automatically generated from the source code;
* The source code of the game, developed using Java 17;
* The source code of Unit Tests, developed using JUnit 5.

### Implemented Features
| Feature          |                                                                                      Implementation Status                                                                                      |
|:-----------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| Basic ruleset    |                [![IMPLEMENTING](https://img.shields.io/badge/-Implementing-yellow)](https://github.com/AleSassi/ingsw2022-AM26/tree/master/src/main/java/it/polimi/ingsw/model)                 |
| Complete ruleset |                [![IMPLEMENTING](https://img.shields.io/badge/-Implementing-yellow)](https://github.com/AleSassi/ingsw2022-AM26/tree/master/src/main/java/it/polimi/ingsw/model)                 |
| Socket & Network |                                                        [![To be implemented](https://img.shields.io/badge/-To%20be%20implemented-red)]()                                                        |
| CLI              |                                                        [![To be implemented](https://img.shields.io/badge/-To%20be%20implemented-red)]()                                                        |
| GUI              |                                                        [![To be implemented](https://img.shields.io/badge/-To%20be%20implemented-red)]()                                                        |
| Persistence      |                                                        [![To be implemented](https://img.shields.io/badge/-To%20be%20implemented-red)]()                                                        |
| All Characters   |         [![IMPLEMENTED](https://img.shields.io/badge/-Implemented-brightgreen)](https://github.com/AleSassi/ingsw2022-AM26/tree/master/src/main/java/it/polimi/ingsw/model/characters)          |
| Team Matches     | [![IMPLEMENTED](https://img.shields.io/badge/-Implemented-brightgreen)](https://github.com/AleSassi/ingsw2022-AM26/blob/master/src/main/java/it/polimi/ingsw/model/match/TeamMatchManager.java) |

### Test cases report
Test cases are currently being implemented progressively as new classes are created. The following table will provide a report of the coverage, as shown by Maven in IntelliJ IDEA.
Test cases aim for 100% coverage.

| Package | Tested Class   | Line Coverage | Method Coverage |
|:--------|:---------------|:-------------:|:---------------:|
| Model   | Global Package | 95% (605/631) |  95% (172/180)  |

### The Developers
- **Alessandro Sassi** (email: alessandro5.sassi@mail.polimi.it)
- **Federico Albertini** (email: federico.albertini@mail.polimi.it)
- **Leonardo Betti** (email: leonardo.betti@mail.polimi.it)

### Software used
- **diagrams.net** - UML diagrams
- **Intellij IDEA Ultimate** - Java IDE

### Copyright and license

The Eriantys board game is copyright 2021 Cranio Creations ltd.