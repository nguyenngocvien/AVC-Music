package com.example.musicplayer.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.musicplayer.Adapter.BaihatAdapter;
import com.example.musicplayer.Model.Baihat;
import com.example.musicplayer.R;

import java.util.ArrayList;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchSong extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerViewListSearchSong;
    TextView txtNoFoundSong;
    BaihatAdapter baihatAdapter;
    SearchView editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_song);


        toolbar = findViewById(R.id.toolbarSearchSong);
        recyclerViewListSearchSong = findViewById(R.id.recyclerViewListSearchSong);
        txtNoFoundSong = findViewById(R.id.textviewNoFoundSong);
        editSearch = findViewById(R.id.edtSearchSong);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baihatAdapter.getFilter().filter("");
                finish();
            }
        });
        ArrayList<Baihat> listSongSearch = new ArrayList<>(MainActivity.arraySong);
        baihatAdapter = new BaihatAdapter(getApplicationContext(), listSongSearch);
        recyclerViewListSearchSong.setAdapter(baihatAdapter);
        recyclerViewListSearchSong.setLayoutManager(new LinearLayoutManager(this));

        editSearch.requestFocus();
        editSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                baihatAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
