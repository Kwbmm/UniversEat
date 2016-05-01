package it.polito.mad.groupFive.restaurantcode.CreateSimpleMenu;

import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;

/**
 * Created by MacBookRetina on 30/04/16.
 */
public class MenuData {
    Restaurant rest;
    Menu menu;

    public Menu getMenu() {
        return menu;
    }

    public Restaurant getRest() {
        return rest;
    }

    public MenuData(Restaurant rest) {
        this.rest = rest;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}
