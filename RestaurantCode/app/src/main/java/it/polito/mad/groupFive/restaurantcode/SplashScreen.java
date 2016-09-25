package it.polito.mad.groupFive.restaurantcode;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import it.polito.mad.groupFive.restaurantcode.Home.Home;

/**
 * Created by MacBookRetina on 08/06/16.
 */
public class SplashScreen extends Activity {
    private SharedPreferences sharedPreferences;
    private Notification_Listener notify;
    private static boolean isDBPersistanceEnabled = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup);
        findViewById(R.id.splash_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }

        });

        Thread setup=new Thread(){
            @Override
            public void run() {
                setup();
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent home=new Intent(getBaseContext(),Home.class);
                startActivity(home);
            }
        };
        setup.start();


    }

    @Override
    protected void onStart() {
        super.onStart();



    }

    private void setup()  {
        FirebaseDatabase db;
        db = FirebaseDatabase.getInstance();
        if(!isDBPersistanceEnabled){
            db.setPersistenceEnabled(true);
            isDBPersistanceEnabled = true;
        }
        db = FirebaseDatabase.getInstance();
        FirebaseAuth auth=FirebaseAuth.getInstance();
        db.getReference("favourite").keepSynced(true);
        db.getReference("menu").keepSynced(true);
        db.getReference("order").keepSynced(true);
        sharedPreferences = this.getSharedPreferences(getString(R.string.user_pref), this.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        String ridc=sharedPreferences.getString("rid",null);
        editor.apply();
        String mail =sharedPreferences.getString("email",null);
        String psw =sharedPreferences.getString("psw",null);
        if (mail!=null&&psw!=null){
            auth.signInWithEmailAndPassword(mail,psw);
            editor.putString("uid",auth.getCurrentUser().getUid());}
        if(ridc!=null){
            notify.startActionOrder(getBaseContext(),ridc);
        }
        if(auth.getCurrentUser()!=null){
            notify=new Notification_Listener();
            notify.startActionFavourite(getBaseContext(),auth.getCurrentUser().getUid());
        }





    }
}
