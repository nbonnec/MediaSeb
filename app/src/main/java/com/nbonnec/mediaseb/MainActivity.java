package com.nbonnec.mediaseb;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nbonnec.mediaseb.models.MediaList;
import com.nbonnec.mediaseb.network.Finder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<MediaList> list_title = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Document doc = null;
        ListView list;
        Elements titles;
        Finder finder = new Finder();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            list = (ListView) findViewById(R.id.list);
            ArrayAdapter<MediaList> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list_title);

            try {
                doc = Jsoup.parse(finder.easySearch(query));
            } catch (Exception e){
                Log.e("MEDIASEB", "exception", e);
            }

            if (doc != null) {
                titles = doc.select("div[class=\"fll span8\"] > a[title]");
                for (Element element : titles) {
                    list_title.add(new MediaList(element.ownText()));
                }

                list.setAdapter(adapter);
            }
        }
    }

}
