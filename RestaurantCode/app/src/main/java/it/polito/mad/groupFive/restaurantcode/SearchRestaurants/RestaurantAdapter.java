package it.polito.mad.groupFive.restaurantcode.SearchRestaurants;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.RestaurantView.User_info_view;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.holders.RestaurantViewHolder;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder>{
    private static final float DISTANCE_NEAR = 3000; //Maximum distance to consider the restaurant near
    private ArrayList<String> favourites;
    public class DistanceRestaurant extends Restaurant {

        private float distance;
        private boolean toKeep;
        public DistanceRestaurant(Restaurant r, float distance) throws RestaurantException {
            super(r.getUid(),r.getRid());
            super
                    .setName(r.getName())
                    .setDescription(r.getDescription())
                    .setImageLocalPath(r.getImageLocalPath())
                    .setAddress(r.getAddress())
                    .setCity(r.getCity())
                    .setRating(r.getRating())
                    .setRatingNumber(1)
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
            this.toKeep = false;
        }

        public float getDistance(){ return this.distance; }
    }

    private SortedList<DistanceRestaurant> restaurants;
    private RecyclerView rv;
    private ProgressBar pb;
    private Context context;
    private Boolean auth=false;
    private String uid;

    public RestaurantAdapter(RecyclerView rv,ProgressBar pb,Context context){
        this.rv = rv;
        this.pb = pb;
        this.context = context;
        favourites=new ArrayList<>();
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference ref=db.getReference("favourite");
        FirebaseUser usr = FirebaseAuth.getInstance().getCurrentUser();
        if (usr !=null){
            uid = usr.getUid();
            auth=true;
            ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> favs=dataSnapshot.getChildren();
                    for(DataSnapshot d:favs){
                        favourites.add(d.getKey());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
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
            //Log.e(METHOD_NAME,e.getMessage());
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
            //Log.e(METHOD_NAME,e.getMessage());
        }
    }

    public void filter(int choice,Integer distance){
        switch (choice){
            case 0: //By ticket
                this.filterByTicket();
                break;
            case 1: //By High rating (3-5)
                this.filterByHighRating();
                break;
            case 2: //Open now
                this.filterByOpenNow();
                break;
            case 3: //Less than 3 km
                this.filterByDistance(distance);
                break;
        }
    }

    private void filterByTicket() {
        for (int i = 0; i < this.restaurants.size(); i++) {
            DistanceRestaurant dr = this.restaurants.get(i);
            HashMap<String,Boolean> tickets = (HashMap<String,Boolean>)dr.getTickets();
            if(tickets != null && tickets.size() > 0){
                dr.toKeep = true;
            }
        }
    }

    private void filterByHighRating() {
        for (int i = 0; i < this.restaurants.size(); i++) {
            DistanceRestaurant dr = this.restaurants.get(i);
            if(dr.getRating() >= 3){
                dr.toKeep = true;
            }
        }
    }

    private void filterByOpenNow() {
        for (int i = 0; i < this.restaurants.size(); i++) {
            DistanceRestaurant dr = this.restaurants.get(i);
            if(dr.isOpen())
                dr.toKeep = true;
        }
    }

    private void filterByDistance(Integer distance) {
        for (int i = 0; i < this.restaurants.size(); i++) {
            DistanceRestaurant dr = this.restaurants.get(i);
            if(dr.getDistance() <= distance.floatValue())
                dr.toKeep = true;
        }
    }

    /**
     * Remove the items marked as such.
     */
    public void updateEntries(){
        this.restaurants.beginBatchedUpdates();
        for (int i = 0; i < this.restaurants.size(); i++) {
            DistanceRestaurant dr = this.restaurants.get(i);
            if(!dr.toKeep){
                this.restaurants.removeItemAt(i);
                i--;
            }
            else{
                dr.toKeep = false;
            }
        }
        this.restaurants.endBatchedUpdates();
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View menu_view= LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_view,null);
        return new RestaurantViewHolder(menu_view);
    }

    @Override
    public void onBindViewHolder(final RestaurantViewHolder holder, int position) {
        final String METHOD_NAME = this.getClass().getName()+" - onBindViewHolder";
        final DistanceRestaurant restaurant = restaurants.get(position);
        if(auth==true) {
            if (favourites.contains(restaurant.getRid())) {
                holder.favourite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart_filled_black));
                holder.favourite.setColorFilter(Color.rgb(217,37,40));
            }
            holder.favourite.setOnClickListener(new OnFavourite(holder, restaurant));
        } else {
            holder.favourite.setVisibility(View.INVISIBLE);
        }
        holder.restaurant_name.setText(restaurant.getName());
        if(restaurant.getName().length()>18)
            holder.restaurant_name.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
        holder.restaurant_rating.setRating(restaurant.getRating());
        String s;
        holder.restaurant_description.setText(restaurant.getDescription());
        float distance = restaurant.getDistance();
        if(distance < 1){ //Less than 1 meter
            s=(", < 1 m "+context.getResources().getString(R.string.from_here));
        }
        else if(distance >= 1 && distance < 1000){// Between 1 and 1000 meters
            s=(", "+String.valueOf((int)distance)+" m "+context.getString(R.string.from_here));
        }
        else{
            s=(", "+String.valueOf((int)(restaurant.getDistance()/1000))+" km "+context.getString(R.string.from_here));
        }
        holder.restaurant_address.setText(restaurant.getCity()+s);

        File img = new File(restaurant.getImageLocalPath());
        try {
            holder.restaurant_image.setImageBitmap(new Picture(Uri.fromFile(img),context.getContentResolver(),300,300).getBitmap());
        } catch (IOException e) {
            //Log.e(METHOD_NAME,e.getMessage());
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent restaurant_view =new Intent(context,User_info_view.class);
                restaurant_view.putExtra("rid",restaurant.getRid());
                context.startActivity(restaurant_view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.restaurants.size();
    }

    public SortedList<DistanceRestaurant> getRestaurants(){ return this.restaurants; }

    public class OnFavourite implements View.OnClickListener{
        private RestaurantViewHolder holder;
        private Restaurant restaurant;
        private ArrayList<String> menusList;


        public OnFavourite(RestaurantViewHolder holder, Restaurant restaurant) {
            this.holder = holder;
            this.restaurant = restaurant;
            menusList=new ArrayList<>();
        }

        @Override
        public void onClick(View v) {
            if (favourites.contains(restaurant.getRid())){
                //is favourite
                favourites.remove(restaurant.getRid());
                holder.favourite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart_border_black));
                holder.favourite.setColorFilter(Color.GRAY);
                FirebaseDatabase db=FirebaseDatabase.getInstance();
                DatabaseReference ref=db.getReference("favourite");
                ref.child(uid).child(restaurant.getRid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
                //remove on server
            }
            else{
                favourites.add(restaurant.getRid());
                //add on server
                holder.favourite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart_filled_black));
                holder.favourite.setColorFilter(Color.rgb(217,37,40));
                FirebaseDatabase db=FirebaseDatabase.getInstance();
                final DatabaseReference menus=db.getReference("menu");
                menus.orderByChild("rid").equalTo(restaurant.getRid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FirebaseDatabase db=FirebaseDatabase.getInstance();
                        DatabaseReference ref=db.getReference("favourite");
                        Iterable<DataSnapshot> favs=dataSnapshot.getChildren();
                        for(DataSnapshot d:favs){

                            ref.child(uid).child(restaurant.getRid()).child(d.getKey()).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {



                                }
                            });




                        }
                        Toast.makeText(context,restaurant.getName()+context.getResources().getString(R.string.added_favourite),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        }
    }
}