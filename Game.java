/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @version 1.0 (February 2002)
 */

class Game 
{
    private Parser parser;
    private Player player;
    private byte count;
    
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
    	player = new Player();
        createRooms();
        parser = new Parser();
        count = 1;
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        Room outside, theatre, pub, lab, office, attic, backstage, lake, toilets, storage, sewers, showroom;
      
        // create the rooms
        outside = new Room("outside the main entrance of the university");
        theatre = new Room("in a lecture theatre");
        pub = new Room("in the campus pub");
        lab = new Room("in a computing lab");
        office = new Room("in the computing admin office");
        attic = new Room("in the attic");
        backstage = new Room("at the theatre backstage");
        toilets = new Room("at the theatre toilets");
        storage = new Room("at the pub's storage room");
        sewers = new Room("in the sewers");
        showroom = new Room("at the showroom");
        lake = new Room("at the lake");
        

        // initialise room exits
        outside.setExit("east", theatre);
        outside.setExit("south", lab);
        outside.setExit("west", pub);

        theatre.setExit("west", outside);
        theatre.setExit("up", attic);
        theatre.setExit("north", backstage);
        
        backstage.setExit("south", theatre);
        backstage.setExit("north", toilets);
        backstage.setExit("east", showroom);
        backstage.makeDark();
        
        showroom.setExit("west", backstage);

        toilets.setExit("south", backstage);
        
        pub.setExit("east", outside);
        pub.setExit("west", storage);
        
        storage.setExit("east", pub);
        storage.setExit("north", sewers);
        
        sewers.setExit("south", storage);
        sewers.setExit("north", lake);

        lab.setExit("north", outside);
        lab.setExit("east", office);

        office.setExit("west", lab);
        
        attic.setExit("down", theatre);

        player.setRoom(outside);  // start game outside
        
        storage.lockRoom();
        sewers.hasGuard();
        
        //Item gun, ammo, shinykey, batteries, plant;
        
        Gun gun = new Gun();
        Plant plant = new Plant();
        Ammo ammo = new Ammo();
        Shinykey shinykey = new Shinykey();
        Batteries batteries = new Batteries();
        
        gun.setWeigth(50);
        plant.setWeigth(50);
        ammo.setWeigth(30);
        shinykey.setWeigth(10);
        batteries.setWeigth(20);
        
        
       // plant = new Item("It's poisonous.", 50);
        //ammo = new Item("ammo, you'll need this to shoot with a gun.", 30);
       // shinykey = new Item("a shiny key, perhaps it unlocks one of the doors?", 10);
        //batteries = new Item("a pair of batteries. Lets hope they still contain power.", 20);
        
        //initialise the locations of items
        attic.getInventory().setItem("gun", gun);
        showroom.getInventory().setItem("ammo", ammo);
        office.getInventory().setItem("batteries", batteries);
        toilets.getInventory().setItem("shinykey", shinykey);
        storage.getInventory().setItem("plant", plant);
        
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to Escape the Town. The whole town has been closed and there is no known way to get out!");
        System.out.println("You will have to find a way to get out of this town");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(player.getCurrentRoom().getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * If this command ends the game, true is returned, otherwise false is
     * returned.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help"))
            printHelp();
        else if (commandWord.equals("go"))
            goRoom(command);
        else if (commandWord.equals("look"))
        	printLook();
        else if (commandWord.equals("hp"))
        	player.printHp();
        else if (commandWord.equals("quit"))
            wantToQuit = quit(command);
        else if (commandWord.equals("take"))
            take(command);
        else if (commandWord.equals("drop"))
            drop(command);
        else if (commandWord.equals("use")) 
            use(command);
        else if (commandWord.equals("examine"))
            examine(command);
        return wantToQuit;
    }
    
    private void use(Command command) {
    	if(player.isAlive() == true) {
    		if(!command.hasSecondWord()) {
    			// if there is no second word, we don't know what to charge...
    			System.out.println("Use what?");
    			return;
    		} 
    		String itemName = command.getSecondWord();
    		Item item = player.getInventory().getItem(itemName);
    		if(!itemExists(item)) {
    			return;
    		}
    		if(player.getCurrentRoom().getShortDescription().equals("in the sewers") && itemName.equals("gun") && player.getInventory().getItem("ammo") == null) {
    				System.out.println("The gun doesn't work! You will need some ammo.");
    			return;
    		}
    		if(player.getCurrentRoom().getShortDescription().equals("in the sewers") && itemName.equals("gun") && player.getInventory().getItem("ammo") != null) {
    			if (count == 1) {
    				System.out.println("You have shot the guard, but hes still moving!");
    				count++;
    			} else {
    				player.getCurrentRoom().killGuard();
    				System.out.println("You have shot the guard again! The guard died.");
    			}
    			return;
    		}
    		if(player.getCurrentRoom().getShortDescription().equals("in the campus pub") && itemName.equals("shinykey")) {
    			Room roomToUnlock = player.getCurrentRoom().getExit("west");
    			roomToUnlock.unlockRoom();
    			System.out.println("You have unlocked the storage door.");
    			return;
    		}
    		if(player.getCurrentRoom().getShortDescription().equals("at the theatre backstage") && itemName.equals("batteries")) {
    			player.getCurrentRoom().placeBatteries();
    			System.out.println("You've placed the batteries. The lights are now working!");
    			return;
    		}
    		System.out.println("You can't use that here!");
    	} else {
    		System.out.println("You can't do that anymore. You are dead.");
    	}
    }
    
