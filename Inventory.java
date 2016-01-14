import java.util.HashMap;
import java.util.Set;

public class Inventory {
	HashMap<String, Item> items;
	
	public Inventory() {
		items = new HashMap<String, Item>();
		}
	
	
	public String getItemList() {
        if(items.isEmpty()) {
            return "";
        }
        
        String returnString = "You see these items:\n";
        Set<String> itemNames = items.keySet();
        
        for(String item : itemNames) {
            returnString += " " + item;
        }
        
        return returnString;
    }
    
    public Item removeItem(String itemName) {
        return items.remove(itemName);
    }
    
    public Item getItem(String itemName) {
        return items.get(itemName);
    } 
    
    public String getItemDescription(String itemName) {
        return items.get(itemName).getDescription();
    }
    
    public void setItem(String name, Item item) {
        items.put(name, item);
    }
}

