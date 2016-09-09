package it.polito.mad.groupFive.restaurantcode.RestaurantView;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Restaurant_info_user.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Restaurant_info_user#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Restaurant_info_user extends Fragment implements OnMapReadyCallback {

    public interface restaurantData {
        public Restaurant getRestaurant();
    }

    restaurantData data;
    Restaurant restaurant;
    ArrayList<String> favourites;
    String uid;
    Boolean auth = false;
    MapView mapView;
    FileDownloadTask imageDownloadTask;
    ImageDownloadListener idl;
    ImageView favicon;
    TextView favtext;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Restaurant_info_user() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Restaurant_info_user.
     */
    // TODO: Rename and change types and number of parameters
    public static Restaurant_info_user newInstance(String param1, String param2) {
        Restaurant_info_user fragment = new Restaurant_info_user();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_restaurant_info_user, container, false);
        getRestaurantData(v);
        //init map
        MapsInitializer.initialize(this.getActivity());
        mapView = (CustomMapView) v.findViewById(R.id.gmap);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        //init favourites
        favourites = new ArrayList<>();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("favourite");
        FirebaseUser usr = FirebaseAuth.getInstance().getCurrentUser();
        if (usr != null) {
            uid = usr.getUid();
            auth = true;
            ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> favs = dataSnapshot.getChildren();
                    for (DataSnapshot d : favs) {
                        favourites.add(d.getKey());
                    }
                    if (favourites.contains(restaurant.getRid())) {
                        favicon.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_filled_black));
                        favtext.setText(R.string.unfav);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return v;
    }

    public void getRestaurantData(View v) {
        restaurant = data.getRestaurant();
        TextView restopen = (TextView) v.findViewById(R.id.restaurant_open);
        ImageView open = (ImageView) v.findViewById(R.id.imageView9);
        if (restaurant.isOpen()) {
            restopen.setText(R.string.nowOpen);
            restopen.setTextColor(Color.rgb(0, 100, 0));
        } else {
            restopen.setText(R.string.nowClosed);
            restopen.setTextColor(Color.rgb(200, 0, 0));
        }
        TextView restdescr = (TextView) v.findViewById(R.id.restaurant_description);
        restdescr.setText(restaurant.getDescription());
        TextView restaddr = (TextView) v.findViewById(R.id.restaurant_address);
        restaddr.setText(restaurant.getAddress() + ", " + restaurant.getCity() + " " + restaurant.getZip() + " " + restaurant.getState());
        RelativeLayout resttel = (RelativeLayout) v.findViewById(R.id.layout_call);
        resttel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + restaurant.getTelephone()));
                startActivity(dialIntent);
            }
        });
        RelativeLayout restweb = (RelativeLayout) v.findViewById(R.id.layout_web);
        restweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + restaurant.getWebsite()));
                startActivity(webIntent);
            }
        });
        favicon = (ImageView) v.findViewById(R.id.favIcon);
        favtext = (TextView) v.findViewById(R.id.favtext);
        RelativeLayout restfav = (RelativeLayout) v.findViewById(R.id.layout_fav);
        restfav.setOnClickListener(new OnFavourite(restaurant));

        final LinearLayout ll = (LinearLayout) v.findViewById(R.id.restaurant_time_t);
        int count = 0;
        for (String weekday : getResources().getStringArray(R.array.weekENG)) {
            LayoutInflater li = LayoutInflater.from(v.getContext());
            View timetableItem = li.inflate(R.layout.timetable_view, null);
            TextView dow = (TextView) timetableItem.findViewById(R.id.dow);
            dow.setText(getResources().getStringArray(R.array.week)[count]);
            String time = "";
            TextView lunch = (TextView) timetableItem.findViewById(R.id.time_lunch);
            if (restaurant.getTimetableLunch() != null && restaurant.getTimetableLunch().containsKey(weekday)) {
                time = time.concat(restaurant.getTimetableLunch().get(weekday).get("start") + "-" + restaurant.getTimetableLunch().get(weekday).get("end"));
            }
            if (restaurant.getTimetableDinner() != null && restaurant.getTimetableDinner().containsKey(weekday)) {
                if (!time.equals("")) time = time.concat("\n");
                time = time.concat((restaurant.getTimetableDinner().get(weekday).get("start") + "-" + (restaurant.getTimetableDinner().get(weekday).get("end"))));
            }
            if (time.equals("")) time = getString(R.string.closed);
            lunch.setText(time);
            ll.addView(timetableItem);
            count++;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            data = (restaurantData) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getFromNetwork(StorageReference storageRoot, final String id, final ImageView imView) throws FileNotFoundException {
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        final File dir = cw.getDir("images", Context.MODE_PRIVATE);
        File filePath = new File(dir, id);
        this.imageDownloadTask = storageRoot.child(id).getFile(filePath);
        this.idl = new ImageDownloadListener(imView, dir, id);
        this.imageDownloadTask.addOnSuccessListener(this.idl);
    }

    @Override
    public void onPause() {
        super.onPause();
        //TODO this.imageDownloadTask.removeOnSuccessListener(this.idl);
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng latLng = new LatLng(restaurant.getXCoord(), restaurant.getYCoord());
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(restaurant.getName()));
        map.moveCamera(CameraUpdateFactory.zoomTo(15));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private class ImageDownloadListener implements OnSuccessListener<FileDownloadTask.TaskSnapshot> {
        private ImageView imView;
        private File directory;
        private String id;

        public ImageDownloadListener(ImageView imageView, File imageDirectory, String imageID) {
            this.imView = imageView;
            this.directory = imageDirectory;
            this.id = imageID;
        }

        @Override
        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
            File img = new File(this.directory, this.id);
            Uri imgPath = Uri.fromFile(img);
            try {
                Bitmap b = new Picture(imgPath, getActivity().getContentResolver()).getBitmap();
                this.imView.setImageBitmap(b);
            } catch (IOException e) {
                //Log.e("getFromNet",e.getMessage());
            }
        }
    }

    public class OnFavourite implements View.OnClickListener {
        private Restaurant restaurant;

        public OnFavourite(Restaurant restaurant) {
            this.restaurant = restaurant;
        }

        @Override
        public void onClick(View v) {
            if (auth == true) {
                if (favourites.contains(restaurant.getRid())) {
                    //is favourite
                    favourites.remove(restaurant.getRid());
                    favicon.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_border_black));
                    favtext.setText(R.string.fav);
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference ref = db.getReference("favourite");
                    ref.child(uid).child(restaurant.getRid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
                    //remove on server
                } else {
                    favourites.add(restaurant.getRid());
                    //add on server
                    favicon.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_filled_black));
                    favtext.setText(R.string.unfav);
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    final DatabaseReference menus = db.getReference("menu");
                    menus.orderByChild("rid").equalTo(restaurant.getRid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference ref = db.getReference("favourite");
                            Iterable<DataSnapshot> favs = dataSnapshot.getChildren();
                            for (DataSnapshot d : favs) {

                                ref.child(uid).child(restaurant.getRid()).child(d.getKey()).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                });


                            }
                            Toast.makeText(getContext(), restaurant.getName() + getResources().getString(R.string.added_favourite), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                }

            }
            else{
                Toast.makeText(getContext(), getResources().getString(R.string.need_to_login), Toast.LENGTH_LONG).show();
            }
        }
    }
}