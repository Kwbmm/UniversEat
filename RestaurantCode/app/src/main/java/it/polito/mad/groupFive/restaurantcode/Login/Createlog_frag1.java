package it.polito.mad.groupFive.restaurantcode.Login;

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
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Customer;
import it.polito.mad.groupFive.restaurantcode.datastructures.RestaurantOwner;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CustomerException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantOwnerException;
import it.polito.mad.groupFive.restaurantcode.libs.RealPathUtil;

/**
 * Created by Giovanni on 27/04/16.
 */
public class Createlog_frag1 extends Fragment{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Codes for intents
    private static final int CAPTURE_IMAGE = 1;
    private static final int SELECT_PICTURE = 2;

    private View v=null;

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Uri userPicUri = null;
    private Customer user=null;
    private RestaurantOwner user_r=null;
    private ImageView userPic = null;
    private EditText txtname;
    private EditText txtsurname;
    private EditText nickname;
    private EditText txtmail;
    private boolean owner;

    public Createlog_frag1(){
        //void constructor
    }

    public static Createlog_frag1 newInstance(String param1, String param2) {
        Createlog_frag1 fragment = new Createlog_frag1();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String METHOD_NAME = this.getClass().getName() + " - onCreateView";
        owner =this.getArguments().getBoolean("owner");
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_createlog_1, container, false);
        txtname = (EditText) v.findViewById(R.id.editText_UserName);
        txtmail = (EditText) v.findViewById(R.id.editText_Mail);
        nickname = (EditText) v.findViewById(R.id.editText_Nickname);
        txtsurname = (EditText) v.findViewById(R.id.editText_surname);

        ImageView userImg = (ImageView) v.findViewById(R.id.imageView_UserImage);
        userImg.setOnClickListener(new View.OnClickListener() {
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


        Button btnNext = (Button) v.findViewById(R.id.Button_Next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(METHOD_NAME,"Press Next: OK");

                Activity a = getActivity();
                if(a instanceof OnFragmentInteractionListener) {
                    setUserData();

                    OnFragmentInteractionListener obs = (OnFragmentInteractionListener) a;
                    obs.onChangeFrag(user,user_r);
                }}});

        return v;

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onChangeFrag(Customer u, RestaurantOwner u_r);
    }


    public void setUserData() {
        final String METHOD_NAME = this.getClass().getName()+" - setUserData";
        SharedPreferences sp=getActivity().getSharedPreferences(getString(R.string.user_pref), CreateLogin.MODE_PRIVATE);
        int uid = sp.getInt("uid",-1);
        ImageView userImg = (ImageView) v.findViewById(R.id.imageView_UserImage);


            if(owner){
                try {
                    user_r = new RestaurantOwner(getActivity(), uid);
                } catch (RestaurantOwnerException e) {
                    e.printStackTrace();
                }
                user_r.setName(txtname.getText().toString());
                user_r.setSurname(txtsurname.getText().toString());
                user_r.setEmail(txtmail.getText().toString());
                user_r.setUserName(nickname.getText().toString());
                try {
                    user_r.saveData();
                } catch (RestaurantOwnerException e) {
                    e.printStackTrace();
                }

                // user_r.setImage64FromDrawable(userImg.getDrawable());

            }
            else {
                try {
                    user = new Customer(getActivity(), uid);
                } catch (CustomerException e) {
                    e.printStackTrace();
                }
                user.setName(txtname.getText().toString());
                user.setSurname(txtsurname.getText().toString());
                user.setEmail(txtmail.getText().toString());
                user.setUserName(nickname.getText().toString());
                try {
                    user.saveData();
                } catch (CustomerException e) {
                    e.printStackTrace();
                }
                // user.setImage64FromDrawable(userImg.getDrawable());
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
                            userPicUri = Uri.fromFile(photo);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, userPicUri);
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
            this.userPic = (ImageView) getActivity().findViewById(R.id.imageView_UserImage);
            this.userPicUri = data.getData();
            try{
                Bitmap imageBitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(userPicUri));
                String imgPath;
                if(Build.VERSION.SDK_INT < 11)
                    imgPath = RealPathUtil.getRealPathFromURI_BelowAPI11(getActivity(), userPicUri);
                else if(Build.VERSION.SDK_INT < 19)
                    imgPath = RealPathUtil.getRealPathFromURI_API11to18(getActivity(), userPicUri);
                else
                    imgPath = RealPathUtil.getRealPathFromURI_API19(getActivity(), userPicUri);
                imageBitmap = detectOrientation(imgPath, imageBitmap);
                this.userPic.setImageDrawable(resize(imageBitmap));
            } catch(FileNotFoundException fnfe) { Log.e(METHOD_NAME,fnfe.getMessage());}
        }
        if(resultCode == getActivity().RESULT_OK && requestCode == CAPTURE_IMAGE){
            this.userPic = (ImageView) getActivity().findViewById(R.id.imageView_UserImage);
            try{
                Bitmap imageBitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(userPicUri));
                imageBitmap = detectOrientation(userPicUri.getPath(),imageBitmap);
                this.userPic.setImageDrawable(resize(imageBitmap));
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

}
