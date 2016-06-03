package it.polito.mad.groupFive.restaurantcode;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, Notification_Listener.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, Notification_Listener.class);
        intent.putExtra("rid", param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent!=null) {
            String rid = intent.getExtras().getString("rid");
            while (true) {


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
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }}

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
