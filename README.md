# 5190 Offseason 2018

[![Build Status](https://dev.azure.com/frc5190/FRC%202018%20Power%20Up/_apis/build/status/Offseason%20Code)](https://dev.azure.com/frc5190/FRC%202018%20Power%20Up/_build/latest?definitionId=3)


This repository is broken up into various submodules. Each submodule contains a different aspect of the FRC Team 5190 Offseason projects or FRC Team 5190 unreleased projects during the 2018 POWER UP competition season.

### ```robot```

#### Robot code with increased efficiency. Some features include

* 3 cube autonomous
* Closed loop PID control on elevator, arm, and climber
* Voltage compensation for predictable open loop feedback
* Commands with intelligent state-based execution
* Coroutine enabled subsystems and actions
* Custom Path Follower with (x, y, theta) error correction
* Efficient code through Kotlin JVM language

##### Instructions for Robot

* Open IntelliJ IDEA, click 'Open' and click on the '5190-Offseason-2018' Gradle Icon.
* Use the terminal to deploy code: ```.\gradlew deploy```

### ```strategysim```

#### Developed by 5190 Programming Mentors to simulate robot strategy. Some features include

* Various strategy modes for simulation
* Options to play against the computer
* Easy-to-use web app interface through Electron and TypeScript

##### Instructions for Strategy Simulator

* Download and install ```node.js```. This installation is bundled with ```npm```
* Install ```npm``` dependencies: ```npm install```
* Compile TypeScript: ```tsc```
* Use the terminal to run the program: ```npm start```
