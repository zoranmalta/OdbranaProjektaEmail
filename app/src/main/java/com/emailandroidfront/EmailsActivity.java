package com.emailandroidfront;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.emailandroidfront.adapter.CostumEmailsAdapter;
import com.emailandroidfront.job_service.NotificationJobService;
import com.emailandroidfront.model.AccountUser;
import com.emailandroidfront.model.Message;
import com.emailandroidfront.model.Tag;
import com.emailandroidfront.remote.ApiUtils;
import com.emailandroidfront.remote.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailsActivity extends AppCompatActivity {

    private static final String TAG = "Email job scheduler";
    private static final String CHANNEL_1_ID = "Channel 1";
    UserService userService;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    AccountUser accountUser;
    TextView textView2;

    ListView listView;
    List<Message> messages;
    ArrayList<Tag> tags;
    private static CostumEmailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //postavljamo vrednosti iz sharedPreference- false znaci da ne radi override preko unetih promena
        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        boolean sortList=sharedPreferences.getBoolean("sorted_by",true);
        String refreshTime= sharedPreferences.getString("sync_frequency","15");
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        setContentView(R.layout.activity_emails);
        Toolbar myToolbar = findViewById(R.id.my_toolbar_emails);
        setSupportActionBar(myToolbar);

        accountUser=getUserFromLogin(sharedPreferences);
        String sendto=accountUser.getUsername();

        listView= findViewById(R.id.list_emails);
        messages=new ArrayList<>();
        userService= ApiUtils.getUserService();

        //punimo listu sa incoming messages iz baze podataka pomocu retrofita iz rest servisa
        getInboxMessagesForAccount(sendto,sortList,editor);

        textView2=findViewById(R.id.textview2_emails_account);
        textView2.setText(sendto+" Emails(Will be refresh every "+refreshTime+" min's)");
        textView2.setBackgroundColor(Color.CYAN);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab_emails);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EmailsActivity.this,CreateEmailActivity.class);
                startActivity(intent);
            }
        });

        drawerLayout=findViewById(R.id.drawer_layout_emails);
        navigationView=findViewById(R.id.nav_view_emails);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setChecked(true);
                // close drawer when item is tapped
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.nav_outbox:
                        Intent intent = new Intent(EmailsActivity.this, OutboxActivity.class);
                        startActivity(intent);
                        finish();
                        return true;

                    case R.id.nav_account_profile_emails:
                        Intent intent1=new Intent(EmailsActivity.this,ProfileActivity.class);
                        startActivity(intent1);
                        return true;

                    case R.id.nav_settings_emails:
                        Intent intent3=new Intent(EmailsActivity.this,SettingsActivity.class);
                        startActivity(intent3);
                        return true;

                    case R.id.nav_logout_emails:
                        Intent intent4=new Intent(EmailsActivity.this,LoginActivity.class);
                        startActivity(intent4);
                        finish();
                        return true;
                    default:
                        return true;
                }
            }
        });
        scheduleJob();
    }

    private AccountUser getUserFromLogin(SharedPreferences sharedPreferences) {
        AccountUser accountUser=new AccountUser();

        accountUser.setId(sharedPreferences.getLong("id",1));
        accountUser.setUsername(sharedPreferences.getString("username","user@example.com"));
        accountUser.setPassword(sharedPreferences.getString("password","123"));
        accountUser.setStmp(sharedPreferences.getString("stmp","stmp"));
        accountUser.setPop3(sharedPreferences.getString("pop3","pop3"));

        return accountUser;
    }
    //.setPeriodic(15*60*1000)
    //zakazuje job pozivajuci servis koji ima metode onStart i onStop napravili ga u klasi NotificationJobService
    private void scheduleJob() {
        ComponentName componentName=new ComponentName(this, NotificationJobService.class);
        JobInfo jobInfo=new JobInfo.Builder(123,componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(40*1000)
                .build();
        JobScheduler scheduler= (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode=scheduler.schedule(jobInfo);
        if(resultCode==JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "Job scheduled");
        }else {
            Log.d(TAG, "Job scheduling failed");
        }
    }

    private void cancelJob(){
        JobScheduler scheduler= (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
    }

    public void getInboxMessagesForAccount(String sendto, final boolean sortList,final SharedPreferences.Editor editor){

        Call<List<Message>> call=userService.getInboxMessages(sendto);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if(response.isSuccessful()) {
                    List<Message> resObject = response.body();
                    Log.d("filter za poruke", resObject.toString());

                    messages=resObject;
                    Long maxId=findMaxId(messages);
                    Log.d(TAG, "maxId :"+maxId);
                    editor.remove("maxid");
                    editor.apply();
                    editor.putLong("maxid",maxId);
                    editor.apply();
                    adapter=new CostumEmailsAdapter((ArrayList<Message>) messages,getApplicationContext());
                    listView.setAdapter(adapter);

                    if(sortList){
                        sortArrayList();
                    }else {
                        sortArrayList();
                        descArrayList();
                    }
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Message message=messages.get(position);

                            if(message.isSeen()==false){
                                setMessageSeenToTrue(message.getId());
                            }

                            String usernameFrom=message.getAccountDto().getUsername();
                            Intent intent=new Intent(EmailsActivity.this,MessageActivity.class);
                            intent.putExtra("message_id",message.getId());
                            intent.putExtra("from",usernameFrom);
                            startActivity(intent);
                            Snackbar.make(view, message.getSubject()+" "+message.getSendto()+" Date: "+message.getDate(), Snackbar.LENGTH_LONG)
                                    .setAction("No action", null).show();
                        }
                    });
                }else {
                    Toast.makeText(EmailsActivity.this,"Error , please try again",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                //inicijalizujemo adapter iako nije uspelo ucitavanje messages iz baze podataka
                adapter=new CostumEmailsAdapter(new ArrayList<Message>(),getApplicationContext());
                Toast.makeText(EmailsActivity.this,t.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private Long findMaxId(List<Message> messages) {
        Long max=1L;
        for(Message m :messages){
            if(m.getId()>max){
                max=m.getId();
            }
        }
        return max;
    }

    public void setMessageSeenToTrue(Long id) {

        Call<Message> call=userService.updateMessage(id);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful()){
                    Message resObject=response.body();
                    for(Message m : messages){
                        if(m.getId()==resObject.getId()){
                            m.setSeen(true);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<Message> call, Throwable t) {
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_emails, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        //sa ovim menjamo dugme search koje se nalazi na tastaruti sa back dugmetom koja iskace prilikom searcha
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        //postavljamo text listener na searchview
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(adapter!=null) {
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        //na dume pismo prelazimo na createEmailActivity
        MenuItem menuItem=menu.findItem(R.id.create_email);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent=new Intent(EmailsActivity.this,CreateEmailActivity.class);
                startActivity(intent);
                return false;
            }
        });
        return true;
    }

    //sortira listu
    private void sortArrayList() {
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        adapter.notifyDataSetChanged();
    }
    //postojecu listu reversuje preokrece u suprotnom redu
    private void descArrayList(){
        Collections.reverse(messages);
        adapter.notifyDataSetChanged();
    }
}
