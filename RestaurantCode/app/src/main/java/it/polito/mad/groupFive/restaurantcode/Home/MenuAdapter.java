package it.polito.mad.groupFive.restaurantcode.Home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import it.polito.mad.groupFive.restaurantcode.New_Menu_details;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.holders.MenuViewHolder;

public class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder>{
    private class DistanceMenu extends Menu {

        private float distance;
        public DistanceMenu(Menu m,float distance) throws MenuException {
            super(m.getRid(), m.getMid());
            super
                    .setBeverage(m.isBeverage())
                    .setDescription(m.getDescription())
                    .setImageLocal(m.getImageLocalPath())
                    .setName(m.getName())
                    .setServiceFee(m.isServiceFee())
                    .setPrice(m.getPrice())
                    .setType(m.getType());
            super.setGlutenfree(m.isGlutenfree());
            super.setVegan(m.isVegan());
            super.setVegetarian(m.isVegetarian());
            super.setSpicy(m.isSpicy());
            this.distance = distance;
        }

        public float getDistance(){ return this.distance; }
    }

    private SortedList<DistanceMenu> menus;
    private RecyclerView rv;
    private ProgressBar pb;
    private Context context;

    public MenuAdapter(RecyclerView rv,ProgressBar pb,Context context){
        this.rv = rv;
        this.pb = pb;
        this.context = context;
        this.menus = new SortedList<DistanceMenu>(DistanceMenu.class,
                new SortedList.Callback<DistanceMenu>() {
                    @Override
                    public int compare(DistanceMenu o1, DistanceMenu o2) {
                        if(o1.getDistance() == Float.MIN_VALUE || o2.getDistance() == Float.MIN_VALUE){
                            //If the distance is not set, we order by insertion time in the db.
                            return o2.getMid().compareTo(o1.getMid());
                        }
                        return (o2.getDistance() - o1.getDistance() >= 0) ? -1 : 1;
                    }

                    @Override
                    public void onInserted(int position, int count) {
                        notifyItemRangeInserted(position,count);
                    }

                    @Override
                    public void onRemoved(int position, int count) {
                        notifyItemRangeRemoved(position,count);
                    }

                    @Override
                    public void onMoved(int fromPosition, int toPosition) {
                        notifyItemMoved(fromPosition,toPosition);
                    }

                    @Override
                    public void onChanged(int position, int count) {
                        notifyItemRangeChanged(position,count);
                    }

                    @Override
                    public boolean areContentsTheSame(DistanceMenu oldItem, DistanceMenu newItem) {
                        return oldItem.getMid().equals(newItem.getMid());
                    }

                    @Override
                    public boolean areItemsTheSame(DistanceMenu item1, DistanceMenu item2) {
                        return item1.getMid().equals(item2.getMid());
                    }
                });
    }

    /**
     * Add a new child to the adapter. Elements are added according to their weights: elements
     * with lower weight are displayed before those with higher weight.
     *
     * @param m Object to insert
     * @param distance Distance of the object with respect to your location
     */
    public void addChildWithDistance(Menu m,float distance){
        final String METHOD_NAME = this.getClass().getName()+" - addChildWithDistance";
        try {
            DistanceMenu wm = new DistanceMenu(m,distance);
            this.menus.add(wm);
            if(rv.getVisibility() == View.GONE){
                pb.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
            }
        } catch (MenuException e) {
            Log.e(METHOD_NAME,e.getMessage());
        }
    }

    public void addChildNoDistance(Menu m){
        final String METHOD_NAME = this.getClass().getName()+" - addChildWithDistance";
        try {
            DistanceMenu wm = new DistanceMenu(m,Float.MIN_VALUE);
            this.menus.add(wm);
            if(rv.getVisibility() == View.GONE){
                pb.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
            }
        } catch (MenuException e) {
            Log.e(METHOD_NAME,e.getMessage());
        }
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View menu_view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_view,null);
        return new MenuViewHolder(menu_view);
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, int position) {
        final String METHOD_NAME = this.getClass().getName()+" - onBindViewHolder";
        final DistanceMenu menu = menus.get(position);
        String s=menu.getDescription();
        if(s.length() >80)
            s = s.substring(0,77) + "...";
        holder.menu_description.setText(s);
        holder.menu_name.setText(menu.getName());
        if(menu.getName().length()>18)
            holder.menu_name.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
        holder.menu_price.setText(String.format(Locale.getDefault(),"%.2f", menu.getPrice())+"€");
        if(!menu.isSpicy()) holder.spicy_icon.setColorFilter(Color.GRAY);
        if(!menu.isVegan()) holder.vegan_icon.setColorFilter(Color.GRAY);
        if(!menu.isVegetarian()) holder.vegetarian_icon.setColorFilter(Color.GRAY);
        if(!menu.isGlutenfree()) holder.glutenfree_icon.setColorFilter(Color.GRAY);
        File img = new File(menu.getImageLocalPath());
        try {
            holder.menu_image.setImageBitmap(new Picture(Uri.fromFile(img),context.getContentResolver(),300,300).getBitmap());
        } catch (IOException e) {
            Log.e(METHOD_NAME,e.getMessage());
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("rid",menu.getRid());
                bundle.putString("mid",menu.getMid());
                New_Menu_details menu_details = new New_Menu_details();
                menu_details.setArguments(bundle);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_up,R.anim.slide_down,R.anim.slide_up,R.anim.slide_down)
                        .addToBackStack(null).add(R.id.frame,menu_details).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.menus.size();
    }

    public SortedList<DistanceMenu> getMenus(){ return this.menus; }
}