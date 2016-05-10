package it.polito.mad.groupFive.restaurantcode.CreateRestaurant;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateRestaurant_1.onFragInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateRestaurant_1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateRestaurant_1 extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Codes for intents
    private static final String IMAGE_TYPE = "image/*";
    private static final int CAPTURE_IMAGE = 1;
    private static final int SELECT_PICTURE = 2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Uri restaurantPicUri = null;
    private View parentView=null;

    private onFragInteractionListener mListener;
    private ImageView restaurantPicView = null;
    private boolean isImageSet=false;

    private Restaurant restaurant = null;
    /*view items*/
    private ImageView restaurantImg;
    private TextView description;
    private TextView name;
    private TextView telephone;
    private TextView website;

    public CreateRestaurant_1() {
    }

    public interface getRestaurant{
        public Restaurant getRest();
        public Boolean editmode();

    }
    public getRestaurant getR;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateRestaurant_1.
     */
    public static CreateRestaurant_1 newInstance(String param1, String param2) {
        CreateRestaurant_1 fragment = new CreateRestaurant_1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String METHOD_NAME = this.getClass().getName()+" onCreate";
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final String METHOD_NAME = this.getClass().getName()+" - onCreateView";

        this.parentView = inflater.inflate(R.layout.fragment_create_restaurant_1, container, false);

        restaurantImg = (ImageView) this.parentView.findViewById(R.id.imageView_RestaurantImage);
        name = (TextView) parentView.findViewById(R.id.editText_RestaurantName);
        description=(TextView) parentView.findViewById(R.id.editText_Description);
        restaurantImg = (ImageView) parentView.findViewById(R.id.imageView_RestaurantImage);
        telephone= (TextView) parentView.findViewById(R.id.editText_Telephone);
        website= (TextView) parentView.findViewById(R.id.editText_Website);
        if(getR.editmode()){
        fetchData();}
        restaurantImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStoragePermissionGranted()){
                    /*
                    pickImage is called here only after the first run.
                    On the first run, the call to isStoragePermissionGranted will return false.
                    However if the permission, inside onRequestPermissionsResult, is granted
                    the call to pickImage() is done over there even if isStoragePermissionGranted
                    will return false.
                     */
                    pickImage();
                }
            }
        });

        Button btnNext = (Button) this.parentView.findViewById(R.id.Button_Next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity a = getActivity();
                if(a instanceof onFragInteractionListener) {
                    if(setRestaurantData()){
                        onFragInteractionListener obs = (onFragInteractionListener) a;
                        obs.onChangeFrag1(restaurant);

                    }
                    else{
                        Toast.makeText(getContext(),getResources().getString(R.string.toastFail),Toast.LENGTH_LONG)
                        .show();
                    }
                }
            }

        });
        return this.parentView;
    }

    private void fetchData(){
        restaurant=getR.getRest();
        name.setText(restaurant.getName());
        description.setText(restaurant.getDescription());
        telephone.setText(restaurant.getTelephone());
        website.setText(restaurant.getWebsite());
        restaurantImg.setImageBitmap(restaurant.getImageBitmap());
    }

    private boolean setRestaurantData() {
        final String METHOD_NAME = this.getClass().getName()+" - setRestaurantData";
        SharedPreferences sp=getActivity().getSharedPreferences(getString(R.string.user_pref), CreateRestaurant.MODE_PRIVATE);


        try {
            if(getR.editmode()){
                isImageSet=true;
            }else {
            restaurant=new Restaurant(getContext());
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("rid",restaurant.getRid());
            editor.apply();}


            if(name.getText().toString().equals("") || name.getText() == null){
                Log.w(METHOD_NAME,"TextView RestaurantName is either empty or null");
                return false;
            }
            restaurant.setName(name.getText().toString());


            if(description.getText().toString().equals("") || description.getText() == null){
                Log.w(METHOD_NAME,"TextView Description is either empty or null");
                return false;
            }
            restaurant.setDescription(description.getText().toString());


            if(!isImageSet){
                Log.w(METHOD_NAME,"ImageView RestaurantImage is null");
                return false;
            }
            if(!getR.editmode()){
            restaurant.setImageFromDrawable(restaurantImg.getDrawable());}


            if(telephone.getText().toString().equals("") || telephone.getText() == null){
                Log.w(METHOD_NAME,"TextView Telephone is either empty or null");
                return false;
            }
            restaurant.setTelephone(telephone.getText().toString());


            if(website.getText().toString().equals("") || website.getText() == null){
                Log.w(METHOD_NAME,"TextView Website is either empty or null");
                return false;
            }
            restaurant.setWebsite(website.getText().toString());

            return true;
        } catch (RestaurantException e) {
            Log.e(METHOD_NAME,e.getMessage());
            return false;
        }
    }

    private void pickImage(){
        final CharSequence[] choices = {getResources().getString(R.string.pick_gallery),getResources().getString(R.string.take_picture)};
        final String METHOD_NAME = this.getClass().getName()+" - pickImage";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.alertBox_photo_title));
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (choices[which].equals(getResources().getString(R.string.take_picture)) &&
                        getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    //Take photo
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                        File photo = null;
                        try {
                            photo = createImageFile();
                        } catch (IOException ioe) {
                            Log.e(METHOD_NAME, ioe.getMessage());
                        }
                        if (photo != null) {
                            restaurantPicUri = Uri.fromFile(photo);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, restaurantPicUri);
                            startActivityForResult(cameraIntent, CAPTURE_IMAGE);
                        }
                    }
                } else if (choices[which].equals(getResources().getString(R.string.pick_gallery))) {
                    //Choose from gallery
                    Intent intent = new Intent();
                    intent.setType(IMAGE_TYPE);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.intentChooser_select_image)), SELECT_PICTURE);
                }
            }
        });
        builder.show();
    }

    public boolean isStoragePermissionGranted() {
        final String METHOD_NAME = this.getClass().getName()+" - isStoragePermissionGranted";
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(METHOD_NAME,"Permission granted");
                return true;
            } else {

                Log.v(METHOD_NAME,"Permission revoked");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(METHOD_NAME,"Permission granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        final String METHOD_NAME = this.getClass().getName()+" - onRequestPermissionResult";
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(METHOD_NAME,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            pickImage();
        }
        else
            Log.e(METHOD_NAME,"Permission: "+permissions[0]+" was "+grantResults[0]);
    }

    private File createImageFile() throws IOException {
        final String METHOD_NAME = this.getClass().getName()+" - createImageFile";
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = getResources().getString(R.string.app_name) + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final String METHOD_NAME = this.getClass().getName()+" - onActivityResult";
        super.onActivityResult(requestCode, resultCode, data);
        int imageWidth = 600;
        int imageHeight = 600;
        if(resultCode == getActivity().RESULT_OK && requestCode == SELECT_PICTURE){
            this.restaurantPicView = (ImageView) getActivity().findViewById(R.id.imageView_RestaurantImage);
            this.restaurantPicUri = data.getData();
            try{
                this.restaurantImg.setImageBitmap(new Picture(this.restaurantPicUri,getActivity().getContentResolver(),imageWidth,imageHeight).getBitmap());
                this.isImageSet = true;
            } catch(IOException ioe) { Log.e(METHOD_NAME,ioe.getMessage());}
        }
        if(resultCode == CreateRestaurant.RESULT_OK && requestCode == CAPTURE_IMAGE){
            this.restaurantPicView = (ImageView) getActivity().findViewById(R.id.imageView_RestaurantImage);
            try{
                this.restaurantImg.setImageBitmap(new Picture(this.restaurantPicUri,getActivity().getContentResolver(),imageWidth,imageHeight).getBitmap());
                this.isImageSet = true;
            } catch(IOException ioe){Log.e(METHOD_NAME,ioe.getMessage());}
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onFragInteractionListener) {
            mListener = (onFragInteractionListener) context;
            getR=(getRestaurant)context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onFragInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface onFragInteractionListener {
        void onChangeFrag1(Restaurant r);
    }
}
