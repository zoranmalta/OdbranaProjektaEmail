package com.emailandroidfront.remote;



import com.emailandroidfront.model.AccountUser;
import com.emailandroidfront.model.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @GET("api/account/{username}/{password}")
    Call<AccountUser> login(@Path("username") String username, @Path("password") String password);

    @GET("api/messages/{sendto}/{id}")
    Call<List<Message>> getMessages(@Path("sendto") String sendto, @Path("id") Long id);

    @GET("api/inboxmessages/{sendto}")
    Call<List<Message>> getInboxMessages(@Path("sendto") String sendto);

    @GET("api/outboxmessages/{id}")
    Call<List<Message>> getOutboxMessages(@Path("id") Long id);

    @GET("api/message/{id}")
    Call<Message> getOneMessageById(@Path("id") Long id);

    @PUT("api/updatemessage/{id}")
    Call<Message> updateMessage(@Path("id") Long id);

    @DELETE("api/{id}")
    Call<Void> deleteMessage(@Path("id") Long id);

    @POST("api/addmessage")
    Call<Message> addMessage(@Body Message message);
}
