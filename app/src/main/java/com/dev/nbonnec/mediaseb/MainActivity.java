package com.dev.nbonnec.mediaseb;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private final static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36";
    private TextView text;

    public String run() throws Exception {
        RequestBody formBody = new FormEncodingBuilder()
                .add("SMMaster", "SMMaster|BtRechSimple")
                .add("__EVENTTARGET", "")
                .add("__EVENTARGUMENT", "")
                .add("__VIEWSTATE", "")
                .add("TBRechLibre", "walking dead")
                .add("CtrlOpacCompte$TBCompteNom", "")
                .add("CtrlOpacCompte$TBCompteCarte", "")
                .add("TBrechLivreMobile", "")
                .add("CtrLOpacCompteMobile$TBCompteNom", "")
                .add("CtrlOpacCompteMobile$TBCompteCarte", "")
                .add("TBRechTitre", "")
                .add("TBRechAuteur", "")
                .add("TBRechSujet", "")
                .add("TBRechEditeur", "")
                .add("TBRechColl", "")
                .add("__ASYNCPOST", "true")
                .add("BtRechSimple", "")
                .add("tri", "-1")
                .add("ordre", "1")
                .build();
        Request request = new Request.Builder()
                .url("http://www.mediatheque.saintsebastien.fr/")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return response.body().string();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String answ = null;
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        client.networkInterceptors().add(new UserAgentInterceptor(USER_AGENT));

        text = (TextView) findViewById(R.id.my_text);
        text.setMovementMethod(new ScrollingMovementMethod());

        try {
            answ = this.run();
        } catch (Exception e){
            Log.e("MEDIASEB", "exception", e);
        }

        if (answ != null)
            text.setText(answ);
    }
}
