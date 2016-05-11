package com.sd.bugsbunny.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sd.bugsbunny.Models.Message;
import com.sd.bugsbunny.Models.User;
import com.sd.bugsbunny.R;
import com.sd.bugsbunny.Utils.Bunny;
import com.sd.bugsbunny.Utils.Databaser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Contacts extends AppCompatActivity {

    /** The Chat list. */
    private ArrayList<User> uList;


    /** The user. */
    public static User user;

    private ArrayAdapter<String> listAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadContacts();
        listenToMessages();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Databaser.getINSTANCE().setContext(getApplicationContext());
        // Set up the login form.

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private void loadContacts()
    {
        ListView list = (ListView) findViewById(R.id.list);

        String[] contacts = new String[] { "adrianodiasx93", "ericmoura", "alexpud"};
        ArrayList<String> contactslist = new ArrayList<String>();
        contactslist.addAll( Arrays.asList(contacts) );

        listAdapter = new ArrayAdapter<String>(this, R.layout.chat_item, contactslist);

        list.setAdapter(listAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id)
            {
                String item = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(Contacts.this, Chat.class);
                intent.putExtra("username", item);
                Contacts.this.startActivity(intent);
            }

        });

    }

    private void listenToMessages(){

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                String message = msg.getData().getString("msg");
                Gson gson = new Gson();
                Message message_db = gson.fromJson(message, Message.class);
                if(message_db != null){
                    Toast.makeText(Contacts.this, message_db.getText(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(Contacts.this, "null", Toast.LENGTH_LONG).show();
                }

                Databaser.getINSTANCE().saveToDatabase(message_db);
            }
        };

        Bunny.getINSTANCE().subscribe(incomingMessageHandler);


    }



//

}
