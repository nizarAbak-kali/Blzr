package com.example.niaba.bluetoothlocalisator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ChatBotActivity extends AppCompatActivity {


    /*
        Example of sentences to understand:
        Où trouver [...] ?
        Où se trouve [...] ?
        Où suis-je ?


     */



    public static final String SENTENCE_TO_PARSE = "sentence.to.parse";
    private static final String CHAT_BOT = "ChatBot";
    public static final int CHATBOT_ACTIVITY_RESULT = 1;
    public static final String CHATBOT_RESULT = "chatbotResult";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat_bot);

        String toParse = getIntent().getStringExtra(SENTENCE_TO_PARSE);

        Log.e(CHAT_BOT, "Parsing " + toParse);
        Toast.makeText(this, "ChatBot " + toParse, Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        resultIntent.putExtra(CHATBOT_RESULT, "Vous êtes arrivé");
        setResult(CHATBOT_ACTIVITY_RESULT, resultIntent);
        finish();
    }


}
