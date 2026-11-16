# NEON DASH – Endless Runner Adventure 🏃‍♂️⚡

A feature-rich, futuristic 2D endless runner game built natively for Android using pure **Java**, **SurfaceView**, and **Android Canvas 2D graphics** with zero external game engine dependencies.

---

## 🌟 Overview

**NEON DASH** is an academic Mobile Application Development (MAD) project. The player controls a high-tech robotic runner sprinting through neon-lit futuristic environments while dodging dynamic obstacles, collecting cyber coins, activating power-ups, and completing challenges.

---

## ✨ Features

### 🎮 Gameplay Mechanics
- **Controls**:
  - **Single Tap**: Jump over low obstacles.
  - **Double Tap**: Double jump (in mid-air).
  - **Swipe Down**: Slide under aerial drones and high laser beams.
- **Dynamic Difficulty**: Running speed accelerates progressively as the distance/score increases.
- **6 Dynamic Obstacles**:
  - 🤖 Moving Patrol Robots
  - ⚡ Electric Energy Barriers
  - 🔺 Cyber Spikes
  - 🛸 Flying Drones
  - 🔴 Laser Obstacles
  - 💥 Falling Debris / Objects

### ⚡ 5 Unique Power-Ups
1. **🧲 Magnetic Field**: Attracts nearby coins automatically.
2. **🛡️ Energy Shield**: Grants invulnerability against one collision.
3. **🚀 Speed Boost**: High-speed sprint with bonus score multiplier.
4. **⏱️ Slow Motion**: Temporarily decelerates obstacle speed.
5. **🦘 Double Jump**: Empowers high-altitude leaping maneuvers.

### 🏙️ 4 Unlockable Realms (Parallax Scrolling)
- 🌆 **Neon City**: Glowing skyscrapers and futuristic metropolis.
- 🏜️ **Desert Run**: Dusty dunes, harsh sandstorms, and cyber cacti.
- 🌿 **Jungle Escape**: Overgrown cyber flora, bioluminescent trees, and mossy ruins.
- 🌐 **Cyber World**: Floating digital platforms and neon grids.

### 🤖 4 Playable Characters
- **Nova**: Balanced standard robotic sprinter.
- **Shadow**: Swift cyber-ninja with agile aerial mobility.
- **Bolt**: High-velocity speedster designed for maximum acceleration.
- **Titan**: Heavy-armored cyber-tank built for enduring high-friction impacts.

### 🏆 Game Modes & Directives
- **Classic Endless**: Pure endurance run with unbounded distance.
- **Time Challenge**: Survive for 60s, 120s, or 180s.
- **Coin Rush**: High-density coin frenzy within a strict countdown timer.
- **Missions & Achievements**: In-game objectives rewarding bonus coins.
- **Hall of Fame**: Persistent local SQLite leaderboard tracking top scores.

---

## 🛠️ Architecture & Tech Stack

- **Language**: Java 17 (100% Pure Java, no Kotlin)
- **UI Toolkit**: Native Android XML Layouts, Material Design Components
- **Game Engine**: Custom multithreaded `GameLoop` running on `SurfaceView` & `Canvas` (60 FPS lock)
- **Sound**: Native Android `SoundPool` with synthesized procedural waveforms (zero external audio assets needed)
- **Haptics**: `Vibrator` / `VibrationEffect` API
- **Persistence**:
  - `SQLiteOpenHelper` (`DatabaseHelper.java`) for leaderboard and mission tracking
  - `SharedPreferences` (`PreferencesManager.java`) for settings, unlocked characters, and currency
- **Offline First**: 100% fully functional without an internet connection

---

## 🚀 Getting Started

1. Clone or download this repository.
2. Open the project in **Android Studio** (Koala / Ladybug or newer).
3. Let Gradle sync dependencies.
4. Run on an Android Emulator or physical device (Min SDK: 24, Android 7.0+).

---

## 👤 Author
- **Sharanya Aithal KS** (USN: `NNM23CS177`)
- Department of Computer Science & Engineering, **NMAM Institute of Technology (NMAMIT), Nitte**
