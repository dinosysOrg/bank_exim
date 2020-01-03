package com.rsa.eximbankapp.Model;

import java.io.Serializable;
import java.sql.Date;

public class TransactionModel implements Serializable {

    public boolean isSend;

    public String title;
    public String username;
    public int avatar;
    public Date datetime;
    public int amount;
    public String bankName;
    public String bankAcc;

    public String txId;

    public TransactionModel(boolean isSend, String title, String username, int avatar, Date datetime, int amount, String bankName, String bankAcc) {
        this.isSend = isSend;

        this.title = title;
        this.username = username;
        this.avatar = avatar;
        this.datetime = datetime;
        this.amount = amount;

        this.bankName = bankName;
        this.bankAcc = bankAcc;
    }
}
