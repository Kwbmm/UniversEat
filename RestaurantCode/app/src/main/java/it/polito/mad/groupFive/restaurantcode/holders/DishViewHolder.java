package it.polito.mad.groupFive.restaurantcode.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import it.polito.mad.groupFive.restaurantcode.R;

/**
 * Created by MacBookRetina on 30/04/16.
 */
public class DishViewHolder extends RecyclerView.ViewHolder {
    public TextView dish_name;
    public ImageView vegan;
    public ImageView vegetarian;
    public ImageView hot;
    public ImageView glunten_free;
    public ImageButton delete;

    public DishViewHolder(View itemView) {
        super(itemView);
        dish_name= (TextView) itemView.findViewById(R.id.dishName);
        vegan=(ImageView) itemView.findViewById(R.id.vegan);
        vegetarian=(ImageView) itemView.findViewById(R.id.vegetarian);
        hot=(ImageView)itemView.findViewById(R.id.hot);
        glunten_free=(ImageView) itemView.findViewById(R.id.gluten_free);
        delete=(ImageButton)itemView.findViewById(R.id.delete_dish);
    }
}
