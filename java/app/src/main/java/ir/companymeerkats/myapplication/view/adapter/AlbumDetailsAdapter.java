package ir.companymeerkats.myapplication.view.adapter;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import ir.companymeerkats.myapplication.R;
import ir.companymeerkats.myapplication.model.MusicFiles;
import ir.companymeerkats.myapplication.view.SongActivity;


public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.Holder> {
    private Context context;
    public static ArrayList<MusicFiles> albumFiles;
    int imagePlay;
    public AlbumDetailsAdapter(Context context,ArrayList<MusicFiles> albumFiles) {
        this.context = context;
        this.albumFiles=albumFiles;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_song,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumDetailsAdapter.Holder holder, int position) {
        holder.nameSong.setText(albumFiles.get(position).getTitle());
        holder.singer.setText(albumFiles.get(position).getArtist());
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
                .load(albumFiles.get(position).getArtUri())
                .apply(new RequestOptions().placeholder(imagePlay).centerCrop()).circleCrop()
                .into(holder.musicImage);
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, SongActivity.class);
                intent.putExtra("senderAlbum","albumDetails");
                intent.putExtra("pos",position);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView nameSong;
        TextView singer;
        ImageView musicImage;
        ConstraintLayout constraintLayout;
        public Holder(@NonNull View itemView) {
            super(itemView);
            nameSong=itemView.findViewById(R.id.nameSong);
            singer=itemView.findViewById(R.id.nameSinger);
            musicImage=itemView.findViewById(R.id.musicImg);
            constraintLayout=itemView.findViewById(R.id.gridVS);
        }
    }
}