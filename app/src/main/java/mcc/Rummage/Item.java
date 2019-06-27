package mcc.Rummage;

/**
 * Created by rrubio04 on 5/13/2017.
 */

public class Item implements Cloneable{


    private String itemName;
    private String itemPrice;
    private String itemDescription;
    private String itemOwner;
    private String itemType;

    //private location;
    //private Image image;

    public Item() {}

    public Item(String itemType, String itemName, String itemPrice, String itemDescription, String itemOwner) {
        this.itemType = itemType;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemDescription = itemDescription;
        this.itemOwner = itemOwner;

    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemOwner() {
        return itemOwner;
    }

    public void setItemOwner(String itemOwner) {
        this.itemOwner = itemOwner;
    }


    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }


    @Override
    protected Item clone(){
        Item clone = null;
        try{
            clone = (Item) super.clone();
        }catch(CloneNotSupportedException cnse){
            throw new RuntimeException(cnse);
        }

        return clone;
    }
}