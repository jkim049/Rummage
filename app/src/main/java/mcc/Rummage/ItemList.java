package mcc.Rummage;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rrubio04 on 5/13/2017.
 */

public class ItemList extends ArrayAdapter<Item> {


    private final Activity context;
    private ArrayList<Item> itemArray;

    public ItemList(Activity context,
                       ArrayList<Item> itemArray) {

        super(context, R.layout.my_items, itemArray);
        this.context = context;
        this.itemArray = itemArray;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.item, null, true);


        TextView itemTypeTextView = (TextView) rowView.findViewById(R.id.itemTypeTextField);
        TextView itemNameTextView = (TextView) rowView.findViewById(R.id.itemNameTextField);
        TextView itemPriceTextView = (TextView) rowView.findViewById(R.id.itemPriceTextField);
        TextView itemDescriptionTextView = (TextView) rowView.findViewById(R.id.itemDescriptionTextField);
        TextView itemOwnerTextView = (TextView) rowView.findViewById(R.id.itemOwnerTextField);

        itemTypeTextView.setText(itemArray.get(position).getItemType());
        itemNameTextView.setText(itemArray.get(position).getItemName());
        itemPriceTextView.setText(itemArray.get(position).getItemPrice());
        itemDescriptionTextView.setText(itemArray.get(position).getItemDescription());
        itemOwnerTextView.setText(itemArray.get(position).getItemOwner());

        return rowView;
    }
}

