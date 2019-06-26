package com.example.aquaculture.Model;

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

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
