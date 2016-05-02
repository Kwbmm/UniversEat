package it.polito.mad.groupFive.restaurantcode.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import it.polito.mad.groupFive.restaurantcode.R;

/**
 * Created by MacBookRetina on 02/05/16.
 */
public class TagsHolder extends RecyclerView.ViewHolder {
    public CheckBox checkBox;
    public TagsHolder(View itemView) {
        super(itemView);
        checkBox= (CheckBox) itemView.findViewById(R.id.tag_check);
    }
}
