package com.emailandroidfront.model;

import android.os.Parcel;
import android.os.Parcelable;


//da bi smo mogli da citav objekat prosledimo kroz intent moramo implementirati Parcelable
// interface i njegove metode kao i novi konstruktor samo palimo crvenu lampicu i resavamo sve

public class AccountUser implements Parcelable {
    private long id;
    private String stmp;
    private String pop3;
    private String username;
    private String password;

    public AccountUser(long id,String stmp,String pop3,String username,String password){
        this.id=id;
        this.stmp=stmp;
        this.pop3=pop3;
        this.username=username;
        this.password=password;
    }

    public AccountUser(Parcel in) {
        id = in.readLong();
        stmp = in.readString();
        pop3 = in.readString();
        username = in.readString();
        password = in.readString();
    }

    public AccountUser() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(stmp);
        dest.writeString(pop3);
        dest.writeString(username);
        dest.writeString(password);
    }

    public static final Creator<AccountUser> CREATOR = new Creator<AccountUser>() {
        @Override
        public AccountUser createFromParcel(Parcel in) {
            return new AccountUser(in);
        }

        @Override
        public AccountUser[] newArray(int size) {
            return new AccountUser[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStmp() {
        return stmp;
    }

    public void setStmp(String stmp) {
        this.stmp = stmp;
    }

    public String getPop3() {
        return pop3;
    }

    public void setPop3(String pop3) {
        this.pop3 = pop3;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
