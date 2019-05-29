package com.emailandroidfront.remote;

public class ApiUtils {
    //postavljamo ip address i port rest aplikacije
    public static final String BASE_URL="http://192.168.1.2:8080/";

    public static UserService getUserService(){
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
}
