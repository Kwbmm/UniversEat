package it.polito.mad.groupFive.restaurantcode;

import java.util.ArrayList;

import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;

/**
 * Created by MacBookRetina on 14/04/16.
 */
public class Option {
    private Restaurant restaurant;
    private Menu menu;
    private int opt_number;

    public Menu getMenu() {
        return menu;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public Menu getDishes() {

        return this.menu;
    }

    public int getOpt_number() {
        return opt_number;
    }

    public void setOpt_number(int opt_number) {
        this.opt_number = opt_number;
    }

    public Option(Menu menu,Restaurant rest) {
        this.menu=menu;
        this.restaurant=rest;
    }

}
