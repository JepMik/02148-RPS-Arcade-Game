# Rock, Paper, Scissor Arcade Game
## Developed for the course: 02148 - Introduction to Coordination in Distributed Applications at DTU.
#### Authors: Jeppe Mikkelsen s204708, Arthur Bosquetti s204718, Maximilian Jørgensen s204178 & Arooj Chaudhry s204759.

## Theme of Game
A basic Rock, Paper, Scissor Arcade game, that uses a client- server architecture for communicaiton.
The Game is played best out of 3's, and draw does not count.
Winner gets to stay, and the spectators queue is used for finding new opponents.
Scoreboard is added for seeing the highscore: "Who won most matches".
Chat is added, and allows for communication between players, and the spectators.

## Libraries
The application was built using the library called [JSpace](https://dtu.bogoe.eu/02148/) which allows for easy coordination of distributed system using tuple spaces.
Guide for installing, using can also be found in JSpace link.

## Run the Game
Skip to step 2 if a server is already up and running.

1. To run the game first a Server must be setup. Run the Server class, and proceed to enter the ip-adress of your device in the terminal.
2. Run the Client class, and proceed to enter the ip-adress of the server you are trying to join and your username.

Remember that 2 players are needed for the Server to start the game.



