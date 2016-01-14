public class Player {
	private int health;
	private Room currentRoom;
	private boolean alive;
	private boolean hurt;
	private boolean warned;
	private int weightLimit;
    private Inventory inventory;
	
	public Player() {
		alive = true;
		health = 100;
		
		inventory = new Inventory();
        weightLimit = 100;
	}
	
	public void damage(int amount) {
		health -= amount;
		if(health <= 0) {
			alive = false;
			System.out.println("You have died.");
		}
	}
	
	public void heal(byte amount) {
		if(health < 100 && alive) {
		health += amount;
		} else if (health >= 100 && alive) {
			System.out.println("Your health is already full.");
		} else if (!alive) {
			System.out.println("You can't heal, you are dead...");
		}
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public boolean isWarned() {
		return warned;
	}
	
	public boolean isHurt() {
		return hurt;
	}
	
	public void getHurt() {
		hurt = true;
	}
	
	public void getWarned() {
		warned = true;
	}
	
	public Inventory getInventory() {
        return inventory;
    }

	public void printHp() {
		System.out.println("You still have " + health + " hp left.");
	}
	
	public Room getCurrentRoom() {
		return currentRoom;
	}
	
	public void setRoom(Room room) {
		currentRoom = room;
	}
}
