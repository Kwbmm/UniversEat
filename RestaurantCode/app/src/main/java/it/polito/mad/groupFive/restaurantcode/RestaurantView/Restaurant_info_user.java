package it.polito.mad.groupFive.restaurantcode.RestaurantView;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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

    public interface restaurantData{
        public Restaurant getRestaurant();
    }
    restaurantData data;
    Restaurant restaurant;
    GoogleMap myMap;
    MapView mapView;
    FileDownloadTask imageDownloadTask;
    ImageDownloadListener idl;

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

        View v=inflater.inflate(R.layout.fragment_restaurant_info_user, container, false);
        getRestaurantData(v);
        //init map
        MapsInitializer.initialize(this.getActivity());
        mapView = (MapView) v.findViewById(R.id.gmap);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return v;
    }

    public void getRestaurantData(View v) {
        restaurant=data.getRestaurant();
        TextView rest_rev_det =(TextView)v.findViewById(R.id.restaurant_rev_details);
        rest_rev_det.setText(String.format(getResources().getString(R.string.review_based_on),(int)restaurant.getRatingNumber()));
        TextView restname= (TextView) v.findViewById(R.id.restaurant_name);
        restname.setText(restaurant.getName());
        TextView restopen= (TextView)v.findViewById(R.id.restaurant_open);
        ImageView open=(ImageView)v.findViewById(R.id.imageView9);
        if(restaurant.isOpen()){
            restopen.setText(R.string.nowOpen);
            restopen.setTextColor(Color.rgb(0,100,0));
            open.setColorFilter(Color.rgb(0,100,0));
        }
        else {
            restopen.setText(R.string.nowClosed);
            restopen.setTextColor(Color.rgb(200,0,0));
            open.setColorFilter(Color.rgb(200,0,0));
        }
        TextView restdescr = (TextView)v.findViewById(R.id.restaurant_description);
        restdescr.setText(restaurant.getDescription());
        TextView restaddr=(TextView) v.findViewById(R.id.restaurant_address);
        restaddr.setText(restaurant.getAddress()+", "+restaurant.getCity()+" "+restaurant.getZip());
        TextView resttel=(TextView)v.findViewById(R.id.restaurant_tel);
        resttel.setText(restaurant.getTelephone());
        resttel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+restaurant.getTelephone()));
                startActivity(dialIntent);
            }
        });
        TextView restweb=(TextView)v.findViewById(R.id.restaurant_web);
        restweb.setText(restaurant.getWebsite());
        restweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://"+restaurant.getWebsite()));
                startActivity(webIntent);
            }
        });
        RatingBar restrating= (RatingBar) v.findViewById(R.id.restaurant_rating);
        restrating.setRating(restaurant.getRating());
        ImageView restImage=(ImageView)v.findViewById(R.id.restaurant_image);

        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference imageref=storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com/restaurant/");
        try {
            getFromNetwork(imageref,restaurant.getRid(),restImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final LinearLayout ll = (LinearLayout) v.findViewById(R.id.restaurant_time_t);
        int count=0;
        for(String weekday : getResources().getStringArray(R.array.weekENG)){
            LayoutInflater li = LayoutInflater.from(v.getContext());
            View timetableItem = li.inflate(R.layout.timetable_view,null);
            TextView dow= (TextView) timetableItem.findViewById(R.id.dow);
            dow.setText(getResources().getStringArray(R.array.week)[count]);
            String time="";
            TextView lunch=(TextView)timetableItem.findViewById(R.id.time_lunch);
            if (restaurant.getTimetableLunch()!=null&&restaurant.getTimetableLunch().containsKey(weekday)) {
                time=time.concat(restaurant.getTimetableLunch().get(weekday).get("start")+"-"+restaurant.getTimetableLunch().get(weekday).get("end"));
            }
            if (restaurant.getTimetableDinner()!=null&&restaurant.getTimetableDinner().containsKey(weekday)) {
                if(!time.equals("")) time=time.concat("\n");
                time=time.concat((restaurant.getTimetableDinner().get(weekday).get("start") + "-" + (restaurant.getTimetableDinner().get(weekday).get("end"))));
            }
            if(time.equals("")) time=getString(R.string.closed);
            lunch.setText(time);
            ll.addView(timetableItem);
            count++;
        }
        final ImageView button_img1=(ImageView)v.findViewById(R.id.button_time_img1);
        final ImageView button_img2=(ImageView)v.findViewById(R.id.button_time_img2);
        final TextView button_time_t=(TextView)v.findViewById(R.id.button_time_t);
        final ScrollView scrollView=(ScrollView)v.findViewById(R.id.scrollview);
        button_time_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ll.getVisibility()==View.GONE){
                    ll.setVisibility(View.VISIBLE);
                    button_time_t.setText(R.string.hide_timetable);
                    button_img1.setImageResource(R.drawable.ic_collapse_black);
                    button_img2.setImageResource(R.drawable.ic_collapse_black);
                }
                else {
                    ll.setVisibility(View.GONE);
                    button_time_t.setText(R.string.view_timetable);
                    button_img1.setImageResource(R.drawable.ic_expand_black);
                    button_img2.setImageResource(R.drawable.ic_expand_black);
                }
            }
        });


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            data=(restaurantData) context;
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
        File filePath = new File(dir,id);
        this.imageDownloadTask = storageRoot.child(id).getFile(filePath);
        this.idl = new ImageDownloadListener(imView,dir,id);
        this.imageDownloadTask.addOnSuccessListener(this.idl);
    }

    @Override
    public void onPause(){
        super.onPause();
        this.imageDownloadTask.removeOnSuccessListener(this.idl);
        mapView.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap map){
        LatLng latLng = new LatLng(restaurant.getXCoord(),restaurant.getYCoord());
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(restaurant.getName()));
        map.moveCamera(CameraUpdateFactory.zoomTo(15));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private class ImageDownloadListener implements OnSuccessListener<FileDownloadTask.TaskSnapshot>{
        private ImageView imView;
        private File directory;
        private String id;
        public ImageDownloadListener(ImageView imageView, File imageDirectory, String imageID){
            this.imView = imageView;
            this.directory = imageDirectory;
            this.id = imageID;
        }
        @Override
        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
            File img = new File(this.directory, this.id);
            Uri imgPath = Uri.fromFile(img);
            try {
                Bitmap b = new Picture(imgPath,getActivity().getContentResolver()).getBitmap();
                this.imView.setImageBitmap(b);
            } catch (IOException e) {
                Log.e("getFromNet",e.getMessage());
            }
        }
    }
}
