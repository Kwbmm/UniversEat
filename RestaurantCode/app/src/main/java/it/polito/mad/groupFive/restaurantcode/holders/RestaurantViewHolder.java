package it.polito.mad.groupFive.restaurantcode.holders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import it.polito.mad.groupFive.restaurantcode.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder{
    public TextView restaurant_name;
    public TextView restaurant_address;
    public RatingBar restaurant_rating;
    public ImageView restaurant_image;
    public TextView restaurant_description;
    public CardView card;
    public ImageButton favourite;

    public RestaurantViewHolder(View itemView) {
        super(itemView);
        this.restaurant_description=(TextView) itemView.findViewById(R.id.restaurant_description);
        this.restaurant_name =(TextView)itemView.findViewById(R.id.restaurant_name);
        this.restaurant_address = (TextView) itemView.findViewById(R.id.restaurant_address);
        this.restaurant_rating = (RatingBar) itemView.findViewById(R.id.restaurant_rating);
        this.restaurant_image = (ImageView) itemView.findViewById(R.id.restaurant_image);
        this.card= (CardView) itemView.findViewById(R.id.restaurant_card);
        this.favourite=(ImageButton) itemView.findViewById(R.id.restaurant_favourite);
    }
}
