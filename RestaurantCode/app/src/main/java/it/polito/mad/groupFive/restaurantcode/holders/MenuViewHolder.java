package it.polito.mad.groupFive.restaurantcode.holders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import it.polito.mad.groupFive.restaurantcode.R;

public class MenuViewHolder extends RecyclerView.ViewHolder {
    public TextView menu_name;
    public TextView menu_description;
    public TextView menu_price;
    public ImageView menu_image;
    public ImageView vegetarian_icon;
    public ImageView vegan_icon;
    public ImageView spicy_icon;
    public ImageView glutenfree_icon;
    public CardView card;

    public MenuViewHolder(View itemView) {
        super(itemView);
        this.menu_name =(TextView)itemView.findViewById(R.id.menu_name);
        this.menu_description=(TextView)itemView.findViewById(R.id.menu_description);
        this.menu_price=(TextView)itemView.findViewById(R.id.menu_price);
        this.menu_image = (ImageView) itemView.findViewById(R.id.menu_image);
        this.card= (CardView) itemView.findViewById(R.id.menu_card);
        this.spicy_icon=(ImageView) itemView.findViewById(R.id.menu_icon1);
        this.glutenfree_icon=(ImageView) itemView.findViewById(R.id.menu_icon2);
        this.vegetarian_icon=(ImageView) itemView.findViewById(R.id.menu_icon3);
        this.vegan_icon=(ImageView) itemView.findViewById(R.id.menu_icon4);
    }
}