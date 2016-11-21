package it.polito.mad.groupFive.restaurantcode;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.RestaurantView.CustomMapView;
import it.polito.mad.groupFive.restaurantcode.RestaurantView.User_info_view;
import it.polito.mad.groupFive.restaurantcode.datastructures.Course;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.MenuException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;

/**
 * Created by Cristiano on 11/06/2016.
 */
public class Restaurant_map extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    SharedPreferences sharedPreferences;
    private ValueAnimator valueAnimator1;
    private ValueAnimator valueAnimator2;
    private RelativeLayout background;
    private Boolean fadeoutBackground;
    private Boolean fadeinBackground;
    private GoogleMap mMap;
    private MapView mapView;

    public Restaurant_map() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        fadeinBackground = true;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_restaurant_map, container, false);
        setAnimators();
        fadeoutBackground = true;
        background = (RelativeLayout) v.findViewById(R.id.background);
        if (fadeinBackground) {
            background.postDelayed(new Runnable() {
                @Override
                public void run() {
                    valueAnimator1.start();
                }
            }, 300);
            fadeinBackground = false;
        } else background.setBackgroundColor(getResources().getColor(R.color.fragmentWhite));
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        MapsInitializer.initialize(this.getActivity());
        mapView = (MapView) v.findViewById(R.id.gmap);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("restaurant");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LatLng latLng = new LatLng((double)dataSnapshot.child("xcoord").getValue(),(double)dataSnapshot.child("ycoord").getValue());
                Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title((String) dataSnapshot.child("name").getValue()));
                marker.setTag(dataSnapshot.child("rid").getValue());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    private void setAnimators() {
        valueAnimator1 = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(R.color.fragmentTransparent), getResources().getColor(R.color.fragmentWhite));
        valueAnimator1.setDuration(300);
        valueAnimator1.setInterpolator(new DecelerateInterpolator(2));
        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                background.setBackgroundColor((int) animation.getAnimatedValue());
            }
        });
        valueAnimator2 = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(R.color.fragmentWhite), getResources().getColor(R.color.fragmentTransparent));
        valueAnimator2.setDuration(100);
        //valueAnimator2.setInterpolator(new DecelerateInterpolator(2));
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                background.setBackgroundColor((int) animation.getAnimatedValue());
            }
        });
    }

    @Override
    public void onDestroyView() {
        if (fadeoutBackground) valueAnimator2.start();
        mapView.onDestroy();
        super.onDestroyView();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnInfoWindowClickListener(this);
        this.mMap=googleMap;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent restaurant_view = new Intent(getContext(), User_info_view.class);
        restaurant_view.putExtra("rid", (String) marker.getTag());
        startActivity(restaurant_view);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
    @Override
    public void onPause() {
        super.onPause();
        try{
            mapView.onPause();
        } catch(NullPointerException e){}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            mapView.onDestroy();
        } catch(NullPointerException e){}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try{
            mapView.onSaveInstanceState(outState);
        } catch (NullPointerException e) {}
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        try{
            mapView.onResume();
        } catch(NullPointerException e){}
    }
}
