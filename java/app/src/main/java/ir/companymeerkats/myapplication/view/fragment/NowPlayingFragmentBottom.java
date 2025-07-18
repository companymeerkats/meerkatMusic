package ir.companymeerkats.myapplication.view.fragment;

import static ir.companymeerkats.myapplication.view.MainActivity.CURRENT_POSITION_TO_FRAG;
import static ir.companymeerkats.myapplication.view.MainActivity.LIST_DATA_TO_FRAG;
import static ir.companymeerkats.myapplication.view.MainActivity.PATH_TO_FRAG;
import static ir.companymeerkats.myapplication.view.MainActivity.PLAY_SONG;
import static ir.companymeerkats.myapplication.view.MainActivity.SHOW_MINI_PLAYER;
import static ir.companymeerkats.myapplication.view.MainActivity.SINGER_TO_FRAG;
import static ir.companymeerkats.myapplication.view.MainActivity.SONG_NAME_TO_FRAG;
import static ir.companymeerkats.myapplication.view.MainActivity.SONG_PO_TO_FRAG;
import static ir.companymeerkats.myapplication.view.MainActivity.context;
import static ir.companymeerkats.myapplication.view.MainActivity.musicService;
import static ir.companymeerkats.myapplication.view.service.MusicService.listFiles;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ir.companymeerkats.myapplication.R;
import ir.companymeerkats.myapplication.view.SongActivity;


public class NowPlayingFragmentBottom extends Fragment  {
    ImageView musicImg;
    TextView nameSong,singer;
    FloatingActionButton playBtn;
    int position=-1;
    int imagePlay;
    private Handler handlerSetItem=new Handler();
    private Runnable runnableSetItem;
    boolean isPlaying;
    int firstWorkNowPlaying=0;
    public NowPlayingFragmentBottom() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_now_playing_bottom, container, false);
        musicImg=view.findViewById(R.id.musicImgMiniPlayer);
        nameSong=view.findViewById(R.id.nameSongMiniPlayer);
        singer=view.findViewById(R.id.nameSingerMiniPlayer);
        playBtn=view.findViewById(R.id.playButtonMiniPlayer);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position!=-1){
                    Intent intent=new Intent(getContext(), SongActivity.class);
                    intent.putExtra("nowPlaying","");
                    intent.putExtra("pos",position);
                    getContext().startActivity(intent);
                }else {
                    Toast.makeText(context, R.string.NoSongSelected, Toast.LENGTH_SHORT).show();
                }

            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position!=-1){
                if (musicService.mediaPlayer !=null){
                    musicService.setActionPlay();
                }else {
                    musicService.setMediaPlayer(position,LIST_DATA_TO_FRAG);
                    musicService.showNotification(R.drawable.baseline_pause_24);
                    musicService.seekTo(CURRENT_POSITION_TO_FRAG);
                }
                if (musicService.isPlaying()){
                    playBtn.setImageResource(R.drawable.baseline_pause_24);
                }else{
                    playBtn.setImageResource(R.drawable.baseline_play_arrow_24);
                }
            }else{
                    Toast.makeText(context, R.string.NoSongSelected, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SHOW_MINI_PLAYER){
            position=SONG_PO_TO_FRAG;
            if (PATH_TO_FRAG != null) {
                byte[] art=getAlbumArt(PATH_TO_FRAG);
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
                if (art != null){
                    Glide.with(getContext()).load(art).circleCrop().into(musicImg);
                }else {
                    Glide.with(getContext()).load(imagePlay).circleCrop().into(musicImg);
                }
                nameSong.setText(SONG_NAME_TO_FRAG);
                singer.setText(SINGER_TO_FRAG);
                if (PLAY_SONG){
                if (musicService.isPlaying()){
                    isPlaying=true;
                    playBtn.setImageResource(R.drawable.baseline_pause_24);
                }else {
                    isPlaying=false;
                    playBtn.setImageResource(R.drawable.baseline_play_arrow_24);
                }}
            }
        }
        sleep();
    }
    public void setItem(){
        byte[] art=getAlbumArt(musicService.getArt());
        if (art != null){
            Glide.with(context).load(art).circleCrop().into(musicImg);
        }else {
            Glide.with(context).load(imagePlay).circleCrop().into(musicImg);
        }
        nameSong.setText(musicService.getTitle());
        singer.setText(musicService.getSinger());
        if (PLAY_SONG){
            if (musicService.isPlaying()){
                playBtn.setImageResource(R.drawable.baseline_pause_24);
            }else {
                playBtn.setImageResource(R.drawable.baseline_play_arrow_24);
            }}
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever =new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        return art;
    }

    void sleep (){
        handlerSetItem = new Handler();
        runnableSetItem = new Runnable() {
            @Override
            public void run() {
                if (firstWorkNowPlaying==0){
                    firstWorkNowPlaying++;
                }else {
                if (musicService!=null){
                if (listFiles.size()!=0){
                if (position!=musicService.position){
                    position=musicService.position;
                    setItem();
                }
                if (isPlaying==musicService.isPlaying()){
                    if (PLAY_SONG){
                        if (musicService.isPlaying()){
                            isPlaying=true;
                            playBtn.setImageResource(R.drawable.baseline_pause_24);
                        }else {
                            isPlaying=false;
                            playBtn.setImageResource(R.drawable.baseline_play_arrow_24);
                        }}
                }
                }}}
                handlerSetItem.postDelayed(this, 2500);
            }
        };
        handlerSetItem.postDelayed(runnableSetItem, 2500);

    }
}
