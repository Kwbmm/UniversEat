package it.polito.mad.groupFive.restaurantcode.CreateSimpleMenu;

import it.polito.mad.groupFive.restaurantcode.datastructures.Course;
import it.polito.mad.groupFive.restaurantcode.datastructures.Menu;
import it.polito.mad.groupFive.restaurantcode.datastructures.Restaurant;

/**
 * Created by MacBookRetina on 30/04/16.
 */
public class MenuData {
    private Restaurant rest;
    private Menu menu;
    private Course newDish;
    private boolean edit;

    public Menu getMenu() {
        return menu;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public Course getNewDish() {
        return newDish;

    }

    public void setNewDish(Course newDish) {
        this.newDish = newDish;
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