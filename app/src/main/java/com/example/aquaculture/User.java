package com.example.aquaculture;

public class User {
    public String username;
    public String password;
    public String fname;
    public String lname;
    public String role;

    public User(String username, String password, String fname, String lname, String role){
        this.username = username;
        this.password = password;
        this.fname = fname ;
        this.lname = lname;
        this.role = role;
    }
}
