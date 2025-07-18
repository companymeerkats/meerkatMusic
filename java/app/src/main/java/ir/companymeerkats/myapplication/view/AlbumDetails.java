package ir.companymeerkats.myapplication.view;

import static ir.companymeerkats.myapplication.view.MainActivity.musicFiles;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import ir.companymeerkats.myapplication.R;
import ir.companymeerkats.myapplication.model.MusicFiles;
import ir.companymeerkats.myapplication.view.adapter.AlbumDetailsAdapter;

public class AlbumDetails extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView albumPhoto;
    String albumName;
    ArrayList<MusicFiles> albumSongs=new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;
    int imagePlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView=findViewById(R.id.recyclerView);
        albumPhoto=findViewById(R.id.album_photo);
        albumName=getIntent().getExtras().getString("albumName");
        int j=0;
        for (int i=0;i<musicFiles.size();i++){
            if (albumName.equals(musicFiles.get(i).getAlbum())){
            albumSongs.add(j,musicFiles.get(i));
            j++;
        }}
        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                imagePlay=R.drawable.play_dark;
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                imagePlay=R.drawable.play_light;
                break;
        }
        Glide.with(this)
                .load(albumSongs.get(0).getArtUri())
                .apply(new RequestOptions().placeholder(imagePlay))
                .into(albumPhoto);
        if (musicFiles.size() > 0) {
            albumDetailsAdapter=new AlbumDetailsAdapter(this,albumSongs);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
            recyclerView.setAdapter(albumDetailsAdapter);
    }


    }
}