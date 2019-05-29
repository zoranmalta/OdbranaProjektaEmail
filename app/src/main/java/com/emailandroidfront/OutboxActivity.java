package com.emailandroidfront;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.emailandroidfront.adapter.CostumOutboxAdapter;
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

public class OutboxActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    UserService userService;

    AccountUser accountUser;

    TextView textView;

    ListView listView;
    List<Message> messages;
    ArrayList<Tag> tags;
    private static CostumOutboxAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outbox);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_emails_outbox);
        setSupportActionBar(myToolbar);

        //postavljamo vrednosti iz sharedPreference- false znaci da ne radi override preko unetih promena
        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        boolean sortList=sharedPreferences.getBoolean("sorted_by",true);
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        //iz sharedPreferences izvlacimo kompletan objekat accountUser
        accountUser=getUserFromLogin(sharedPreferences);

        listView=findViewById(R.id.list_outbox);
        messages=new ArrayList<>();
        userService= ApiUtils.getUserService();

        //postavlja ime ulogovanog korisnika u textview samo za debug (obrisati kasnije)
        textView=findViewById(R.id.textview_account_outbox);


        Long id=accountUser.getId();
        String username=accountUser.getUsername();
        String password=accountUser.getPassword();
        String sharedUsername=sharedPreferences.getString("username","difoltni korisnik");
        textView.setText(sharedUsername);

        FloatingActionButton floatingActionButton=findViewById(R.id.fab_outbox);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OutboxActivity.this,CreateEmailActivity.class);
                startActivity(intent);
            }
        });

        //punimo drawer i view i kacimo listener na kliknuti item u navdraweru
        drawerLayout=findViewById(R.id.drawer_layout_outbox);
        navigationView=findViewById(R.id.nav_view_outbox);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setChecked(true);
                // close drawer when item is tapped
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.nav_inbox:
                        Intent intent = new Intent(OutboxActivity.this, EmailsActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.nav_account_profile_outbox:
                        Intent intent1=new Intent(OutboxActivity.this,ProfileActivity.class);
                        startActivity(intent1);
                        return true;
                    case R.id.nav_settings_outbox:
                        Intent intent3=new Intent(OutboxActivity.this,SettingsActivity.class);
                        startActivity(intent3);
                        return true;
                    case R.id.nav_logout_outbox:
                        Intent intent4=new Intent(OutboxActivity.this, LoginActivity.class);
                        startActivity(intent4);
                        finish();
                        return true;
                    default:
                        return true;
                }
            }
        });

        getOutboxMessagesForAccount(id,sortList);
    }
    public void getOutboxMessagesForAccount( long id,final boolean sortList){
        Call<List<Message>> call=userService.getOutboxMessages(id);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if(response.isSuccessful()){
                    messages=response.body();

                    adapter=new CostumOutboxAdapter((ArrayList<Message>) messages,getApplicationContext());
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

                            String usernameFrom=message.getAccountDto().getUsername();
                            Intent intent=new Intent(OutboxActivity.this,MessageActivity.class);
                            intent.putExtra("message_id",message.getId());
                            intent.putExtra("from",usernameFrom);
                            startActivity(intent);
                            Snackbar.make(view, message.getSubject()+" "+message.getSendto()+" Date: "+message.getDate(), Snackbar.LENGTH_LONG)
                                    .setAction("No action", null).show();
                        }
                    });
                }else {
                    Toast.makeText(OutboxActivity.this,"Error please try again",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                //inicijalizujemo adapter iako nije uspelo ucitavanje messages iz baze podataka
                adapter=new CostumOutboxAdapter(new ArrayList<Message>(),getApplicationContext());
                Toast.makeText(OutboxActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_emails, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        //sa ovim menjamo dugme search koje se nalazi na tastaruti sa back dugmetom koja iskace prilikom searcha
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

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
        return true;
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

    private void sortArrayList() {
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void descArrayList(){
        Collections.reverse(messages);
        adapter.notifyDataSetChanged();
    }
}
