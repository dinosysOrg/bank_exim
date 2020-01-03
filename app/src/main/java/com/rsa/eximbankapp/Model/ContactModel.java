package com.rsa.eximbankapp.Model;

public class ContactModel {

    public String name;
    public int avatar;
    public String bankName;
    public String bankAcc;

    public ContactModel(String name, int avatar, String bankName, String bankAcc) {
        this.name = name;
        this.avatar = avatar;
        this.bankName = bankName;
        this.bankAcc = bankAcc;
    }
}
