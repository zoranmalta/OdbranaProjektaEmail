package com.emailandroidfront;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.emailandroidfront.adapter.CostumAttachmentAdapter;
import com.emailandroidfront.model.Attachment;
import com.emailandroidfront.model.Message;
import com.emailandroidfront.remote.ApiUtils;
import com.emailandroidfront.remote.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    Message message;
    List<Attachment> attachments;
    UserService userService;
    Long messageId;

    TextView textViewSubject;
    TextView textViewFrom;
    TextView textViewDate;
    TextView textViewTo;
    TextView textViewCc;
    TextView textViewBcc;
    TextView textViewContent;
    ImageView imageView;
    ListView listView;

    private static CostumAttachmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar myToolbar =  findViewById(R.id.my_toolbar_message);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userService= ApiUtils.getUserService();
        attachments=new ArrayList<>();

        textViewSubject=findViewById(R.id.message_subject);
        textViewFrom=findViewById(R.id.message_from);
        textViewDate=findViewById(R.id.message_date);
        textViewTo=findViewById(R.id.message_to);
        textViewCc=findViewById(R.id.message_cc);
        textViewBcc=findViewById(R.id.message_bcc);
        textViewContent=findViewById(R.id.message_content);
        imageView=findViewById(R.id.message_image);
        listView=findViewById(R.id.attachment_list);

        Intent intent=getIntent();
        messageId= intent.getLongExtra("message_id",0);
        String usernameFrom=intent.getStringExtra("from");

        getMessage(messageId,usernameFrom);

    }
    public void getMessage(Long messageId, final String usernameFrom) {
        Call<Message> call=userService.getOneMessageById(messageId);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.body()==null){
                    Toast.makeText(MessageActivity.this,"Refresh your Inbox",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("filter", response.body().toString());
                message=response.body();
                attachments=message.getAttachments();

                textViewSubject.setText(message.getSubject());
                textViewFrom.setText(" From : "+usernameFrom);
                textViewDate.setText(" Date : "+message.getDate());
                textViewTo.setText(" To : "+message.getSendto());
                textViewContent.setText(" Content : "+message.getContent());
                textViewBcc.setText("Cc : "+message.getSendcc());
                textViewBcc.setText("Bcc : "+message.getSendbc());

                adapter=new CostumAttachmentAdapter((ArrayList<Attachment>) attachments,getApplicationContext());
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(MessageActivity.this,"Attachment shows",Toast.LENGTH_LONG).show();
                        Attachment attachment=attachments.get(position);
                        String fileName=attachment.getName()+"."+attachment.getType();
                        Intent intent=new Intent(MessageActivity.this,PDFActivity.class);
                        intent.putExtra("fileName",fileName);
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onFailure(Call<Message> call, Throwable t) {

                adapter=new CostumAttachmentAdapter((ArrayList<Attachment>) attachments,getApplicationContext());
                Toast.makeText(MessageActivity.this,"Error , please try again",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_message, menu);

        MenuItem deleteItem=menu.findItem(R.id.delete_message);
        MenuItem replayItem=menu.findItem(R.id.replay_message);
        MenuItem forwardItem=menu.findItem(R.id.forward_message);
        deleteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                deleteMessage(messageId);
                return false;
            }
        });

        replayItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                setMessageforReplay();
                return false;
            }
        });
        forwardItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                setMessageForwarding();
                return false;
            }
        });

        return true;
    }

    private void setMessageForwarding() {
        Intent intent=new Intent(MessageActivity.this,CreateEmailActivity.class);
        intent.putExtra("to","");
        intent.putExtra("cc","");
        intent.putExtra("bcc","");
        intent.putExtra("text",message.getContent());
        intent.putExtra("subject",message.getSendbc());
        startActivity(intent);
    }

    private void setMessageforReplay() {
        Intent intent=new Intent(MessageActivity.this,CreateEmailActivity.class);
        intent.putExtra("to",message.getAccountDto().getUsername());
        intent.putExtra("cc",message.getSendcc());
        intent.putExtra("bcc",message.getSendbc());
        intent.putExtra("text",message.getContent());
        intent.putExtra("subject",message.getSendbc());
        startActivity(intent);
    }

    private void deleteMessage(Long messageId) {
        Call<Void> call=userService.deleteMessage(messageId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(MessageActivity.this,"Message Deleted",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(MessageActivity.this, EmailsActivity.class);
                startActivity(intent);
                finish();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MessageActivity.this,"Error , please try again",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
