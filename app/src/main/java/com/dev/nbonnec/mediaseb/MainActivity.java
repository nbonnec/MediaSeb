package com.dev.nbonnec.mediaseb;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private final static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36";
    private ListView list;
    private Document doc = null;
    private Elements titles;
    private List<String> list_title = new ArrayList<String>();

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

        list = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_title);

        try {
            doc = Jsoup.parse(this.run());
        } catch (Exception e){
            Log.e("MEDIASEB", "exception", e);
        }

        if (doc != null) {
            titles = doc.select("div[class=\"fll span8\"] > a[title]");
            for (Element element : titles)
                list_title.add(element.ownText());
            list.setAdapter(adapter);
        }
    }
}
