package com.example.musicplayer.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.musicplayer.Adapter.BaihatAdapter;
import com.example.musicplayer.Model.Baihat;
import com.example.musicplayer.R;
import com.example.musicplayer.Service.APIService;
import com.example.musicplayer.Service.DataService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    LinearLayout layoutPlayRandom;
    RelativeLayout layoutPlayingSong;
    public static CircleImageView imageSongPlaying;
    RecyclerView recyclerViewListSong;
    TextView txtPlayRandom;
    public static TextView txtTitleSongPlaying, txtSingerSongPlaying;
    public static ImageButton imgButtonPlay, imgButtonNext;
    Toolbar toolbar;
    public static ArrayList<Baihat> arraySong;
    BaihatAdapter baihatAdapter;
    public static MediaPlayer mp3Player;
    public static int SongID = 0;
    public static boolean checkrandom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MappingView();
        GetData();// get songs from server
        ActionButtonPlaying();
        ActionButtonNext();
        ActionTabPlayingSong();

    }

    private void ActionTabPlayingSong() {
        txtTitleSongPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MusicPlayActivity.class);
                intent.putExtra("songplaying", true);
                startActivity(intent);
            }
        });
    }

    private void ActionButtonNext() {
        imgButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SongID ++;
                if (SongID > MainActivity.arraySong.size() - 1){
                    SongID = 0;
                }
                if (mp3Player.isPlaying() || MainActivity.mp3Player != null){
                    mp3Player.stop();
                    mp3Player.release();
                    mp3Player = null;
                }
                if (checkrandom){
                    Random random = new Random();
                    int index = random.nextInt(MainActivity.arraySong.size());
                    if (index == SongID){
                        SongID = index - 1;
                    }
                    SongID = index;
                }
                new PlayMp3().execute(arraySong.get(SongID).getLinkbaihat());
                Picasso.get().load(arraySong.get(SongID).getHinhanhbaihat()).placeholder(R.drawable.music_image).error(R.drawable.music_image).into(imageSongPlaying);
                txtTitleSongPlaying.setText(arraySong.get(SongID).getTenbaihat());
                txtSingerSongPlaying.setText(arraySong.get(SongID).getCasi());
                imgButtonPlay.setImageResource(R.drawable.ic_pause_black_24dp);
                imgButtonNext.setClickable(false);
                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imgButtonNext.setClickable(true);
                    }
                }, 5000);
            }
        });
    }

    private void ActionButtonPlaying() {
        imgButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp3Player == null){
                    new PlayMp3().execute(arraySong.get(SongID).getLinkbaihat());
                    imgButtonPlay.setImageResource(R.drawable.ic_pause_black_24dp);
                } else if (mp3Player.isPlaying()){
                    //if media play -> pause -> change play image
                    mp3Player.pause();
                    imgButtonPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                } else {
                    //if media pause -> play -> change pause image
                    mp3Player.start();
                    imgButtonPlay.setImageResource(R.drawable.ic_pause_black_24dp);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add search icon in toolbar
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //set click event of search icon
        switch (item.getItemId()){
            case R.id.action_search:
                Intent intent = new Intent(getApplicationContext(), SearchSong.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void GetData() {
        DataService dataService = APIService.getService();
        Call<List<Baihat>> callback = dataService.GetDanhSachBaiHat();
        callback.enqueue(new Callback<List<Baihat>>() {
            @Override
            public void onResponse(Call<List<Baihat>> call, Response<List<Baihat>> response) {
                arraySong.addAll(response.body());
                baihatAdapter.notifyDataSetChanged();

                //set default song playing
                Picasso.get().load(arraySong.get(SongID).getHinhanhbaihat()).placeholder(R.drawable.music_image).error(R.drawable.music_image).into(imageSongPlaying);
                txtTitleSongPlaying.setText(arraySong.get(SongID).getTenbaihat());
                txtSingerSongPlaying.setText(arraySong.get(SongID).getCasi());
            }
            @Override
            public void onFailure(Call<List<Baihat>> call, Throwable t) {

            }
        });
    }

    private void MappingView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerViewListSong = findViewById(R.id.recyclerViewListSong);
        arraySong = new ArrayList<>();
        baihatAdapter = new BaihatAdapter(getApplicationContext(), arraySong);
        recyclerViewListSong.setAdapter(baihatAdapter);
        recyclerViewListSong.setLayoutManager(new LinearLayoutManager(this));

        imageSongPlaying = findViewById(R.id.imageViewSongPlaying);
        txtTitleSongPlaying = findViewById(R.id.textviewSongTitle);
        txtSingerSongPlaying = findViewById(R.id.textviewSongSinger);
        imgButtonPlay = findViewById(R.id.imageButtonPlay);
        imgButtonNext = findViewById(R.id.imageButtonNext);
        layoutPlayingSong = findViewById(R.id.layoutPlayingSong);
    }

    public class PlayMp3 extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            return strings[0];
        }

        @Override
        protected void onPostExecute(String baihat) {
            super.onPostExecute(baihat);
            try {
                mp3Player = new MediaPlayer();
                mp3Player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp3Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp3Player.stop();
                        mp3Player.reset();
                    }
                });
                mp3Player.setDataSource(baihat);
                mp3Player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp3Player.start();
        }
    }
}
