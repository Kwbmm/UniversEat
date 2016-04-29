package it.polito.mad.groupFive.restaurantcode.holders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import it.polito.mad.groupFive.restaurantcode.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder{
    public TextView restaurant_name;
    public TextView restaurant_address;
    public RatingBar restaurant_rating;
    public CardView card;

    public RestaurantViewHolder(View itemView) {
        super(itemView);
        //TODO set image
        this.restaurant_name =(TextView)itemView.findViewById(R.id.restaurant_name);
        this.restaurant_address = (TextView) itemView.findViewById(R.id.restaurant_address);
        this.restaurant_rating = (RatingBar) itemView.findViewById(R.id.restaurant_rating);
        this.card= (CardView) itemView.findViewById(R.id.restaurant_card);
    }
}
