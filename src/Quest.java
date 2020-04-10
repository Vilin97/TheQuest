import java.util.*;
import java.lang.Math;
import java.util.stream.Stream;


public class Quest {
    // the class for the Quest game
    private Map map;
    private TeamHeroes heroes;
    public static final String path = "C:/Users/Vasily/GitHub/Java/TheQuest/data/";
    private static final double marketProb = 0.3;
    private static final double emptyProb = 0.5;
    private static final double encounterProb = 0.7;
    private static final String[] actionKeys = {"w","a","s","d","q","i"};
    private static final String actionLegend = String.format(
            "%s = up, %s = left, %s = down, %s = right, %s = quit game, %s = display information",
            actionKeys[0],actionKeys[1],actionKeys[2],actionKeys[3],actionKeys[4],actionKeys[5]);
    private boolean gameEnded;
    private static final int maxTeamSize = 3;

    public Quest(Map map, TeamHeroes heroes) {
        this.map = map;
        this.heroes = heroes;
        this.gameEnded = false;
    }

    public Quest() {
        this.gameEnded = false;
    }

    public void play() {
        Scanner in = new Scanner(System.in);
        initializeHeroes();
        initializeMap();
        System.out.println(map);
        System.out.println(Map.legend);
        while (!gameEnded) {
            System.out.println(actionLegend);
            System.out.println("Your move:");
            String action = in.nextLine();
            processAction(action);
            System.out.println(map);
        }
        endGame();
    }

    private void endGame() {
        System.out.println("Good game! Here are your heroes' stats:");
        for (Unit unit : heroes.getUnits()) {
            Hero hero = (Hero) unit;
            System.out.println( hero.detailedToString());
        }
        System.out.println("~~~G A M E   O V E R~~~");
    }

    private void processAction(String action) {
        int[] newLocation = map.getHeroLocation();
        boolean moved;
        if (!Arrays.asList(actionKeys).contains(action)) {
            System.out.println("Your move was not recognized. Try again:");
        }
        else if (action.equals(actionKeys[0])) { newLocation = new int[]{newLocation[0] - 1, newLocation[1]}; }
        else if (action.equals(actionKeys[1])) { newLocation = new int[]{newLocation[0], newLocation[1] - 1}; }
        else if (action.equals(actionKeys[2])) { newLocation = new int[]{newLocation[0] + 1, newLocation[1]}; }
        else if (action.equals(actionKeys[3])) { newLocation = new int[]{newLocation[0], newLocation[1] + 1}; }
        else if (action.equals(actionKeys[4])) {
            gameEnded = true;
        }
        else if (action.equals(actionKeys[5])) {
            processInformationAction();
        }

        int[] oldLocation = map.getHeroLocation();
        map.moveHeroes(newLocation);
        int[] location = map.getHeroLocation();
        moved = (oldLocation != location);

        if (moved) {
            Cell cell = map.getPosition()[location[0]][location[1]];
            if (cell.isEmptyCell() ) {
                processEmpty();

            }
            else if (cell.isMarketCell()) {
                processMarket(cell);
            }
        }

    }

    private void processEmpty() {
        // process empty cell encounter
        double r = Math.random();
        if (r < encounterProb) {
            fight();
        }
    }

    private void fight() {
        // spawn monsters and fight them
        TeamMonsters monsters = initializeMonsters();
        System.out.println("Heroes are fighting a group of monsters! Here are the opponents:\n"+monsters.toString());
        Fight fight = new Fight(heroes, monsters);
        fight.fight();
        setHeroes(fight.getHeroes());
        System.out.println("Here is what your team now looks like:\n"+heroes.toString());
    }

