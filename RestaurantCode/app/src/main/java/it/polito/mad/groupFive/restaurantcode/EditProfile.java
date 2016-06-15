package it.polito.mad.groupFive.restaurantcode;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.polito.mad.groupFive.restaurantcode.datastructures.Picture;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;

/**
 * Created by Giovanni on 04/06/16.
 */
public class EditProfile extends NavigationDrawer{

    //Codes for intents
    private static final String IMAGE_TYPE = "image/*";
    private static final int CAPTURE_IMAGE = 1;
    private static final int SELECT_PICTURE = 2;





    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Uri userPicUri = null;
    private String uid;
    private User user=null;
    private Bitmap img;
    private ImageView userPicView = null;
    private ImageView imageView24;
    private ImageView imageView30;
    private ImageView imageView25;
    private EditText txtmailnew;
    private EditText txtmailold;
    private EditText txtpassword;
    private String password;
    private boolean changemail=true;
    FrameLayout mlay;
    private FirebaseDatabase db;
    private DatabaseReference myRef;
    private SharedPreferences sp;
    private boolean owner;
    private boolean isImageSet=false;
    private boolean toast=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.actionBar_editProfile);

        mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.fragment_editprofil, mlay);

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("User");

        sp = getSharedPreferences(getString(R.string.user_pref),MODE_PRIVATE);
        user =new User(uid);


        imageView24=(ImageView)findViewById(R.id.imageView24);
        imageView30=(ImageView)findViewById(R.id.imageView30);
        imageView25=(ImageView)findViewById(R.id.imageView25);
        txtmailold = (EditText) findViewById(R.id.editText_Mail_old);
        txtmailold.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) imageView24.setColorFilter(getResources().getColor(R.color.colorPrimary));
                else imageView24.setColorFilter(getResources().getColor(R.color.material_grey_600));
            }
        });
        txtmailnew = (EditText) findViewById(R.id.editText_Mail_new);
        txtmailnew.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) imageView30.setColorFilter(getResources().getColor(R.color.colorPrimary));
                else imageView30.setColorFilter(getResources().getColor(R.color.material_grey_600));
            }
        });
        this.userPicView = (ImageView) findViewById(R.id.imageView_UserImage_e);
        txtpassword = (EditText) findViewById(R.id.editText_Password_e);
        txtpassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) imageView25.setColorFilter(getResources().getColor(R.color.colorPrimary));
                else imageView25.setColorFilter(getResources().getColor(R.color.material_grey_600));
            }
        });
        TextView addPic=(TextView) findViewById(R.id.textView_imageText_e);





        addPic.setOnClickListener(new View.OnClickListener() {
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


        TextView btnNext = (TextView) findViewById(R.id.Button_Next_e);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(setUserData()){

                        final FirebaseAuth auth = FirebaseAuth.getInstance();

                        Firebase.setAndroidContext(getApplicationContext());
                        Firebase ref = new Firebase("https://luminous-heat-4574.firebaseio.com/");
                        if(isImageSet) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            img.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            user.setUid(firebaseUser.getUid());
                            //myRef.push().setValue(user);
                            byte[] data = baos.toByteArray();

                            FirebaseStorage storage=FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReferenceFromUrl("gs://luminous-heat-4574.appspot.com");
                            final StorageReference userImg =storageRef.child("Users/"+user.getUid()+".jpg");

                            UploadTask uploadTask = userImg.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }

                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    if(!changemail){
                                        Intent profile = new Intent(getBaseContext(), Profile.class);
                                        startActivity(profile);
                                    }

                                }
                            });
                        }

                        if(changemail) {
                            ref.changeEmail(txtmailold.getText().toString(), txtpassword.getText().toString(), txtmailnew.getText().toString(), new Firebase.ResultHandler() {
                                @Override
                                public void onSuccess() {
                                    // email changed
                                    Log.v("Change mail correct", "Mail changed");
                                    myRef.orderByChild("uid").equalTo(uid).addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            user=dataSnapshot.getValue(User.class);
                                            user.setEmail(txtmailnew.getText().toString());
                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString("email", txtmailnew.getText().toString());
                                            editor.commit();
                                            myRef.child(dataSnapshot.getKey()).setValue(user);
                                            Intent profile = new Intent(getBaseContext(), Profile.class);
                                            startActivity(profile);
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



                                }

                                @Override
                                public void onError(FirebaseError firebaseError) {
                                    // error encountered
                                }
                            });
                        }


                    }
                    else {
                        if(!toast)
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toastFail),Toast.LENGTH_LONG)
                                    .show();
                    }
                }});












    }


    private void pickImage(){
        final CharSequence[] choices = {getResources().getString(R.string.pick_gallery),getResources().getString(R.string.take_picture)};
        final String METHOD_NAME = this.getClass().getName()+" - pickImage";
        final Activity a = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.alertBox_photo_title));
        builder.setItems(choices,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (choices[which].equals(getResources().getString(R.string.take_picture)) && a.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    //Take photo
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(a.getPackageManager()) != null) {
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
        toast=false;

        if(txtpassword.getText().toString().trim().equals("") || txtpassword.getText() == null){
            Log.w(METHOD_NAME, "TextView Password is either empty or null");
            return false;
        }

        //password wrong
        if (!txtpassword.getText().toString().equals(sp.getString("psw","-1"))){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.passwordwrong),Toast.LENGTH_LONG)
                    .show();
            toast=true;
            return false;
        }
        if(txtmailold.getText().toString().trim().equals("") || txtmailold.getText() == null){
            changemail=false;

        }
        if(!txtmailold.getText().toString().equals(sp.getString("email","-1"))){
            if (changemail) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.mailwrong), Toast.LENGTH_SHORT).show();
                toast = true;
                return false;
            }
        }


        if(txtmailnew.getText().toString().trim().equals("") || txtmailnew.getText() == null){
            if(changemail) {
                Log.w(METHOD_NAME, "TextView Email is either empty or null");
                return false;
            }
        }
        if(!isValidEmailAddress(txtmailnew.getText().toString())){
            if(changemail) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.email), Toast.LENGTH_LONG)
                        .show();
                toast = true;
                return false;
            }
        }


        //this.img=userPicView.getDrawingCache();
        // user.setImageFromDrawable(userPicView.getDrawable());


        return true;
    }

    public boolean isStoragePermissionGranted() {
        final String METHOD_NAME = this.getClass().getName()+" - isStoragePermissionGranted";
        final Activity a = this;
        if (Build.VERSION.SDK_INT >= 23) {
            if (a.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
        final Activity a = this;
        int imageWidth = 600;
        int imageHeight = 600;
        if(resultCode == a.RESULT_OK && requestCode == SELECT_PICTURE){
            this.userPicUri = data.getData();
            try{
                img=new Picture(this.userPicUri,a.getContentResolver(),imageWidth,imageHeight).getBitmap();
                this.userPicView.setImageBitmap(img);
                this.isImageSet = true;
            } catch(IOException ioe) { Log.e(METHOD_NAME,ioe.getMessage());}
        }
        if(resultCode == a.RESULT_OK && requestCode == CAPTURE_IMAGE){
            try{
                img=new Picture(this.userPicUri,a.getContentResolver(),imageWidth,imageHeight).getBitmap();
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


    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}
