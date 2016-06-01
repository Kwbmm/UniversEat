package it.polito.mad.groupFive.restaurantcode.Login;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import it.polito.mad.groupFive.restaurantcode.NavigationDrawer;
import it.polito.mad.groupFive.restaurantcode.R;
import it.polito.mad.groupFive.restaurantcode.datastructures.Customer;
import it.polito.mad.groupFive.restaurantcode.datastructures.RestaurantOwner;
import it.polito.mad.groupFive.restaurantcode.datastructures.User;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.CustomerException;
import it.polito.mad.groupFive.restaurantcode.datastructures.exceptions.RestaurantOwnerException;

/**
 * Created by Giovanni on 22/04/16.
 */
public class CreateLogin extends NavigationDrawer implements Createlog_frag.OnFragmentInteractionListener, Createlog_frag1.OnFragmentInteractionListener{
    private String uid;
    private boolean owner;
    private User user_r=null;
    private User user=null;
    private Bundle bundle;
    private Bitmap image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD_NAME =this.getClass().getName()+" - OnCreate";
        super.onCreate(savedInstanceState);
        FrameLayout mlay= (FrameLayout) findViewById(R.id.frame);
        mlay.inflate(this, R.layout.activity_createlogin, mlay);
        Createlog_frag fragment= new Createlog_frag();
        getSupportFragmentManager().beginTransaction().add(R.id.ac_login,fragment).commit();
    }

    public void onChangeFrag0(boolean o){
        final String METHOD_NAME = this.getClass().getName() + " - onChangeFrag0";
        SharedPreferences sp=getSharedPreferences(getString(R.string.user_pref), CreateLogin.MODE_PRIVATE);
        uid=sp.getString("uid",null);
        this.owner=o;
        user = new User(owner);

        bundle = new Bundle();
        bundle.putBoolean("owner", owner);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean("owner",owner);
        editor.apply();
        Createlog_frag1 frag1 = new Createlog_frag1();
        frag1.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.ac_login,frag1).addToBackStack(null).commit();
    }

    public void onChangeFrag(User u, Bitmap image) {
        final String METHOD_NAME = this.getClass().getName() + " - onChangeFrag";
        SharedPreferences sharedPreferences;
        sharedPreferences = this.getSharedPreferences(getString(R.string.user_pref), this.MODE_PRIVATE);

        this.image=image;
        user.setName(u.getName());
        user.setSurname(u.getSurname());
        user.setEmail(u.getEmail());
        user.setUserName(u.getUserName());
        user.setPassword(u.getPassword());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new Created(user));

        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("logged",true);
        editor.apply();
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
    private class Created implements OnCompleteListener {
        User user;
        FirebaseDatabase db;
        DatabaseReference myRef;

        public Created(User user) {
            this.user = user;
        }


        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                Log.v("login", "Success");
                String key;
                db = FirebaseDatabase.getInstance();
                myRef = db.getReference("User");
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword());
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.user_pref),MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString("uid",auth.getCurrentUser().getUid());


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                FirebaseUser firebaseUser = auth.getCurrentUser();
                user.setUid(firebaseUser.getUid());
                myRef.push().setValue(user);
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
                        finish();
                    }
                });
            } else {
                Log.d("login", "Failed");
            }
        }
    }
}