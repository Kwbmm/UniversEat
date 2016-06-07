package it.polito.mad.groupFive.restaurantcode.SearchRestaurants;

import android.content.Context;
import android.net.Uri;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.holders.RestaurantViewHolder;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder>{
    private class DistanceRestaurant extends Restaurant {

        private float distance;
        public DistanceRestaurant(Restaurant r, float distance) throws RestaurantException {
            super(r.getUid(),r.getRid());
            super
                    .setName(r.getName())
                    .setDescription(r.getDescription())
                    .setImageLocalPath(r.getImageLocalPath())
                    .setAddress(r.getAddress())
                    .setCity(r.getCity())
                    .setRating(r.getRating())
                    .setState(r.getState())
                    .setXCoord(r.getXCoord())
                    .setYCoord(r.getYCoord())
                    .setZip(r.getZip())
                    .setTelephone(r.getTelephone())
                    .setWebsite(r.getWebsite())
                    .setTimetableDinner(r.getTimetableDinner())
                    .setTimetableLunch(r.getTimetableLunch())
                    .setTickets(r.getTickets());
            this.distance = distance;
        }

        public float getDistance(){ return this.distance; }
    }

    private SortedList<DistanceRestaurant> restaurants;
    private RecyclerView rv;
    private ProgressBar pb;
    private Context context;

    public RestaurantAdapter(RecyclerView rv,ProgressBar pb,Context context){
        this.rv = rv;
        this.pb = pb;
        this.context = context;
        this.restaurants = new SortedList<DistanceRestaurant>(DistanceRestaurant.class,
                new SortedList.Callback<DistanceRestaurant>() {
                    @Override
                    public int compare(DistanceRestaurant o1, DistanceRestaurant o2) {
                        if(o1.getDistance() == Float.MIN_VALUE || o2.getDistance() == Float.MIN_VALUE){
                            //If the distance is not set, we order by insertion time in the db.
                            return o2.getRid().compareTo(o1.getRid());
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
                    public boolean areContentsTheSame(DistanceRestaurant oldItem, DistanceRestaurant newItem) {
                        return oldItem.getRid().equals(newItem.getRid());
                    }

                    @Override
                    public boolean areItemsTheSame(DistanceRestaurant item1, DistanceRestaurant item2) {
                        return item1.getRid().equals(item2.getRid());
                    }
                });
    }

    /**
     * Add a new child to the adapter. Elements are added according to their weights: elements
     * with lower weight are displayed before those with higher weight.
     *
     * @param r Object to insert
     * @param distance Distance of the object with respect to your location
     */
    public void addChildWithDistance(Restaurant r,float distance){
        final String METHOD_NAME = this.getClass().getName()+" - addChildWithDistance";
        try {
            DistanceRestaurant dr = new DistanceRestaurant(r,distance);
            this.restaurants.add(dr);
            if(rv.getVisibility() == View.GONE){
                pb.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
            }
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME,e.getMessage());
        }
    }

    public void addChildNoDistance(Restaurant r){
        final String METHOD_NAME = this.getClass().getName()+" - addChildWithDistance";
        try {
            DistanceRestaurant dr = new DistanceRestaurant(r,Float.MIN_VALUE);
            this.restaurants.add(dr);
            if(rv.getVisibility() == View.GONE){
                pb.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
            }
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME,e.getMessage());
        }
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View menu_view= LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_view,null);
        return new RestaurantViewHolder(menu_view);
    }

    @Override
    public void onBindViewHolder(final RestaurantViewHolder holder, int position) {
        final String METHOD_NAME = this.getClass().getName()+" - onBindViewHolder";
        final Restaurant restaurant = restaurants.get(position);
        holder.restaurant_address.setText(restaurant.getAddress());
        holder.restaurant_name.setText(restaurant.getName());
        holder.restaurant_rating.setRating(restaurant.getRating());
        holder.distance.setText(String.valueOf((restaurants.get(position).getDistance()/1000))+" km");

        File img = new File(restaurant.getImageLocalPath());
        try {
            holder.restaurant_image.setImageBitmap(new Picture(Uri.fromFile(img),context.getContentResolver(),300,300).getBitmap());
        } catch (IOException e) {
            Log.e(METHOD_NAME,e.getMessage());
        }
            /*
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent menu_view =new Intent(context,User_info_view.class);
                    menu_view.putExtra("mid",resta.getRid());
                    menu_view.putExtra("rid",menu.getRid());
                    context.startActivity(menu_view);
                }
            });
            */
    }

    @Override
    public int getItemCount() {
        return this.restaurants.size();
    }

    public SortedList<DistanceRestaurant> getRestaurants(){ return this.restaurants; }
}