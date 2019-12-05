package com.example.pspchaterbot;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TextToSpeechActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech mTts;

    public TextToSpeechActivity(Context context){
        mTts = new TextToSpeech(context,
                this  // TextToSpeech.OnInitListener
        );
    }

    public void onInit(int status) {
        int result = mTts.setLanguage(Locale.getDefault());
        if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
        } else {
            sayHello("ERROR");
        }
    }

    public void sayHello(String hello) {
        mTts.speak(hello,
                TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                null);
    }

    public void stop(){
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
    }
}
