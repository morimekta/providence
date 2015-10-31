/* -*- thrift -*-
 *
 * Game Data objects
 *
 */

namespace java   no.norrs.spacemunk

enum Platform {
  API = 1,
  WEB = 2,
  ANDROID = 3,
}


enum ResourceConstraint {
  NORMAL = 1;
  ADVANCED = 2;
}

enum ResourceType {
 RESEARCH = 1,
 MONEY = 2,
 PRODUCTION = 3
}

struct Resource {
 1: optional  ResourceConstraint constraint,
 2: optional ResourceType type
}

struct Planet {
   1: optional list<Resource> resources;
}



enum ExtensionType {
 ORBITAL = 1,
 MONOLITH = 2,
 STARBASE = 3
}

struct Extension {
 1: optional ExtensionType type
 2: optional Planet planet
}

enum ShipClass {
 DREADNOUGHT = 1,
 BATTELCRUISER = 2,
 INTERCEPTOR = 3,
}

struct Ship {
 1: optional string name;
 2: optional ShipClass type;
 3: optional Player owner;
 4: optional list<Upgrade> protected_slots;
 5: optional list<Upgrade> slots;
 6: optional i32 cost;
}

enum Race {
 HUMAN = 1,
 PLANTS = 2,
 ROCKS = 3
}

enum PlayerPhaseType {
 ACTIVE = 1,
 PASSED = 2,
}

struct Player {
 1: optional Race race;
 2: optional PlayerPhaseType phaseState
 3: optional list<Upgrade> shipUpgrades;
 4: optional list<Upgrade> techs;
 5: optional map<Resource, i32> resources
 6: optional list<Upgrade> unusedUpgrades;
 /** Max four badges */
 7: optional list<Badge> badgeSlots;
 /** Current sum of victory points */
 8: optional i32 victoryPoints;
}

struct Diplomat {
 1: optional Resource resource;
 2: optional Player owner;
}

struct Badge {
  1: optional Diplomat diplomat;
  2: optional i16 reputation;
}

enum UpgradeType {
 MILITARY = 1,
 GRID = 2,
 NANO = 3,
 ALIEN = 4,
}

struct Upgrade {
 1: optional string name;
 2: optional UpgradeType type;
  3: optional list<i32> weapons;
  4: optional i32 computer;
  5: optional i32 shield;
  6: optional i32 hull;
  7: optional i32 generator;
  8: optional i32 energyDemand;

  9: optional Upgrade upgrade;
  10: optional Extension allowsToBuild;
  11: optional i32 influenceDisks;
  12: optional Resource advancedresource;
}

enum PhaseType {
 EXPLORE = 1,
 INFLUENCE = 2,
 RESEARCH = 3,
 UPGRADE = 4,
 BUILD = 5,
 MOVE = 6,
}

struct ActionPhase {
 1: optional PhaseType type;
  2: optional i32 amount;
  3: optional i32 reactionAmount;
  // WTF IS EXCEPTION DECORATOR?! :D
}


struct Coordinates {
 1: optional i32 x;
 2: optional i32 y;
}

enum GamePhase {
 ACTIONS = 1,
 COMBAT = 2,
 PLAYER_UPKEEP = 3,
 GAME_UPKEEP = 4
}

struct Game {
  1: optional map<Coordinates, Hex> map;
  /** Picked on combat won against alien/npc */
  2: optional list<Upgrade> upgrades;
  3: optional list<Player> orderedGameOverPlayers;
  4: optional list<Player> orderedPlayers;
  5: optional GamePhase currentPhase;
  6: optional list<Upgrade> availableTechs; // is this upgrade or techonology?
  /** List of tiles availle over each zone from galactic center. */
  7: optional map<i16, list<Hex>> availableTiles;
}


struct Hex {
    1: optional Player controller;
    2: optional i16 zone;
    3: optional list<Planet> planets;
    4: optional Extension extention;
    5: optional list<Ship> ships;
    6: optional list<i16> warps;
    /** clockwise */
    7: optional i16 rotation;
    8: optional list<Ship> aliens;
    //9: optional GameObject bonus;
}