    private TeamMonsters initializeMonsters() {
        Monster[] dragons = ReadFile.readMonsterData(path+"Dragons.txt", "D");
        Monster[] spirits = ReadFile.readMonsterData(path+"Spirits.txt", "S");
        Monster[] exoskeletons = ReadFile.readMonsterData(path+"Exoskeletons.txt", "E");
        Monster[] availableMonsters = new Monster[dragons.length + spirits.length + exoskeletons.length];
        System.arraycopy(dragons, 0, availableMonsters, 0, dragons.length);
        System.arraycopy(spirits, 0, availableMonsters, dragons.length, spirits.length);
        for (int i = 0; i < exoskeletons.length; i++) { availableMonsters[i + dragons.length + spirits.length] = exoskeletons[i]; }

        int highestLevel = heroes.getHighestLevel();
        List<Monster> availableMonstersFilteredByLevel = new ArrayList<>();
        for (Monster availableMonster : availableMonsters) {
            if (availableMonster.getLevel() <= highestLevel) {
                availableMonstersFilteredByLevel.add(availableMonster);
            }
        }
        availableMonsters = new Monster[availableMonstersFilteredByLevel.size()];
        availableMonsters = availableMonstersFilteredByLevel.toArray(availableMonsters);

        Monster[] chosenMonsters = new Monster[heroes.getUnits().length];
        for (int i = 0; i < chosenMonsters.length; i++) {
            int id = new Random().nextInt(availableMonsters.length);
            chosenMonsters[i] = availableMonsters[id];
        }
        return new TeamMonsters(chosenMonsters);
    }

    private void processInformationAction() {
        // process input of i
        Scanner in = new Scanner(System.in);
        System.out.println("Your team is:\n"+heroes);
        System.out.println("Select a hero whose equipment you want to alter (1, 2 or 3) or "+actionKeys[4]+" to quit this menu.");
        String ans = IOTools.getValidatedInput("1,2,3,q".split(","));
        if (ans.equals(actionKeys[4])) { // do nothing
        }
        else {
            Unit[] units = heroes.getUnits();
            int heroID = Integer.parseInt(ans)-1;
            if (heroID < 0 || heroID >= units.length) {
                System.out.println("There is no such hero in the team. Quitting.");
            } else {
                Hero hero = (Hero) units[heroID];
                System.out.println("You selected:\n"+hero.detailedToString());
                units[heroID] = alterHero(hero);
                heroes.setUnits(units);
            }
        }
    }

    private static Hero alterHero(Hero hero) {
        // let the player alter hero
        boolean notDone = true;
        while (notDone) {
            hero = alterHeroOnce(hero);
            System.out.println("Would you like to do anything else with this hero? y/n");
            String ans = IOTools.getValidatedInput("y,n".split(","));
            if (ans.equals("n")) notDone = false;
        }
        return hero;
    }

    protected static Hero alterHeroOnce(Hero hero) {
        System.out.println("e = equip item; u, w = unequip the current weapon; \n" +
                "u, a = unequip the current armor; d = drink potion; q = quit. \n" +
                "Your choice:");
        String ans = IOTools.getValidatedInput("e;u,w;u,a;d;q".split(";"));
        if (ans.equals("e")) {
            int[] ids = General.flattenIntArray(
                    new int[][]{General.arrayPlusOne(hero.getBackpack().getIds("Armor")),
                            General.arrayPlusOne(hero.getBackpack().getIds("Weapon"))});
            if (ids.length == 0) {
                System.out.println("There are no items to equip. Choose another action.");
                alterHeroOnce(hero);
            } else { System.out.println("Choose what item to equip: ");
                int id = IOTools.getIntInput(ids)-1;
                hero.equip(id); }
        }
        else if (ans.equals("u,w")) {
            if (hero.getWeapon().isEmpty()) {
                System.out.println("The hero already has no weapon. Choose another action.");
                alterHeroOnce(hero);
            }
            hero.unequipWeapon();
        }
        else if (ans.equals("u,a")) {
            if (hero.getWeapon().isEmpty()) {
                System.out.println("The hero already wears no armor. Choose another action.");
                alterHeroOnce(hero);
            }
            hero.unequipArmor();
        }
        else if (ans.equals("d")) {
            System.out.println("Choose what potion to drink: ");
            int[] ids = General.arrayPlusOne(hero.getBackpack().getIds("Potion"));
            if (ids.length == 0) {
                System.out.println("There are no potions to drink. Choose another action.");
                alterHeroOnce(hero);
            } else {
            int id = IOTools.getIntInput(ids)-1;
            hero.drinkPotion(id); }
        }
        else if (ans.equals(actionKeys[4])) { return hero; }
        System.out.println("Have a look at the result of your action:\n"+hero.detailedToString());
        return hero;
    }

