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
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;

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
    private ImageView imageView10;
    private ImageView imageView11;
    private ImageView imageView12;
    private ImageView imageView13;
    private ImageView imageView31;
    private ImageView imageView32;
    private EditText txtname;
    private EditText txtsurname;
    private EditText nickname;
    private EditText txtmail;
    private EditText txtpassword;
    private EditText txtrepeat;
    private TextView btnNext;
    private ProgressBar progressBar;
    private boolean owner;
    private boolean isImageSet=false;
    private boolean toast=false;

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
        imageView10=(ImageView)v.findViewById(R.id.imageView10);
        imageView11=(ImageView)v.findViewById(R.id.imageView11);
        imageView12=(ImageView)v.findViewById(R.id.imageView12);
        imageView13=(ImageView)v.findViewById(R.id.imageView13);
        imageView31=(ImageView)v.findViewById(R.id.imageView31);
        imageView32=(ImageView)v.findViewById(R.id.imageView32);
        txtname = (EditText) v.findViewById(R.id.editText_UserName);
        txtname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) imageView10.setColorFilter(getResources().getColor(R.color.colorPrimary));
                else imageView10.setColorFilter(getResources().getColor(R.color.material_grey_600));
            }
        });
        txtmail = (EditText) v.findViewById(R.id.editText_Mail);
        txtmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) imageView13.setColorFilter(getResources().getColor(R.color.colorPrimary));
                else imageView13.setColorFilter(getResources().getColor(R.color.material_grey_600));
            }
        });
        nickname = (EditText) v.findViewById(R.id.editText_Nickname);
        nickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) imageView11.setColorFilter(getResources().getColor(R.color.colorPrimary));
                else imageView11.setColorFilter(getResources().getColor(R.color.material_grey_600));
            }
        });
        txtsurname = (EditText) v.findViewById(R.id.editText_surname);
        txtsurname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) imageView31.setColorFilter(getResources().getColor(R.color.colorPrimary));
                else imageView31.setColorFilter(getResources().getColor(R.color.material_grey_600));
            }
        });
        txtpassword = (EditText) v.findViewById(R.id.editText_Password);
        txtpassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) imageView12.setColorFilter(getResources().getColor(R.color.colorPrimary));
                else imageView12.setColorFilter(getResources().getColor(R.color.material_grey_600));
            }
        });
        txtrepeat = (EditText) v.findViewById(R.id.editText_Password_repeat);
        txtrepeat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) imageView32.setColorFilter(getResources().getColor(R.color.colorPrimary));
                else imageView32.setColorFilter(getResources().getColor(R.color.material_grey_600));
            }
        });
        progressBar=(ProgressBar)v.findViewById(R.id.progressBar7);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        this.userPicView = (ImageView) v.findViewById(R.id.imageView_UserImage);
        TextView selectPic=(TextView)v.findViewById(R.id.textView_imageText);
        selectPic.setOnClickListener(new View.OnClickListener() {
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


        btnNext = (TextView) v.findViewById(R.id.Button_Next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNext.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                Activity a = getActivity();
                if(a instanceof OnFragmentInteractionListener) {
                    if(setUserData()){
                        OnFragmentInteractionListener obs = (OnFragmentInteractionListener) a;
                        obs.onChangeFrag(user,img);
                    }
                    else {
                        if(!toast)
                        Toast.makeText(getContext(),getResources().getString(R.string.toastFail),Toast.LENGTH_LONG)
                                .show();
                        btnNext.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }}});

        return v;
    }

    private void pickImage(){
        final CharSequence[] choices = {getResources().getString(R.string.pick_gallery),getResources().getString(R.string.take_picture)};
        final String METHOD_NAME = this.getClass().getName()+" - pickImage";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                            //Log.e(METHOD_NAME, ioe.getMessage());
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
                    //Log.d(METHOD_NAME,"here");
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
        toast=false;

        if(txtname.getText().toString().trim().equals("") || txtname.getText() == null){
            //Log.w(METHOD_NAME,"TextView Name is either empty or null");
            return false;
        }
        user.setName(txtname.getText().toString());

        if(txtsurname.getText().toString().trim().equals("") || txtsurname.getText() == null){
            //Log.w(METHOD_NAME,"TextView Surname is either empty or null");
            return false;
        }
        user.setSurname(txtsurname.getText().toString());

        if(txtmail.getText().toString().trim().equals("") || txtmail.getText() == null){
            //Log.w(METHOD_NAME,"TextView Email is either empty or null");
            return false;
        }
        if(!isValidEmailAddress(txtmail.getText().toString())){
            Toast.makeText(getContext(),getResources().getString(R.string.email),Toast.LENGTH_LONG)
                    .show();
            toast=true;
            return false;
        }
        user.setEmail(txtmail.getText().toString());

        if(nickname.getText().toString().trim().equals("") || nickname.getText() == null){
            //Log.w(METHOD_NAME, "TextView Username is either empty or null");
            return false;
        }
        user.setUserName(nickname.getText().toString());

        if(!isImageSet){
            //Log.w(METHOD_NAME,"ImageView Profile Picture is not set");
            return false;
        }
        //this.img=userPicView.getDrawingCache();
        // user.setImageFromDrawable(userPicView.getDrawable());

        if(txtpassword.getText().toString().trim().equals("") || txtpassword.getText() == null){
            //Log.w(METHOD_NAME, "TextView Password is either empty or null");
            return false;
        }
        if (txtpassword.getText().length()<6){
            Toast.makeText(getContext(),getResources().getString(R.string.password6),Toast.LENGTH_LONG)
                    .show();
            toast=true;
            return false;
        }
        if(!txtpassword.getText().toString().equals(txtrepeat.getText().toString())){
            Toast.makeText(getContext(),getResources().getString(R.string.toastRegisterPasswordFail),Toast.LENGTH_LONG)
                    .show();
            toast=true;
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
                //Log.v(METHOD_NAME,"Permission granted");
                return true;
            } else {

                //Log.v(METHOD_NAME,"Permission revoked");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //Log.v(METHOD_NAME,"Permission granted");
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
            } catch(IOException ioe) {
                //Log.e(METHOD_NAME,ioe.getMessage());
            }
        }
        if(resultCode == getActivity().RESULT_OK && requestCode == CAPTURE_IMAGE){
            try{
                img=new Picture(this.userPicUri,getActivity().getContentResolver(),imageWidth,imageHeight).getBitmap();
                this.userPicView.setImageBitmap(img);
                this.isImageSet = true;
            } catch(IOException ioe) {
                //Log.e(METHOD_NAME,ioe.getMessage());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        final String METHOD_NAME = this.getClass().getName() + " - onRequestPermissionResult";
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Log.v(METHOD_NAME,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            pickImage();
        } else {
            //Log.e(METHOD_NAME,"Permission: "+permissions[0]+" was "+grantResults[0]);
        }
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

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}
