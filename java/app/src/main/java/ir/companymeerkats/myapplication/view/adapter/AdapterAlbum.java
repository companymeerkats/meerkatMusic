package ir.companymeerkats.myapplication.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import ir.companymeerkats.myapplication.R;
import ir.companymeerkats.myapplication.view.AlbumDetails;
import ir.companymeerkats.myapplication.model.MusicFiles;


public class AdapterAlbum extends RecyclerView.Adapter<AdapterAlbum.Holder> {
    private Context context;
    public List<MusicFiles> albumFiles;
    int imagePlay;
    public AdapterAlbum(Context context,List<MusicFiles> albumFiles) {
        this.context = context;
        this.albumFiles=albumFiles;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_album,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAlbum.Holder holder, @SuppressLint("RecyclerView") int position) {
        holder.nameAlbum.setText(albumFiles.get(position).getAlbum());
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
                .apply(new RequestOptions().placeholder(imagePlay))
                .into(holder.albumImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, AlbumDetails.class);
                intent.putExtra("albumName",albumFiles.get(position).getAlbum());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView nameAlbum; 
        ImageView albumImage;
        public Holder(@NonNull View itemView) {
            super(itemView);
            nameAlbum=itemView.findViewById(R.id.album_name);
            albumImage=itemView.findViewById(R.id.album_image);
        }
    }
}