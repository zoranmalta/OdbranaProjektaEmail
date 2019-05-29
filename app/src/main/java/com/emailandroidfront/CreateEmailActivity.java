package com.emailandroidfront;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.emailandroidfront.model.AccountUser;
import com.emailandroidfront.model.Attachment;
import com.emailandroidfront.model.Message;
import com.emailandroidfront.remote.ApiUtils;
import com.emailandroidfront.remote.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEmailActivity extends AppCompatActivity {
    private static final String TAG ="Attachment" ;
    UserService userService;
    Intent intentA;

    private EditText mEditTextTo;
    private EditText mEditTextBc;
    private EditText mEditTextCc;
    private EditText mEditTextSubject;
    private EditText mEditTextMessage;
    private AccountUser accountUser;
    Message message=new Message();
    List<Attachment> attachments=new ArrayList<>();
    Attachment attachment=new Attachment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_email);
        userService= ApiUtils.getUserService();


        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        Toolbar myToolbar = findViewById(R.id.my_toolbar_create_emails);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        accountUser=getUserFromLogin(sharedPreferences);

        mEditTextTo=findViewById(R.id.edit_text_to);
        mEditTextSubject=findViewById(R.id.edit_text_subject);
        mEditTextMessage=findViewById(R.id.edit_text_message);
        mEditTextBc=findViewById(R.id.edit_text_bc);
        mEditTextCc=findViewById(R.id.edit_text_cc);
        Intent intent=getIntent();
        if(intent!=null){
            mEditTextTo.setText(intent.getStringExtra("to"));
            mEditTextCc.setText(intent.getStringExtra("cc"));
            mEditTextBc.setText(intent.getStringExtra("bcc"));
            mEditTextSubject.setText(intent.getStringExtra("subject"));
            mEditTextMessage.setText(intent.getStringExtra("text"));
        }
    }
    private void sendMail() {
        String sendto=mEditTextTo.getText().toString();
        String [] recipients=sendto.split(",");
        String subject=mEditTextSubject.getText().toString();
        String messageText=mEditTextMessage.getText().toString();
        String sendbc=mEditTextBc.getText().toString();
        String sendcc=mEditTextCc.getText().toString();

        message.setSendto(sendto);
        message.setSendbc(sendbc);
        message.setSendcc(sendcc);
        message.setSubject(subject);
        message.setContent(messageText);
        message.setAccountDto(accountUser);
        message.setSeen(false);
        message.setAttachments(attachments);

        Call<Message> call=userService.addMessage(message);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    Message m = response.body();

                    Toast.makeText(CreateEmailActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateEmailActivity.this, MessageActivity.class);
                    intent.putExtra("message_id", m.getId());
                    intent.putExtra("from", accountUser.getUsername());
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(CreateEmailActivity.this,"Error , please try again",Toast.LENGTH_LONG).show();
            }
        });
//        Intent intent=new Intent(Intent.ACTION_SEND);
//        intent.putExtra(Intent.EXTRA_EMAIL,recipients);
//        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
//        intent.putExtra(Intent.EXTRA_TEXT,message);
//
//        intent.setType("message/rfc822");
//        startActivity(Intent.createChooser(intent,"Choose an email client"));
    }

    //ovde su send i cancel i attachment dugmad iz toolbara
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.toolbar_create_email,menu);

        MenuItem sendItem=menu.findItem(R.id.send_email);
        MenuItem cancelItem=menu.findItem(R.id.cancel_email);
        MenuItem attachmentItem=menu.findItem(R.id.attachment);

        sendItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if( validateCc(mEditTextCc)||validateBc(mEditTextBc)||validateTo(mEditTextTo)) {
                    sendMail();
                }else {
                    Toast.makeText(CreateEmailActivity.this,"Validation failed , please try again",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        cancelItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent=new Intent(CreateEmailActivity.this, EmailsActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
        attachmentItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                intentA=new Intent(Intent.ACTION_GET_CONTENT);
                intentA.setType("*/*");
                startActivityForResult(intentA,7);
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 7:
                if(resultCode==RESULT_OK){
                    String pathHolder=data.getData().getPath();
                    try {
                        attachment.setData(encodeFileToBase64Binary(pathHolder));

                        attachments.add(attachment);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "Nije ucitan attachment");
                        Toast.makeText(CreateEmailActivity.this, "Nije ucitan attachment" , Toast.LENGTH_LONG).show();
                    }
                    Log.d(TAG, "onActivityResult: "+pathHolder);
                    Toast.makeText(CreateEmailActivity.this, pathHolder , Toast.LENGTH_LONG).show();
                }
        }
    }
    private String encodeFileToBase64Binary(String fileName )
            throws IOException {

        File file = new File(fileName);
        String fileString= file.getName();
        Log.d(TAG, "onActivityResult:filestring "+fileString);
        Toast.makeText(CreateEmailActivity.this, fileString , Toast.LENGTH_LONG).show();



        byte[] bytes = loadFile(file);
        byte[] encoded = Base64.encode(bytes,Base64.DEFAULT);
        String encodedString = new String(encoded);

        String [] filename=fileString.trim().split(".");
        Log.d(TAG, "onActivityResult: "+filename[0]);
        Log.d(TAG, "onActivityResult: "+filename[1]);
        attachment.setName(filename[0]);
        attachment.setType(filename[1]);

        return encodedString;
    }
    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
    }

    private boolean validateTo(EditText mEditTextTo) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(mEditTextTo.getText());
        return matcher.matches();
    }
    private boolean validateBc(EditText mEditTextBc) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(mEditTextBc.getText());
       return matcher.matches();
    }
    private boolean validateCc(EditText mEditTextCc) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(mEditTextCc.getText());
       return matcher.matches();
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
}
