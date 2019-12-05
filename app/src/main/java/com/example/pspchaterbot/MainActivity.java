package com.example.pspchaterbot;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pspchaterbot.apibot.ChatterBot;
import com.example.pspchaterbot.apibot.ChatterBotFactory;
import com.example.pspchaterbot.apibot.ChatterBotSession;
import com.example.pspchaterbot.apibot.ChatterBotType;
import com.example.pspchaterbot.rest.Client;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "xyz";
    private String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36";
    private ChatterBotSession botsession = null;
    private Button btSend, btSpeech;
    private EditText etInput;
    private TextView tvOutput;
    private TextToSpeechActivity mTts;

    private String auxiliar;
    private ArrayList<String> result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        initEvents();

        initChat();

    }

    private void initEvents() {
        btSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etInput.getText().toString();
                mTts.sayHello(text);
                etInput.setText(text);
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etInput.getText().toString();
                etInput.setText("");
                new Chat().execute(text);
            }
        });
    }

    private void initComponents() {
        mTts = new TextToSpeechActivity(getApplicationContext());
        etInput = findViewById(R.id.etInput);
        tvOutput = findViewById(R.id.tvOutput);
        btSpeech = findViewById(R.id.btSpeech);
        btSend = findViewById(R.id.btSend);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==5) {
            if(resultCode==RESULT_OK && data!=null) {
                result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                new Translate().execute("es", result.get(0), "en");
                Log.v("onActivityResult", "Translate");

            }
        }
    }

    private void initChat() {
        try {
            ChatterBotFactory factory = new ChatterBotFactory();
            ChatterBot bot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
            botsession = bot.createSession();
        } catch(Exception e) {
            Log.v(TAG, e.toString());
        }
    }

    private class Chat extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... s) {
            String[] r = {s[0], chat(s[0])};
            return r;
        }

        @Override
        protected void onPostExecute(String[] response) {
            super.onPostExecute(response);
            tvOutput.append("you> " + response[0] + "\n");
            tvOutput.append("bot> " + response[1] + "\n");
            mTts.sayHello(response[1]);
        }
    }

    private String chat(String text){
        String response;
        try {
            response = botsession.think(text);
        } catch (Exception e) {
            response = e.toString();
        }
        Log.v("chat", response);
        return response;
    }

    public void traducir2(String... params){
        new Translate2().execute(params);
    }

    class Translate extends AsyncTask<String, String, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            Client client = new Client();

            HashMap<String, String> httpBodyParams;
            httpBodyParams = new HashMap<>();
            httpBodyParams.put("fromLang", strings[0]);
            httpBodyParams.put("text", strings[1]);
            httpBodyParams.put("to", strings[2]);


            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : httpBodyParams.entrySet()){
                if (first)
                    first = false;
                else
                    result.append("&");
                try {
                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                result.append("=");
                try {
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            String parameters = result.toString();



            String e = client.postHttp("https://www.bing.com/ttranslatev3?isVertical=1&&IG=9AB86C10F77B448D932E5D5DB4E982F1&IID=translator.5026.3", parameters);
            Log.v("holiii", "Respuesta: " + parameters);
            String sub = e.substring(e.indexOf("\"text\":\""), e.indexOf("\"to\":\""));
            auxiliar = sub.substring(8).replace("\"", "").replace(",", "");


            traducir2("en", chat(auxiliar), "es");

            return null;
        }

    }

    class Translate2 extends AsyncTask<String, String, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            Client client = new Client();

            HashMap<String, String> httpBodyParams;
            httpBodyParams = new HashMap<>();
            httpBodyParams.put("fromLang", strings[0]);
            httpBodyParams.put("text", strings[1]);
            httpBodyParams.put("to", strings[2]);


            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : httpBodyParams.entrySet()){
                if (first)
                    first = false;
                else
                    result.append("&");
                try {
                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                result.append("=");
                try {
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            String parameters = result.toString();


            String e = client.postHttp("https://www.bing.com/ttranslatev3?isVertical=1&&IG=9AB86C10F77B448D932E5D5DB4E982F1&IID=translator.5026.3", parameters);
            String sub = e.substring(e.indexOf("\"text\":\""), e.indexOf("\"to\":\""));
            auxiliar = sub.substring(8).replace("\"", "").replace(",", "");
            mTts.sayHello(auxiliar);

            return null;
        }

    }
}
