package com.chamud.cheziandsima.translatorjson;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity extends ActionBarActivity {

    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void TranslateTheText(View view) {

        if (isEmpty()) {
            Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please wait", Toast.LENGTH_LONG).show();
            TextView tv = (TextView)findViewById(R.id.TranslateTextArea);
            result="";
            tv.setText("");
            Translate translate = new Translate();
            translate.execute();
        }
    }

    private boolean isEmpty() {
        return ((EditText) findViewById(R.id.translateEditText)).getText().toString().trim().length() == 0;
    }


    class Translate extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            TextView tv = (TextView)findViewById(R.id.TranslateTextArea);
            tv.setText(result);
            tv.setText(result);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String BaseURL = "http://newjustin.com/translateit.php?action=translations&english_words=";
            String QueryString = ((EditText) findViewById(R.id.translateEditText)).getText().toString().replace(" ", "+");
            String Json = getJSON(BaseURL + QueryString, 5000);

            // Create a JSONObject by passing the JSON data
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(Json);
                JSONArray jArray = jObject.getJSONArray("translations");

                for(int i = 0; i < jArray.length(); i++){

                    JSONObject translationObject =
                            jArray.getJSONObject(i);

                    String key = translationObject.keys().next();

                    result = result + key + " : " +
                            translationObject.getString(key) + "\n";

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Get the Array named translations that contains all the translations


            return null;
        }

        public String getJSON(String url, int timeout) {
            HttpURLConnection c = null;
            try {
                URL u = new URL(url);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("Content-length", "0");
                c.setRequestProperty("Content-Type", "text/json; charset=utf-8");
                c.setUseCaches(false);
                c.setAllowUserInteraction(false);
                c.setConnectTimeout(timeout);
                c.setReadTimeout(timeout);
                c.connect();
                int status = c.getResponseCode();

                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        return sb.toString();
                }

            } catch (MalformedURLException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
//            } catch (ProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
            } finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return null;
        }
    }
}
