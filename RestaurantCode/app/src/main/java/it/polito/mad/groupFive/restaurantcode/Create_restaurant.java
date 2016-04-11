package it.polito.mad.groupFive.restaurantcode;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class Create_restaurant extends NavigationDrawer implements Create_restaurant_frag.OnFragmentInteractionListener {

    @Override
    public void onFragmentInteraction(Uri uri) {
        final String METHOD_NAME = this.getClass().getName()+" - onFragInt";
        Log.d(METHOD_NAME,"Hi!");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String METHOD_NAME = this.getClass().getName()+" - onCreate";
        setContentView(R.layout.activity_create_resturant);
    }
}
