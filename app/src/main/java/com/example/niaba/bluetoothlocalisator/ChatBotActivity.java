package com.example.niaba.bluetoothlocalisator;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
    public static final String X_COORD = "x.coord";
    public static final String Y_COORD = "y.coord";

    private static final String CHAT_BOT = "ChatBot";
    public static final int CHATBOT_ACTIVITY_RESULT = 1;
    public static final String CHATBOT_RESULT = "chatbotResult";
    private static final int CHATBOT_ACTIVITY_RESULT_NOT_OK = 2;

    PathFinder pathFinder;

    private String findFirstAmong(String str, String... targets) {
        for (String t : targets) {
            if (str.contains(t)) {
                return t;
            }
        }
        return null;
    }

    private Products findProduct(String text, ArrayList<Products> products) {
        for (Products prod : products) {
            if (prod.getHYP_GRP_CLASS_DESC().contains(text.toUpperCase())) {
                return prod;
            }
        }

        return null;
    }

    private String answer(String query) {

        String result = null;

        String wish = findFirstAmong(query, "trouv", "veux", "voudr");
        //query = query.toLowerCase();
        Products produit = null;

        if (wish != null) {
            String[] splittedWish = query.split(" ");

            for (String s : splittedWish) {
                produit = findProduct(s, TestDataParser.getProducts());
                if (produit != null) {
                    Log.e("answer", "produit found");
                    break;
                }
            }
        } else if (query.contains("suis") && query.contains("où") && query.contains("je")) {
            result = "Vous etes sur la planète Terre.";
        } else if (query.contains("skype") || query.contains("Skype")) {
            result = "ouverture de Skype";

            String number = "+33782547960";
            Intent skypeIntent = new Intent("android.intent.action.VIEW");
            skypeIntent.setComponent(new ComponentName("com.skype.raider", "com.skype.raider.Main"));
            String skypeId = "Le Gourrierec Geoffrey";
            skypeIntent.setData(Uri.parse("skype:" + number + "?call"));
            startActivity(skypeIntent);
        } else if (query.contains("visiophonie")) {
            result = "lancement de l'appel visio";
            //String number = "+33782547960";
            String number = "+33670477428";
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.putExtra("videocall", true);
            intent.setData(Uri.parse("tel:" + number));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            } else {
                startActivity(intent);
            }

        } else {
            return "Désolé nous ne pouvons répondre a votre attente";
        }

        if (produit == null) {
            return "Nous n'avons pu trouver le produit désiré";
        } else {
/*
            Log.e("ChatBotActivity", "starting path finder");
            int x_coord = 1;
            getIntent().getIntExtra(X_COORD, x_coord);

            int y_coord = 1;
            getIntent().getIntExtra(Y_COORD, y_coord);

            Log.e("ChatBotActivity", "X=" + x_coord + ",Y=" + y_coord + "\nA=" + produit.getABSCISSA() + ",B=" + produit.getORDINATE());

            pathFinder = new PathFinder();
            pathFinder.ent.setGoal(produit.getABSCISSA(), produit.getORDINATE());
            pathFinder.ent.setStart(x_coord, y_coord);

            ArrayList<Cell> solution = pathFinder.algoAstar(pathFinder.ent.getStart(), pathFinder.ent.getGoal());
            for (Cell i : solution)
                Log.e("ChatBotActivity ", i.getX() + " ; " + i.getY());*/


            //TODO Build string for direction
            result = "C'est tout droit";

            int  rand_choice = (int) (Math.random()*6) +1 ;
            switch (rand_choice){
                case 1:
                    result = "tout droit juste en face";
                    break;
                case 2:
                    result = "faites demi tour";
                    break;
                case 3:
                    result = "tourné à gauche";
                    break;
                case 4:
                    result = "tourné à droite";
                    break;

                case 5:
                    result = "vous êtes arrivé";
                    break;

                case 6:
                    result = "prenez la troisième à gauche";
                    break;

                case 7:
                    result = "prenez la troisième allée à droite";
                    break;
            }


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
