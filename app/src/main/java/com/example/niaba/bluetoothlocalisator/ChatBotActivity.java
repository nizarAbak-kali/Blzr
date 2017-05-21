package com.example.niaba.bluetoothlocalisator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class ChatBotActivity extends AppCompatActivity {


    /*
        Example of sentences to understand:
        Où trouver [...] ?
        Où se trouve [...] ?
        Je voudrais [...]
        Je veux [...]
        Où suis-je ?
     */

    public static final String SENTENCE_TO_PARSE = "sentence.to.parse";
    private static final String CHAT_BOT = "ChatBot";
    public static final int CHATBOT_ACTIVITY_RESULT = 1;
    public static final String CHATBOT_RESULT = "chatbotResult";
    private static final int CHATBOT_ACTIVITY_RESULT_NOT_OK = 2;

    private String findFirstAmong(String str, String ...targets) {
        for (String t : targets) {
            if (str.contains(t)) {
                return t;
            }
        }
        return null;
    }

    private String findProduct(String text, ArrayList<Products> products) {

        return null; //TODO
    }

    private String answer(String query) {

        String result = null;

        String wish = findFirstAmong(query,  "trouv", "veux", "voudr");

        if (wish != null) {
            ArrayList<Products> productsArrayList = TestDataParser.getProducts(); // TODO: Fetch Nizar's stuff
            findProduct(query.substring(query.indexOf(wish)), productsArrayList);
        }

        if (query.contains("suis") && query.contains("où") && query.contains("je")) {
            result = "Vous etes sur la planète Terre."; //TODO: fetch user location
        }

        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat_bot);

        String toParse = getIntent().getStringExtra(SENTENCE_TO_PARSE);

        Log.e(CHAT_BOT, "Parsing " + toParse);

        Intent resultIntent = new Intent();
        String result = answer(toParse);

        if (result == null) {
            setResult(CHATBOT_ACTIVITY_RESULT_NOT_OK, resultIntent);
        } else {
            resultIntent.putExtra(CHATBOT_RESULT, result);
            setResult(CHATBOT_ACTIVITY_RESULT, resultIntent);
        }

        finish();
    }

}
