package com.example.niaba.bluetoothlocalisator;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    public static final int REQUEST_CODE_RECOGNITION_RESULT = 42;
    public static final int REQUEST_CODE_PARSING_RESULT = 51;
    private static final String UTTERANCE_ID_CHATBOT = "utterance_id_chatbot";
    public static final String MAIN_ACTIVITY = "MainActivity";

    private TextToSpeech TTS;

    Button localize_button;
    Button talk_me_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        localize_button = (Button) findViewById(R.id.button);
        talk_me_button = (Button) findViewById(R.id.button2);

        if (localize_button == null|| talk_me_button == null){
            Log.e(MAIN_ACTIVITY, "bouton pas init");
        }

        localize_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, BluetoothLocalizationActivity.class);
                startActivity(i);
            }
        });

        talk_me_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent(MainActivity.this, RecognitionListenerActivity.class);
                startActivityForResult(resultIntent, REQUEST_CODE_RECOGNITION_RESULT);
            }
        });

        TTS = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.e(MAIN_ACTIVITY, "TTS onInit");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case REQUEST_CODE_RECOGNITION_RESULT:

                if(resultCode == RecognitionListenerActivity.RECOGNIZER_ACTIVITY_RESULT_NOT_OK) {
                    return;
                }

                String recognizerResult = data.getStringExtra(RecognitionListenerActivity.RECOGNIZER_RESULT);

                if(recognizerResult == null) {
                    return;
                }

                Intent parseIntent = new Intent(MainActivity.this, ChatBotActivity.class);
                parseIntent.putExtra(ChatBotActivity.SENTENCE_TO_PARSE, recognizerResult);

                startActivityForResult(parseIntent, REQUEST_CODE_PARSING_RESULT);
                break;

            case REQUEST_CODE_PARSING_RESULT:
                String resultChatBot = data.getStringExtra(ChatBotActivity.CHATBOT_RESULT);
                Log.e(MAIN_ACTIVITY, "chat bot speaking");
                TTS.speak(resultChatBot, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID_CHATBOT);

                break;

            default:
                Log.e(MAIN_ACTIVITY, "unknown request code");
                break;
        }

    }
}

