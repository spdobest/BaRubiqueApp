package com.bestdealfinance.bdfpartner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;

import java.util.List;


/**
 * Created by disha.vora on 21-11-2015.
 */
public class PayoutLoansAdapter extends RecyclerView.Adapter<PayoutLoansAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;

    private List<String>type1;    //String Resource for header view email
    private List<String>pay1;
    private List<String>pay2;//String Resource for header view email
    Context ctx;

    public static class ViewHolder extends RecyclerView.ViewHolder {//} implements View.OnClickListener {
        int Holderid;

        TextView slab1,slab2, type;


        public ViewHolder(View itemView, int ViewType) {
            super(itemView);
//            itemView.setClickable(true);
//            itemView.setOnClickListener(this);
            if (ViewType == TYPE_ITEM) {
                slab1 = (TextView) itemView.findViewById(R.id.payout1); // Creating TextView object with the id of textView from item_row.xml
                slab2 = (TextView) itemView.findViewById(R.id.payout2); // Creating TextView object with the id of textView from item_row.xml
                type = (TextView) itemView.findViewById(R.id.type);// Creating ImageView object with the id of ImageView from item_row.xml
                Holderid = 1;                                               // setting holder id as 1 as the object being populated are of type item row
            } else {
//                Name = (TextView) itemView.findViewById(R.id.name);         // Creating Text View object from header.xml for name
//                profile = (ImageView) itemView.findViewById(R.id.circleView);// Creating Image view object from header.xml for profile pic
                Holderid = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
            }
        }

    }

    public PayoutLoansAdapter(Context ctx, List<String> pay,List<String>pay_app,List<String> type) { // MyAdapter Constructor with titles and icons parameter
        // titles, icons, name, email, profile pic are passed from the main activity as we
        this.ctx = ctx;
        this.type1=type;
        this.pay1=pay;
        this.pay2=pay_app;
                             //here we assign those passed values to the values we declared here

    }


    @Override
    public PayoutLoansAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.payout_loans_item_row, parent, false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.payout_loans_header, parent, false); //Inflating the layout

            ViewHolder vhHeader = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhHeader; //returning the object created


        }
        return null;

    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(PayoutLoansAdapter.ViewHolder holder, int position) {
        if (holder.Holderid == 1) {                              // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image

            holder.type.setText(type1.get(position));
//            if (type1.get(position).equals("Credit Card")){
//                holder.slab1.setText("Rs. "+pay1.get(position)); // Setting the Text with the array of our Titles
//                holder.slab2.setText("Rs. "+pay2.get(position)); // Setting the Text with the array of our Titles
//            }
//            else {
                holder.slab1.setText(pay1.get(position)+"%"); // Setting the Text with the array of our Titles
                holder.slab2.setText(pay2.get(position)+"%"); // Setting the Text with the array of our Titles
//            }

            // Setting the Text with the array of our Titles

        } else {

//            holder.profile.setImageResource(profile);           // Similarly we set the resources for header view
//            holder.Name.setText(name);
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return pay1.size(); // the number of items in the list will be +1 the titles including the header view.
    }


    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}