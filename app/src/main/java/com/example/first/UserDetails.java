package com.example.first;

public class UserDetails {
    private String userName;
    private String userPass;
    private String userEmail;
    private String userPhone;
    public String getUserName(){
        return userName;
    }
    public String getUserEmail(){
        return userEmail;
    }
    public String getPassowrd(){
        return userPass;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public UserDetails(String userName,String userEmail,String userPassword,String userPhone){
        this.userName = userName;
        this.userPass = userPassword;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
    }



}
