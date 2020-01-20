package obj;

import java.util.Currency;

public class Asset {
    /*
     * Generic class for an asset - an object etc.
     */

    int id;
    String name;
    String type;
    int quantity;
    Currency value;
    int[][] accessories; //IDs of Accessory items (such as Stand or Cable etc, with quantity);
    boolean authorisationNeeded;

    public Asset(String name, String type, int quantity, Currency value, int[][]accessories, boolean authorisationNeeded){
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.value = value;
        this.accessories = accessories;
        this.authorisationNeeded = authorisationNeeded;
    }
}
