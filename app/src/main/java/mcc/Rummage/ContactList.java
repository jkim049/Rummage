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
 * Created by Larry on 4/29/17.
 */


public class ContactList extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> textArray;
    private static  Integer[] images = {R.drawable.h1,R.drawable.h2};
    //private final String[] web;
    public ContactList(Activity context,
                      ArrayList<String> textArray) {
        super(context, R.layout.contact_item, textArray);
        this.context = context;
        this.textArray = textArray;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.contact_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        txtTitle.setText(textArray.get(position));
        imageView.setImageResource(images[position%2]);
        return rowView;
    }
}