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
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Customer;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.RestaurantOwner;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;
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
    private static final String IMAGE_TYPE = "image/*";
    private static final int CAPTURE_IMAGE = 1;
    private static final int SELECT_PICTURE = 2;

    private View v=null;

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Uri userPicUri = null;
    private User user=null;
    private Bitmap img;
    private ImageView userPicView = null;
    private EditText txtname;
    private EditText txtsurname;
    private EditText nickname;
    private EditText txtmail;
    private EditText txtpassword;
    private EditText txtrepeat;
    private boolean owner;
    private boolean isImageSet=false;

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
        txtpassword = (EditText) v.findViewById(R.id.editText_Password);
        txtrepeat = (EditText) v.findViewById(R.id.editText_Password_repeat);

        this.userPicView = (ImageView) v.findViewById(R.id.imageView_UserImage);
        this.userPicView.setOnClickListener(new View.OnClickListener() {
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
                Activity a = getActivity();
                if(a instanceof OnFragmentInteractionListener) {
                    if(setUserData()){
                        OnFragmentInteractionListener obs = (OnFragmentInteractionListener) a;
                        obs.onChangeFrag(user,img);
                    }
                    else {
                        Toast.makeText(getContext(),getResources().getString(R.string.toastFail),Toast.LENGTH_LONG)
                                .show();
                    }
                }}});

        return v;
    }

    private void pickImage(){
        final CharSequence[] choices = {getResources().getString(R.string.pick_gallery),getResources().getString(R.string.take_picture)};
        final String METHOD_NAME = this.getClass().getName()+" - pickImage";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.alertBox_photo_title));
        builder.setItems(choices,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (choices[which].equals(getResources().getString(R.string.take_picture)) && getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
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
                    Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.intentChooser_select_image)), SELECT_PICTURE);
                }
                else{
                    Log.d(METHOD_NAME,"here");
                }
            }
        });
        builder.show();
    }

    public boolean setUserData() {
        final String METHOD_NAME = this.getClass().getName()+" - setUserData";
        SharedPreferences sp=getActivity().getSharedPreferences(getString(R.string.user_pref), CreateLogin.MODE_PRIVATE);
        String uid = sp.getString("uid",null);
        user = new User(owner);

        if(txtname.getText().toString().trim().equals("") || txtname.getText() == null){
            Log.w(METHOD_NAME,"TextView Name is either empty or null");
            return false;
        }
        user.setName(txtname.getText().toString());

        if(txtsurname.getText().toString().trim().equals("") || txtsurname.getText() == null){
            Log.w(METHOD_NAME,"TextView Surname is either empty or null");
            return false;
        }
        user.setSurname(txtsurname.getText().toString());

        if(txtmail.getText().toString().trim().equals("") || txtmail.getText() == null){
            Log.w(METHOD_NAME,"TextView Email is either empty or null");
            return false;
        }
        user.setEmail(txtmail.getText().toString());

        if(nickname.getText().toString().trim().equals("") || nickname.getText() == null){
            Log.w(METHOD_NAME, "TextView Username is either empty or null");
            return false;
        }
        user.setUserName(nickname.getText().toString());

        if(!isImageSet){
            Log.w(METHOD_NAME,"ImageView Profile Picture is not set");
            return false;
        }
        //this.img=userPicView.getDrawingCache();
        // user.setImageFromDrawable(userPicView.getDrawable());

        if(txtpassword.getText().toString().trim().equals("") || txtpassword.getText() == null){
            Log.w(METHOD_NAME, "TextView Password is either empty or null");
            return false;
        }
        if(!txtpassword.getText().toString().equals(txtrepeat.getText().toString())){
            Toast.makeText(getContext(),getResources().getString(R.string.toastRegisterPasswordFail),Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        user.setPassword(txtpassword.getText().toString());
        return true;
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
        int imageWidth = 600;
        int imageHeight = 600;
        if(resultCode == getActivity().RESULT_OK && requestCode == SELECT_PICTURE){
            this.userPicUri = data.getData();
            try{
                img=new Picture(this.userPicUri,getActivity().getContentResolver(),imageWidth,imageHeight).getBitmap();
                this.userPicView.setImageBitmap(img);
                this.isImageSet = true;
            } catch(IOException ioe) { Log.e(METHOD_NAME,ioe.getMessage());}
        }
        if(resultCode == getActivity().RESULT_OK && requestCode == CAPTURE_IMAGE){
            try{
                img=new Picture(this.userPicUri,getActivity().getContentResolver(),imageWidth,imageHeight).getBitmap();
                this.userPicView.setImageBitmap(img);
                this.isImageSet = true;
            } catch(IOException ioe) { Log.e(METHOD_NAME,ioe.getMessage());}
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
        void onChangeFrag(User u, Bitmap image);
    }
}
