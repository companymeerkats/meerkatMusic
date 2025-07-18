package ir.companymeerkats.myapplication.view;


import static ir.companymeerkats.myapplication.view.MainActivity.musicService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.ArrayList;

import ir.companymeerkats.myapplication.R;


public class AdapterSong extends RecyclerView.Adapter<AdapterSong.Holder> {
    private Context context;
    static ArrayList<MusicFiles> musicFiles;
    int imagePlay;
    public AdapterSong(Context context,ArrayList<MusicFiles> musicFiles) {
        this.context = context;
        this.musicFiles=musicFiles;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_song,parent,false);
        return new Holder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterSong.Holder holder, @SuppressLint("RecyclerView") int position) {
        holder.nameSong.setText(musicFiles.get(position).getTitle());
        holder.singer.setText(musicFiles.get(position).getArtist());
        int nightModeFlags =
                context.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                imagePlay=R.drawable.play_dark;
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                imagePlay=R.drawable.play_light;
                break;
        }
        Glide.with(context)
                .load(musicFiles.get(position).getArtUri())
                .apply(new RequestOptions().placeholder(imagePlay).centerCrop())
                .into(holder.musicImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,SongActivity.class);
                intent.putExtra("pos",position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView nameSong;
        TextView singer;
        ImageView musicImage;
        public Holder(@NonNull View itemView) {
            super(itemView);
            nameSong=itemView.findViewById(R.id.nameSong);
            singer=itemView.findViewById(R.id.nameSinger);
            musicImage=itemView.findViewById(R.id.musicImg);
        }
    }

    void updateList(ArrayList<MusicFiles> musicFilesArrayList){
        musicFiles=new ArrayList<>();
        musicFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}