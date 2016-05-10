package com.sd.bugsbunny.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sd.bugsbunny.Models.Message;
import com.sd.bugsbunny.R;
import com.sd.bugsbunny.Utils.Bunny;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by adrianodiasx93 on 5/8/16.
 */
public class Chat extends AppCompatActivity {

    /** The Conversation list. */
    private ArrayList<Message> convList;

    /** The chat adapter. */
    private ChatAdapter adp;

    /** The Editext to compose the message. */
    private EditText txt;

    /** The user name of buddy. */
    private String buddy;

    /** The date of last message in conversation. */
    private Date lastMsgDate;

    /** Flag to hold if the activity is running or not. */
    private boolean isRunning;

    /** The handler. */
    private static Handler handler;

    private Button btnSend;

    private Toolbar informBuddy;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        convList = new ArrayList<Message>();
        ListView list = (ListView) findViewById(R.id.list);
        adp = new ChatAdapter();
        list.setAdapter(adp);
        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setStackFromBottom(true);

        txt = (EditText) findViewById(R.id.txt);
        txt.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE);


        Bundle extras = getIntent().getExtras();
        if (extras != null){
            if(extras.containsKey("username"))
                buddy = extras.getString("username");
        }

        showTheNameOfMy(buddy);


        handler = new Handler();

        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(btnSendListener);
        try {
            Bunny.getINSTANCE().startService();
        } catch (IOException e) {
            Log.e("RECEIVING", e.getMessage(), e);

        }
    }

    private void showTheNameOfMy(String buddy){
        informBuddy = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(informBuddy);
        getSupportActionBar().setTitle(buddy);
    }

    private View.OnClickListener btnSendListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            sendMessage();
        }
    };

    private void sendMessage()
    {
        if (txt.length() == 0)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txt.getWindowToken(), 0);

        String text = txt.getText().toString();
        final Message msg = new Message(text, new Date(), "adrianodiasx93", buddy);
        convList.add(msg);
        adp.notifyDataSetChanged();
        txt.setText(null);

        new SendMessageAsyncTask(msg).execute();


    }

    private class ChatAdapter extends BaseAdapter
    {

        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount()
        {
            return convList.size();
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Message getItem(int arg0)
        {
            return convList.get(arg0);
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int pos, View v, ViewGroup arg2)
        {
            Message c = getItem(pos);

            if (c.isSent())
                v = getLayoutInflater().inflate(R.layout.chat_item_sent, null);
            else
                v = getLayoutInflater().inflate(R.layout.chat_item_rcv, null);

            TextView lbl = (TextView) v.findViewById(R.id.lbl1);
            lbl.setText(DateUtils.getRelativeDateTimeString(Chat.this, c
                            .getDate().getTime(), DateUtils.SECOND_IN_MILLIS,
                    DateUtils.DAY_IN_MILLIS, 0));

            lbl = (TextView) v.findViewById(R.id.lbl2);
            lbl.setText(c.getText());

            lbl = (TextView) v.findViewById(R.id.lbl3);
            if (c.isSent())
            {
                lbl.setText("Sent");
            }
            else
                lbl.setText("");

            return v;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class SendMessageAsyncTask extends AsyncTask<Void, Void, Boolean> {

        Message message;

        public SendMessageAsyncTask(Message message){
            this.message=message;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Bunny.getINSTANCE().send(message);
            return true;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(!success){
                Toast.makeText(Chat.this, "Ops, tivemos um problema.", Toast.LENGTH_LONG).show();
                //TODO react to fail
            }
        }

    }

}