    private ArrayList<String> heroesIndices() {
        ArrayList<String> res = new ArrayList<>();
        for (int i: heroes.getUnitIDs()) {
            res.add(Integer.toString(i));
        }
        return res;
    }

    private void processMarket(Cell cell) {
        // process market interaction
        Scanner in = new Scanner(System.in);
        Market market = (Market) cell;
        System.out.println(market.getWelcomeMessage());
        System.out.println("Here is what the market has to offer:");
        market.displayGoods();
        boolean notDone = true;
        while (notDone) {
            System.out.println("Select the hero to interact with the market by typing the hero ID or "+actionKeys[4]+" to quit the market:");
            ArrayList<String> inputs = heroesIndices();
            inputs.add(actionKeys[4]);
            String ans = IOTools.getValidatedInput(inputs);
            if (ans.equals(actionKeys[4])) notDone = false;
            else {
                int heroID = Integer.parseInt(ans)-1;
                Hero hero = (Hero) heroes.getUnits()[heroID];
                hero.tradeWithMarket(market);
            }
        }
    }

    private void initializeMap() {
        map = new Map();
        map.setRandomPosition(marketProb, emptyProb);
    }

    private void initializeHeroes() {
        // let the player choose the team of heroes
        Hero[] warriors = ReadFile.readHeroData(path+"Warriors.txt", "W");
        Hero[] sorcerers = ReadFile.readHeroData(path+"Sorcerers.txt", "S");
        Hero[] paladins = ReadFile.readHeroData(path+"Paladins.txt", "P");
        Hero[] availableHeroes = new Hero[warriors.length + sorcerers.length + paladins.length];
        System.arraycopy(warriors, 0, availableHeroes, 0, warriors.length);
        System.arraycopy(sorcerers, 0, availableHeroes, warriors.length, sorcerers.length);
        for (int i = 0; i < paladins.length; i++) { availableHeroes[i + warriors.length + sorcerers.length] = paladins[i]; }
        Scanner in = new Scanner(System.in);
        System.out.println("Hello! Welcome to The Quest!\n" +
                "Here is the list of available heroes:");
        System.out.println("ID | class | name | mana | strength | agility | dexterity | starting money | starting experience");
        System.out.println(General.myArrayToString(availableHeroes));
        System.out.println("Please choose your team of heroes by entering one, two or three IDs in format: 1,2,3:");
        boolean teamOk = false;
        while (!teamOk) {
            try {
                String selection = in.nextLine();
                String[] ids_ = selection.replace(" ", "").split(",");
                int[] ids = new int[ids_.length];
                for (int i = 0; i < ids.length; i++) { ids[i] = Integer.parseInt(ids_[i])-1; }
                Hero[] selected = new Hero[ids.length];
                for (int i = 0; i < selected.length; i++) { selected[i] = availableHeroes[ids[i]]; }
                heroes = new TeamHeroes(selected);
                if (heroes.getUnits().length <= maxTeamSize) {
                    teamOk = true;
                }
            }
            catch (Exception e) {
                System.out.println("Input not recognized. Try again:");
            }
        }
        System.out.println(heroes);
    }


    public TeamHeroes getHeroes() {
        return heroes;
    }

    public void setHeroes(TeamHeroes heroes) {
        this.heroes = heroes;
    }

}
