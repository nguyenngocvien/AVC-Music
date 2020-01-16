package com.example.musicplayer.Activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.musicplayer.Model.Baihat;
import com.example.musicplayer.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Random;

public class MusicPlayActivity extends AppCompatActivity {

    TextView txtTimeSong, txtTimeTotal, txtTitle, txtSinger;
    SeekBar skSong;
    ImageView imageDisc;
    ImageButton btnPrev, btnPlay, btnNext, btnShowList, btnRandom;
    Toolbar toolbar;

    Animation animation;
    boolean next = false;
    Baihat baihat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Anhxa();
        ActionToolbar();

        baihat = (Baihat) getIntent().getSerializableExtra("song");
        if (baihat != null){
            MainActivity.SongID = Integer.parseInt(baihat.getIdbaihat()) - 1;
            new Mp3().execute(baihat.getLinkbaihat());
            LoadSongUI(baihat.getTenbaihat(),baihat.getCasi(),baihat.getHinhanhbaihat());
            imageDisc.startAnimation(animation);
            btnPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
        }
        if (getIntent().getExtras().getBoolean("songplaying")){
            String title = MainActivity.arraySong.get(MainActivity.SongID).getTenbaihat();
            String singer = MainActivity.arraySong.get(MainActivity.SongID).getCasi();
            String image = MainActivity.arraySong.get(MainActivity.SongID).getHinhanhbaihat();
            LoadSongUI(title, singer, image);
            if(MainActivity.mp3Player != null) {
                if (MainActivity.mp3Player.isPlaying()) {
                    imageDisc.startAnimation(animation);
                    btnPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                } else {
                    imageDisc.clearAnimation();
                    btnPlay.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                }
                SetTimeTotal();
                UpdateTimeSong();
            } else {
                try {
                    MainActivity.mp3Player = new MediaPlayer();
                    MainActivity.mp3Player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    MainActivity.mp3Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            MainActivity.mp3Player.stop();
                            MainActivity.mp3Player.reset();
                        }
                    });
                    MainActivity.mp3Player.setDataSource(MainActivity.arraySong.get(MainActivity.SongID).getLinkbaihat());
                    MainActivity.mp3Player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SetTimeTotal();
                UpdateTimeSong();
            }
        }
        if (getIntent().getExtras().getBoolean("playrandom")) {
            Random random = new Random();
            int index = random.nextInt(MainActivity.arraySong.size()-1);
            if (index == MainActivity.SongID){
                MainActivity.SongID = index + 5;
            } else {
                MainActivity.SongID = index;
            }

            new Mp3().execute(MainActivity.arraySong.get(MainActivity.SongID).getLinkbaihat());

            String title = MainActivity.arraySong.get(MainActivity.SongID).getTenbaihat();
            String singer = MainActivity.arraySong.get(MainActivity.SongID).getCasi();
            String image = MainActivity.arraySong.get(MainActivity.SongID).getHinhanhbaihat();
            LoadSongUI(title, singer, image);

            imageDisc.startAnimation(animation);
            btnPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
        }

        if (MainActivity.checkrandom){
            btnRandom.setImageResource(R.drawable.ic_shuffle);
        } else {
            btnRandom.setImageResource(R.drawable.ic_repeat);
        }

        ActionSeekBar();
        ActionButtonPlay();
        ActionButtonPrevious();
        ActionButtonNext();
        ActionRandomSong();
    }

    private void ActionRandomSong() {
        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainActivity.checkrandom) {
                    btnRandom.setImageResource(R.drawable.ic_repeat);
                    MainActivity.checkrandom = true;
                } else {
                    btnRandom.setImageResource(R.drawable.ic_shuffle);
                    MainActivity.checkrandom = false;
                }
            }
        });
    }

    private void ActionSeekBar() {
        skSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //cập nhật giá trị liên tục khi kéo
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //cập nhật giá trị khi chạm vào

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //cập nhật giá trị khi thả tay ra
                MainActivity.mp3Player.seekTo(skSong.getProgress());
            }
        });
    }

    private void ActionButtonPlay() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.mp3Player.isPlaying()){
                    //nếu đang phát -> pause -> đổi hình play
                    MainActivity.mp3Player.pause();
                    btnPlay.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                    imageDisc.clearAnimation();

                } else {
                    //đang ngừng -> phát -> đổi hình pause
                    MainActivity.mp3Player.start();
                    btnPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                    imageDisc.startAnimation(animation);
                }
                UpdateTimeSong();
            }
        });
    }

    private void ActionButtonPrevious() {
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.SongID--;
                if (MainActivity.SongID < 0) {
                    MainActivity.SongID = MainActivity.arraySong.size() -1;
                }
                if (MainActivity.mp3Player.isPlaying() || MainActivity.mp3Player != null) {
                    MainActivity.mp3Player.stop();
                    MainActivity.mp3Player.release();
                    MainActivity.mp3Player = null;
                }
                //play mp3
                new Mp3().execute(MainActivity.arraySong.get(MainActivity.SongID).getLinkbaihat());
                String title = MainActivity.arraySong.get(MainActivity.SongID).getTenbaihat();
                String singer = MainActivity.arraySong.get(MainActivity.SongID).getCasi();
                String image = MainActivity.arraySong.get(MainActivity.SongID).getHinhanhbaihat();
                LoadSongUI(title, singer, image);
                btnPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                UpdateTimeSong();
                imageDisc.startAnimation(animation);

                btnNext.setClickable(false);
                btnPrev.setClickable(false);
                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnNext.setClickable(true);
                        btnPrev.setClickable(true);
                    }
                }, 5000);
            }
        });
    }
     private void ActionButtonNext(){
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.SongID ++;
                if (MainActivity.SongID > MainActivity.arraySong.size()-1){
                    MainActivity.SongID = 0;
                }

                if (MainActivity.mp3Player.isPlaying() || MainActivity.mp3Player != null){
                    MainActivity.mp3Player.stop();
                    MainActivity.mp3Player.release();
                    MainActivity.mp3Player = null;
                }
                if (MainActivity.checkrandom){
                    Random random = new Random();
                    int index = random.nextInt(MainActivity.arraySong.size());
                    if (index == MainActivity.SongID){
                        MainActivity.SongID = index - 1;
                    }
                    MainActivity.SongID = index;
                }
                String title = MainActivity.arraySong.get(MainActivity.SongID).getTenbaihat();
                String singer = MainActivity.arraySong.get(MainActivity.SongID).getCasi();
                String image = MainActivity.arraySong.get(MainActivity.SongID).getHinhanhbaihat();
                LoadSongUI(title, singer, image);
                new Mp3().execute(MainActivity.arraySong.get(MainActivity.SongID).getLinkbaihat());
                btnPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                imageDisc.startAnimation(animation);

                btnNext.setClickable(false);
                btnPrev.setClickable(false);
                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnNext.setClickable(true);
                        btnPrev.setClickable(true);
                    }
                }, 5000);
            }
        });
    }

    private void UpdateTimeSong(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(MainActivity.mp3Player != null){
                    //update progress skSong
                    skSong.setProgress(MainActivity.mp3Player.getCurrentPosition());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                    txtTimeSong.setText(simpleDateFormat.format(MainActivity.mp3Player.getCurrentPosition()));

                    // kiểm tra thời gian bài hát -> nếu kết thúc -> Next
                    MainActivity.mp3Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            next = true;
                            try {
                                Thread.sleep(1000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    });
                    handler.postDelayed(this, 300); // gọi lặp lại để cập nhật timeSong liên tục
                }
            }
        }, 300);
        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (next){
                    MainActivity.SongID ++;
                    if (MainActivity.SongID > MainActivity.arraySong.size()-1){
                        MainActivity.SongID = 0;
                    }
                    if (MainActivity.checkrandom){
                        Random random = new Random();
                        int index = random.nextInt(MainActivity.arraySong.size());
                        if (index == MainActivity.SongID){
                            MainActivity.SongID = index - 1;
                        }
                        MainActivity.SongID = index;
                    }
                    if (MainActivity.mp3Player.isPlaying() || MainActivity.mp3Player != null){
                        MainActivity.mp3Player.stop();
                        MainActivity.mp3Player.release();
                        MainActivity.mp3Player = null;
                    }

                    String title = MainActivity.arraySong.get(MainActivity.SongID).getTenbaihat();
                    String singer = MainActivity.arraySong.get(MainActivity.SongID).getCasi();
                    String image = MainActivity.arraySong.get(MainActivity.SongID).getHinhanhbaihat();
                    LoadSongUI(title, singer, image);
                    new Mp3().execute(MainActivity.arraySong.get(MainActivity.SongID).getLinkbaihat());
                    btnPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                    imageDisc.startAnimation(animation);

                    btnNext.setClickable(false);
                    btnPrev.setClickable(false);
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnNext.setClickable(true);
                            btnPrev.setClickable(true);
                        }
                    }, 5000);
                    next = false;
                    handler1.removeCallbacks(this);
                }else {
                    handler1.postDelayed(this, 1000);
                }
            }
        },1000);
    }

    private void SetTimeTotal(){
        if (MainActivity.mp3Player != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            txtTimeTotal.setText(simpleDateFormat.format(MainActivity.mp3Player.getDuration()));
            //gán max của skSong = mediaPlayer.Duration();
            skSong.setMax(MainActivity.mp3Player.getDuration());
        }
    }

    private void LoadSongUI(String title, String singer, String image){
        txtTitle.setText(title);
        txtSinger.setText(singer);
        Picasso.get().load(image).placeholder(R.drawable.music_image).error(R.drawable.music_image).into(imageDisc);
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = MainActivity.arraySong.get(MainActivity.SongID).getTenbaihat();
                String singer = MainActivity.arraySong.get(MainActivity.SongID).getCasi();
                String image = MainActivity.arraySong.get(MainActivity.SongID).getHinhanhbaihat();
                Picasso.get().load(image).placeholder(R.drawable.music_image).error(R.drawable.music_image).into(MainActivity.imageSongPlaying);
                MainActivity.txtTitleSongPlaying.setText(title);
                MainActivity.txtSingerSongPlaying.setText(singer);
                if (MainActivity.mp3Player.isPlaying()){
                    MainActivity.imgButtonPlay.setImageResource(R.drawable.ic_pause_black_24dp);
                } else {
                    MainActivity.imgButtonPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
                finish();
            }
        });
    }

    private void Anhxa() {
        toolbar = findViewById(R.id.toolbar);
        txtTitle = findViewById((R.id.textviewSongTitle));
        txtTitle.setSelected(true);
        txtSinger = findViewById(R.id.textviewSinger);
        txtTimeSong = findViewById(R.id.textviewTimeSong);
        txtTimeTotal = findViewById(R.id.textviewTimeTotal);
        skSong = findViewById(R.id.seekbarSong);
        btnPrev = findViewById(R.id.imageButtonPrev);
        btnPlay = findViewById(R.id.imageButtonPlay);
        btnNext = findViewById(R.id.imageButtonNext);
        btnRandom = findViewById(R.id.imageButtonRandom);
        btnShowList = findViewById(R.id.imageButtonShowListSong);
        imageDisc = findViewById(R.id.imageviewDisc);
        animation = AnimationUtils.loadAnimation(this, R.anim.disc_rotate);
    }

    public class Mp3 extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            return strings[0];
        }

        @Override
        protected void onPostExecute(String baihat) {
            super.onPostExecute(baihat);
            try {
            MainActivity.mp3Player = new MediaPlayer();
            MainActivity.mp3Player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            MainActivity.mp3Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    MainActivity.mp3Player.stop();
                    MainActivity.mp3Player.reset();
                    btnPlay.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                }
            });
            MainActivity.mp3Player.setDataSource(baihat);
            MainActivity.mp3Player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MainActivity.mp3Player.start();
            SetTimeTotal();
            UpdateTimeSong();
        }
    }
}
