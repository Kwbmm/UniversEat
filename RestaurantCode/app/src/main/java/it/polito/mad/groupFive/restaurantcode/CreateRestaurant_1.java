package it.polito.mad.groupFive.restaurantcode;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
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

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.UserException;
import it.polito.mad.groupFive.restaurantcode.libs.RealPathUtil;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateRestaurant_1.onFragInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateRestaurant_1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateRestaurant_1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Codes for intents
    private static final int CAPTURE_IMAGE = 1;
    private static final int SELECT_PICTURE = 2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Uri restaurantPicUri = null;
    private View parentView=null;

    private onFragInteractionListener mListener;
    private ImageView restaurantPic = null;

    private Restaurant restaurant = null;

    public CreateRestaurant_1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateRestaurant_1.
     */
    // TODO: Rename and change types and number of parameters
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
        final String METHOD_NAME = this.getClass().getName()+"onCreate";
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final String METHOD_NAME = this.getClass().getName()+" - onCreateView";

        this.parentView = inflater.inflate(R.layout.fragment_create_restaurant_1, container, false);
        ImageView restaurantImg = (ImageView) this.parentView.findViewById(R.id.imageView_RestaurantImage);
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

    private boolean setRestaurantData() {
        final String METHOD_NAME = this.getClass().getName()+" - setRestaurantData";
        SharedPreferences sp=getActivity().getSharedPreferences(getString(R.string.user_pref), CreateRestaurant.MODE_PRIVATE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                restaurant = new Restaurant(getActivity(),ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE),sp.getInt("uid",-1));
            else //TODO Move randInt inside dataStructures classes
                restaurant = new Restaurant(getActivity(),randInt(),sp.getInt("uid",-1));
            TextView name = (TextView) parentView.findViewById(R.id.editText_RestaurantName);
            if(name.getText().toString().equals("") || name.getText() == null)
                return false;
            restaurant.setName(name.getText().toString());

            TextView description = (TextView) parentView.findViewById(R.id.editText_Description);
            if(description.getText().toString().equals("") || description.getText() == null)
                return false;
            restaurant.setDescription(description.getText().toString());

            ImageView restaurantImg = (ImageView) parentView.findViewById(R.id.imageView_RestaurantImage);
            if(restaurantImg.getDrawable() == null)
                return false;
            //TODO Fix this!!
            //restaurant.setImage64FromDrawable(restaurantImg.getDrawable());

            TextView telephone = (TextView) parentView.findViewById(R.id.editText_Telephone);
            if(telephone.getText().toString().equals("") || telephone.getText() == null)
                return false;
            //restaurant.setTelephone(telephone.getText().toString());

            TextView website = (TextView) parentView.findViewById(R.id.editText_Website);
            if(website.getText().toString().equals("") || website.getText() == null)
                return false;
            //restaurant.setWebsite(website.getText().toString());

            return true;
        } catch (RestaurantException |
                UserException |
                JSONException e) {
            Log.e(METHOD_NAME,e.getMessage());
            return false;
        } catch (IOException e) {
            Log.e(METHOD_NAME, e.getMessage());
            return false;
        }
    }

    //TODO Move randInt inside the dataStructures classes
    public static int randInt() {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand= new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt(Integer.MAX_VALUE -1 );
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
                    intent.setType("image/*");
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
        if(resultCode == getActivity().RESULT_OK && requestCode == SELECT_PICTURE){
            this.restaurantPic = (ImageView) getActivity().findViewById(R.id.imageView_RestaurantImage);
            this.restaurantPicUri = data.getData();
            try{
                Bitmap imageBitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(restaurantPicUri));
                String imgPath;
                if(Build.VERSION.SDK_INT < 11)
                    imgPath = RealPathUtil.getRealPathFromURI_BelowAPI11(getActivity(), restaurantPicUri);
                else if(Build.VERSION.SDK_INT < 19)
                    imgPath = RealPathUtil.getRealPathFromURI_API11to18(getActivity(), restaurantPicUri);
                else
                    imgPath = RealPathUtil.getRealPathFromURI_API19(getActivity(), restaurantPicUri);
                imageBitmap = detectOrientation(imgPath, imageBitmap);
                this.restaurantPic.setImageDrawable(resize(imageBitmap));
            } catch(FileNotFoundException fnfe) { Log.e(METHOD_NAME,fnfe.getMessage());}
        }
        if(resultCode == getActivity().RESULT_OK && requestCode == CAPTURE_IMAGE){
            this.restaurantPic = (ImageView) getActivity().findViewById(R.id.imageView_RestaurantImage);
            try{
                Bitmap imageBitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(restaurantPicUri));
                imageBitmap = detectOrientation(restaurantPicUri.getPath(),imageBitmap);
                this.restaurantPic.setImageDrawable(resize(imageBitmap));
            } catch(FileNotFoundException ffe){Log.e(METHOD_NAME,ffe.getMessage());}
        }
    }

    private Bitmap detectOrientation(String pathToImg, Bitmap bitmap){
        try{
            ExifInterface ei = new ExifInterface(pathToImg);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(bitmap, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(bitmap, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(bitmap,270);
            }
        } catch(IOException ioe){ Log.e("detectOrientation", ioe.getMessage());}
        return bitmap;
    }

    private Bitmap rotateImage(Bitmap source, float angle){
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    private Drawable resize(Bitmap img){

        if (img.getHeight() <= 512 && img.getWidth() <= 512)
            return new BitmapDrawable(getResources(),img);
        if(img.getHeight() > img.getWidth()){
            //Width:Height = x : 512
            Bitmap bitmapResized = Bitmap.createScaledBitmap(img, img.getWidth()*512/img.getHeight(), 512, false);
            return new BitmapDrawable(getResources(),bitmapResized);
        }
        else if(img.getHeight() < img.getWidth()){
            //Width:Height = 512 : x
            Bitmap bitmapResized = Bitmap.createScaledBitmap(img, 512, img.getHeight()*512/img.getWidth(), false);
            return new BitmapDrawable(getResources(),bitmapResized);
        }
        else{
            Bitmap bitmapResized = Bitmap.createScaledBitmap(img, 512, 512, false);
            return new BitmapDrawable(getResources(),bitmapResized);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onFragInteractionListener) {
            mListener = (onFragInteractionListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface onFragInteractionListener {
        // TODO: Update argument type and name
        void onChangeFrag1(Restaurant r);
    }
}
