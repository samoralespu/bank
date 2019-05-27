package com.diesel.BankApp;


public class User {
    public String id;
    public String userName;
    public String change_date;
    public String password;
    public String account_number;

    public User(String id, String userName, String change_date, String password, String account_number) {
        this.id = id;
        this.userName = userName;
        this.change_date = change_date;
        this.password = password;
        this.account_number = account_number;
    }

}