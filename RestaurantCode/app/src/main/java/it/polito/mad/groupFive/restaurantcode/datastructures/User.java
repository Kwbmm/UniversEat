package it.polito.mad.groupFive.restaurantcode.datastructures;

public class User {
    protected String FireID;
    protected String name;
    protected String surname;
    protected String userName;
    protected String password;
    protected String address;
    protected String email;
    protected String ImagePath;
    protected boolean manager;

    public User(){

    }
    public User(boolean manager){
        this.manager=manager;
    }

    public User(String uid){

    }


    public String getUid() {
        return FireID;
    }

    public void setUid(String fireID) {
        FireID = fireID;
    }



    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        manager = manager;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
