# TheQuest
Object Oriented Quest. The player controls a team of heroes, fights monsters, gets money and experience, gets upgrades at markets

To run do: javac Main.java
	         java Main

The prompts are (hopefully) comprehensive enough to guide you through the gameplay.

My classes are (in approximate order of importance):
Main -- main
Quest -- the class controlling the gameplay of the quest game. Has functions like "processAction" to process the player's action.
Map -- class for the map. Similar to the Board class from Tic Tac Toe. Map consists of cells
Cell -- abstract class representing a cell on the map
Market -- class extending Cell. Represents a market. Has items and spells that can be bought.
Inaccessible and Empty -- classes extending Cell. Represent the respective tiles.
Unit -- abstract class representing a unit. Has things like HP.
Hero -- abstract class representing a hero. Has things like experience, mana, skillset, backpack, stats
Warrior, Paladin, Sorcerer -- classes extending Hero. Have the levelUP function.
SkillSet -- class representing skillset of a hero. Contains skills.
Backpack -- class representing backpack of a hero. Contains items.
Stats -- class representing the stats of a hero. Contains strength, agility and dexterity.
Monster -- abstract class representing a monster. Has things like damage and dodgeChance.
Dragon, Exoskeleton, Spirit -- classes extending Monster.
Item -- class representing an item.
Potion, Weapon, Armor -- classes extending item.
Skill -- abstract class representing a skill.
FireSpell, IceSpell, LightningSpell -- classes extending Skill.
Team -- abstract class representing a team of units.
TeamHeroes -- class extending Team. Represents a team of heroes.
TeamMonsters -- class extending Team. Represents a team of monsters.
ReadFile -- class to read from files. Has functionality to read in hero, monster, item and spell data
General -- class containing public static functions that should be prebuilt in any language like Python but for some reason are missing in Java.
IOTools -- class containing functions to take input from user. 
