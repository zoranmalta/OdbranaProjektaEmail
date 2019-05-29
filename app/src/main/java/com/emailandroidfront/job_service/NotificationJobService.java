package com.emailandroidfront.job_service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.emailandroidfront.EmailsActivity;
import com.emailandroidfront.MessageActivity;
import com.emailandroidfront.R;
import com.emailandroidfront.model.Message;
import com.emailandroidfront.remote.ApiUtils;
import com.emailandroidfront.remote.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//registrovati servis u androidManifest.xml u application i dodati android:permission="android.permission.BIND_JOB_SERVICE"
public class NotificationJobService extends JobService {

    private static final String CHANNEL_1_ID = "Channel 1" ;
    private static final String TAG = "Notifikacija ";
    UserService userService;
    List<Message> messages;
    SharedPreferences.Editor editor;
    @Override
    public boolean onStartJob(JobParameters params) {

        Log.d(TAG, "onStart job started");

        PreferenceManager.setDefaultValues(this, R.xml.preferences,false);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();
        String username=sharedPreferences.getString("username","");
        Long maxId=sharedPreferences.getLong("maxid",10000000);
        userService= ApiUtils.getUserService();
        messages=new ArrayList<>();

        getInboxMessagesSync(username,maxId);
        jobFinished(params,false);
        return true;
    }

    private void getInboxMessagesSync(String username, final Long maxId) {
        Call<List<Message>> call=userService.getInboxMessages(username);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if(response.isSuccessful()) {
                    List<Message> resObject = response.body();
                    Log.d("pristigle porukeu inbox", resObject.toString());
                    messages = resObject;
                    List<Message> messagesForNotify=new ArrayList<>();
                    int i=0;
                    for(Message m:messages){
                        if(m.getId()>maxId){
                           messagesForNotify.add(m);
                            Log.d("ima novih poruka", String.valueOf(i));
                            i++;
                        }
                    }
                    if(i==1){
                        clearNotifications();
                        triggerNotification(messagesForNotify);
                    }
                    if(i>1){
                        clearNotifications();
                        triggerGroupNotification(messagesForNotify,i);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {

            }
        });
    }
    public void triggerNotification(List<Message> messages ){
        //todo pozovi notifikator
        String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";
        Message message=messages.get(0);

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, MessageActivity.class);
        resultIntent.putExtra("message_id",message.getId());
        resultIntent.putExtra("from",message.getAccountDto().getUsername());
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_markunread_mailbox_black_24dp)
                .setContentTitle(message.getAccountDto().getUsername()+":"+message.getSubject())
                .setContentText(message.getContent())
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }
    public void clearNotifications(){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        // notificationId is a unique int for each notification that you must define
        notificationManager.cancelAll();
    }
    public void triggerGroupNotification(List<Message> messages , int i){
        int SUMMARY_ID = 0;
        String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";

        Intent resultIntent = new Intent(this, EmailsActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        Notification summaryNotification =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                        .setContentTitle("You have "+i+"new Emails")
                        //set content text to support devices running API level < 24
                        .setContentText("Two new messages")
                        .setSmallIcon(R.drawable.ic_email_black_24dp)
                        //build summary info into InboxStyle template
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine("Alex Faarborg  Check this out")
                                .addLine("Jeff Chang    Launch Party")
                                .setBigContentTitle(i+" new messages")
                                .setSummaryText(messages.get(0).getAccountDto().getUsername()))
                        //specify which group this notification belongs to
                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        .setContentIntent(resultPendingIntent)
                        .setAutoCancel(true)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .build();

        int j=1;
        for(Message message : messages){

            Notification notification =
                    new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_markunread_mailbox_black_24dp)
                    .setContentTitle(message.getAccountDto().getUsername()+":"+message.getSubject())
                    .setContentText(message.getContent())
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .setAutoCancel(true)
                    .build();

            notificationManager.notify(j,notification);
            j++;
        }
        notificationManager.notify(0,summaryNotification);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