    private void examine(Command command) {
    	if(player.isAlive() == true) {
    		if(!command.hasSecondWord()) {
    			// if there is no second word, we don't know what to examine...
    			System.out.println("Examine what?");
    			return;
    		}
        
    		String itemName = command.getSecondWord();
    		Item item = player.getCurrentRoom().getInventory().getItem(itemName);
        
    		if(!itemExists(item)) {
    			return;
    		}
        
    		System.out.println("It's " + player.getCurrentRoom().getInventory().getItemDescription(itemName));
    	} else {
    		System.out.println("You can't do that anymore. You are dead.");
    	}
    }

    private void printLook() {
    	if(player.isAlive() == true) {
    		if(player.getCurrentRoom().isDark() == true) {
    			System.out.println("Its too dark in here. You can see the lamp but the batteries are out of power.");
    		} else if(player.getCurrentRoom().isGuard() == true) {
    			System.out.println(player.getCurrentRoom().getLongDescription() + " You can see a guard standing in front of the next exit.");
    		} else {
    			System.out.println(player.getCurrentRoom().getLongDescription());
    		}
    	} else {
    		System.out.println("You can't do that anymore. You're dead.");
    	}
    }
    
    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /** 
     * Try to go to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
    	if(player.isAlive() == true) {
    		if(!command.hasSecondWord()) {
    			// if there is no second word, we don't know where to go...
    			System.out.println("Go where?");
    			return;
    		}

    		String direction = command.getSecondWord();

    		// Try to leave current room.
    		Room nextRoom = player.getCurrentRoom().getExit(direction);

    		if (nextRoom == null)
    			System.out.println("There is no door!");
    		else {
    			if(nextRoom.isLocked()) {
    				if(player.getCurrentRoom().getShortDescription().equals("in the campus pub")) {
    					System.out.println("The door is locked! Perhaps you can find a key...?");
    				}
    			} else if (player.getCurrentRoom().isDark()) {
    				if(direction.equals("north") || direction.equals("east")) {
    					System.out.println("Its too dark to go any further. You can only go back.");
    				} else {
    					player.setRoom(nextRoom);
    					System.out.println(player.getCurrentRoom().getLongDescription());
    				}
    			} else if (player.getCurrentRoom().isGuard()) {
    				if(direction.equals("north") && player.isWarned() == false) {
    					System.out.println("A guard stops you. 'YOU WON'T GO ANY FURTHER, DO NOT TRY AGAIN.' he screams.");
    					player.getWarned();
    				} else if(direction.equals("north") && player.isWarned() == true && player.isHurt() == true) {
    					System.out.println("The guard badly hurts you.");
    					player.damage(30);
    				} else if(direction.equals("north") && player.isWarned() == true) {
    					System.out.println("The guard badly hurts you.");
    					player.getHurt();
    					player.damage(30);
    				} else {
    					player.setRoom(nextRoom);
    					System.out.println(player.getCurrentRoom().getLongDescription());
    				}
    			}
    			else {
    				if(player.getCurrentRoom().getShortDescription().equals("in the sewers") && direction.equalsIgnoreCase("north")) {
    					System.out.println("You have reached the lake, You have escaped the town!");
    					System.out.println("You have finished this game, thanks for playing!");
    					player.setRoom(nextRoom);
    				} else {
    					player.setRoom(nextRoom);
    					System.out.println(player.getCurrentRoom().getLongDescription());
    				}
    			}
    			if(player.isHurt() == true) {
    				player.damage(10);
    				System.out.println("You have lost 10 hp because of the severe wounds.");
    			}
    		}
    	} else {
    		System.out.println("You can't do that anymore. You are dead.");
    	}
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game. Return true, if this command
     * quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else
            return true;  // signal that we want to quit
    }
    
    private void take(Command command) {
    	if(player.isAlive() == true) {
    		if(!command.hasSecondWord()) {
    			// if there is no second word, we don't know what to take...
    			System.out.println("Take what?");
    			return;
    		}
        
    		String itemName = command.getSecondWord();
    		Item item = player.getCurrentRoom().getInventory().removeItem(itemName);
        
    		if(!itemExists(item)) {
    			return;
    		}
    		
    		if(itemName.equals("plant")) {
    			System.out.println("You tried to take the plant but it was poisonous! You have lost 40 hp.");
    			player.getCurrentRoom().getInventory().removeItem(itemName);
    			player.damage(40);
    		} else {
    			player.getInventory().setItem(itemName, item);
    			System.out.println("You took the " + itemName);
    		}
    	} else {
    		System.out.println("You can't do that anymore. You are dead.");
    	}
    }
    
    private void drop(Command command) {
    	if(player.isAlive() == true) {
    		if(!command.hasSecondWord()) {
    			// if there is no second word, we don't know what to drop...
    			System.out.println("Drop what?");
    			return;
    		}

    		String itemName = command.getSecondWord();
    		Item item = player.getInventory().removeItem(itemName);
        
    		if(!itemExists(item)) {
    			return;
    		}
        
    		player.getCurrentRoom().getInventory().setItem(itemName, item);
    		System.out.println("You dropped the " + itemName);
    	} else {
    		System.out.println("You can't do that anymore. You are dead.");
    	}
        
    }
    
    private boolean itemExists(Item item) {
        if(item == null) {
            System.out.println("There is no such item.");
            return false;
        }
        return true;
    }
    
    
    public static void main(String[] args)
    {	
        Game game = new Game();
        game.play();
    }
}
