package com.example.musicplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.musicplayer.Activity.MainActivity;
import com.example.musicplayer.Activity.MusicPlayActivity;
import com.example.musicplayer.Model.Baihat;
import com.example.musicplayer.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class BaihatAdapter extends RecyclerView.Adapter<BaihatAdapter.ViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Baihat> listSong;
    private ArrayList<Baihat> listSongFull;

    public BaihatAdapter(Context context, ArrayList<Baihat> listSong) {
        this.context = context;
        this.listSong = listSong;
        listSongFull = new ArrayList<>(listSong);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.line_search_song, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Baihat baihat = listSong.get(position);
        holder.txtTitle.setText(baihat.getTenbaihat());
        holder.txtSinger.setText(baihat.getCasi());
        holder.txtPosition.setText(baihat.getIdbaihat());
        Picasso.get().load(baihat.getHinhanhbaihat()).placeholder(R.drawable.music_image).error(R.drawable.music_image).into(holder.imgSong);
    }

    @Override
    public int getItemCount() {
        return listSong.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {

        //run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Baihat> filterList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filterList.addAll(listSongFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Baihat song: listSongFull) {
                    if (song.getTenbaihat().toLowerCase().contains(filterPattern)){
                        filterList.add(song);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;
            return filterResults;
        }

        //run on ui thread;
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listSong.clear();
            listSong.addAll((Collection<? extends Baihat>) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtTitle, txtSinger, txtPosition;
        CircleImageView imgSong;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.textviewSearchSongTitle);
            txtSinger = itemView.findViewById(R.id.textviewSearchSongSinger);
            imgSong = itemView.findViewById(R.id.imageViewSearchSong);
            txtPosition = itemView.findViewById(R.id.textviewSongPosition);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (MainActivity.mp3Player.isPlaying() || MainActivity.mp3Player != null){
                            MainActivity.mp3Player.stop();
                            MainActivity.mp3Player.release();
                            MainActivity.mp3Player = null;
                        }
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(context, MusicPlayActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("song", listSong.get(getPosition()));

                    Picasso.get().load(MainActivity.arraySong.get(getPosition()).getHinhanhbaihat()).placeholder(R.drawable.music_image).error(R.drawable.music_image).into(MainActivity.imageSongPlaying);
                    MainActivity.txtTitleSongPlaying.setText(MainActivity.arraySong.get(getPosition()).getTenbaihat());
                    MainActivity.txtSingerSongPlaying.setText(MainActivity.arraySong.get(getPosition()).getCasi());
                    MainActivity.imgButtonPlay.setImageResource(R.drawable.ic_pause_black_24dp);

                    MainActivity.txtTitleSongPlaying.setClickable(false);
                    MainActivity.imgButtonPlay.setClickable(false);
                    MainActivity.imgButtonNext.setClickable(false);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.txtTitleSongPlaying.setClickable(true);
                            MainActivity.imgButtonPlay.setClickable(true);
                            MainActivity.imgButtonNext.setClickable(true);
                        }
                    }, 5000);

                    context.startActivity(intent);
                }
            });
        }
    }
}
