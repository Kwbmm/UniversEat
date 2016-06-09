package it.polito.mad.groupFive.restaurantcode;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Notification_Listener extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "it.polito.mad.groupFive.restaurantcode.action.FOO";
    private static final String ACTION_BAZ = "it.polito.mad.groupFive.restaurantcode.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "it.polito.mad.groupFive.restaurantcode.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "it.polito.mad.groupFive.restaurantcode.extra.PARAM2";

    public Notification_Listener() {
        super("Notification_Listener");
    }
    private int count=0;
    private ArrayList<String> menu_favs;
    private ArrayList<String> rest_favs; private static Boolean alive=false;

    public static void setAlive(Boolean alive) {
        Notification_Listener.alive = alive;
    }



    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFavourite(Context context, String uid) {

        Intent intent = new Intent(context, Notification_Listener.class);
        intent.setAction("favourite");
        intent.putExtra("uid", uid);
            alive=true;
        context.startService(intent);


    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionOrder(Context context, String rid) {


        Intent intent = new Intent(context, Notification_Listener.class);
        intent.setAction("Order");
        intent.putExtra("rid", rid);

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent!=null) {
            if(intent.getAction()=="Order"){
            String rid = intent.getExtras().getString("rid");


                FirebaseDatabase db;
                db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference("order");
                ref.orderByChild("rid").equalTo(rid).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String key = dataSnapshot.getKey();
                        Boolean noty = (boolean) dataSnapshot.child("notified").getValue();
                        String name = (String) dataSnapshot.child("menuname").getValue();
                        String date = (String) dataSnapshot.child("date").getValue();
                        if (noty == false) {
                            try {
                            SimpleDateFormat format=new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
                            SimpleDateFormat formatback=new SimpleDateFormat("EEE d MMM HH:mm");

                              Date  date2 = format.parse(date);
                                String fdate=formatback.format(date2);
                                Notification_new_order.notify(getBaseContext(), name, fdate, count);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                            }
                            count++;
                            FirebaseDatabase db;
                            db = FirebaseDatabase.getInstance();
                            DatabaseReference ref = db.getReference("order");
                            ref.child(key).child("notified").setValue(true);

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


        }if(intent.getAction()=="favourite"){
                menu_favs=new ArrayList<>();
                rest_favs=new ArrayList<>();
                final String uid=intent.getExtras().getString("uid");
                final FirebaseDatabase db=FirebaseDatabase.getInstance();
                DatabaseReference favs=db.getReference("favourite");
                favs.child(intent.getExtras().getString("uid")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> rfav=dataSnapshot.getChildren();
                        for(DataSnapshot d:rfav) {
                            rest_favs.add(d.getKey());
                            Iterable<DataSnapshot> mfav=d.getChildren();
                            for(DataSnapshot m:mfav) {
                                menu_favs.add(m.getKey());
                            }
                        }


                            for (final String rid:rest_favs){
                               DatabaseReference menuref= db.getReference("menu");
                                Log.v("rid",rid);
                                menuref.orderByChild("rid").equalTo(rid).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        if(menu_favs.contains(dataSnapshot.getKey())){

                                        }else{
                                            Formatter f=new Formatter();
                                            Float price=Float.parseFloat(dataSnapshot.child("price").getValue().toString());
                                            NotificationNewMenu.notify(getBaseContext(),dataSnapshot.child("name").getValue().toString(),2,rid,dataSnapshot.getKey(),f.format("%.2f",price).toString());
                                            DatabaseReference favs=db.getReference("favourite");
                                            favs.child(uid).child(rid).child(dataSnapshot.getKey()).setValue(true);

                                        }
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
                        }



                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;

    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        this.stopSelf();
        super.onDestroy();
    }
}
