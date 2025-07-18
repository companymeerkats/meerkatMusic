package ir.companymeerkats.myapplication.view;


import static ir.companymeerkats.myapplication.view.AlbumDetailsAdapter.albumFiles;
import static ir.companymeerkats.myapplication.view.AdapterSong.musicFiles;
import static ir.companymeerkats.myapplication.view.MainActivity.CURRENT_POSITION;
import static ir.companymeerkats.myapplication.view.MainActivity.CURRENT_POSITION_TO_FRAG;
import static ir.companymeerkats.myapplication.view.MainActivity.LIST_DATA_TO_FRAG;
import static ir.companymeerkats.myapplication.view.MainActivity.MUSIC_LAST_PLAYED;
import static ir.companymeerkats.myapplication.view.MainActivity.emptyList;
import static ir.companymeerkats.myapplication.view.MainActivity.musicService;
import static ir.companymeerkats.myapplication.view.MainActivity.repeatBoolean;
import static ir.companymeerkats.myapplication.view.MainActivity.shuffleBoolean;
import static ir.companymeerkats.myapplication.view.MusicService.opennessSongActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ir.companymeerkats.myapplication.Application;
import ir.companymeerkats.myapplication.R;
import ir.companymeerkats.myapplication.vm.ViewModelListSong;
@AndroidEntryPoint
public class SongActivity extends AppCompatActivity implements ActionPlaying{
    TextView songName,singer,currentPositionText,totalDurationText;
    ImageView imageMain,nextBtn,prevBtn,shuffleBtn,repeatBtn,imagePlay;
    SeekBar seekBar;
    ConstraintLayout constraintLayout;
    int position;
    static ArrayList<MusicFiles> listSongs=new ArrayList<>();
    private Handler handler=new Handler();
    private Runnable mUpdateSeekbar;
    int currentPosition;
    int totalDuration;
    String valueNowPlaying;

    private CompositeDisposable compositeDisposable;
    @Inject
    public ViewModelListSong viewModelListSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        compositeDisposable = new CompositeDisposable();
        initViews();
        musicService.setCallBack(this);
        valueNowPlaying = getIntent().getExtras().getString("nowPlaying");
        position=getIntent().getExtras().getInt("pos",-1);
        handler = new Handler();
        mUpdateSeekbar = new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        totalDuration = musicService.getDuration();
                            if (valueNowPlaying != null) {
                                if (musicService.runSeekbar==0){
                                currentPosition = CURRENT_POSITION_TO_FRAG;
                                seekBar.setProgress(currentPosition * 100 / totalDuration);
                                musicService.seekTo(currentPosition);
                            }else{
                                    currentPosition = musicService.getCurrentPosition();
                                    seekBar.setProgress(currentPosition * 100 / totalDuration);

                                }}
                        else {
                            currentPosition = musicService.getCurrentPosition();
                            seekBar.setProgress(currentPosition * 100 / totalDuration);
                            }
                        int mintsTotalDuration = totalDuration / 1000 / 60;
                        int secondsTotalDuration = totalDuration / 1000 - mintsTotalDuration * 60;
                        int mintsCurrentPosition = currentPosition / 1000 / 60;
                        int secondsCurrentPosition = currentPosition / 1000 - mintsCurrentPosition * 60;
                        if (secondsCurrentPosition <= 9) {
                            currentPositionText.setText(mintsCurrentPosition + ":" + "0" + secondsCurrentPosition);
                        } else {
                            currentPositionText.setText(mintsCurrentPosition + ":" + secondsCurrentPosition);
                        }
                        if (secondsTotalDuration <= 9) {
                            totalDurationText.setText(mintsTotalDuration + ":" + "0" + secondsTotalDuration);
                        } else {
                            totalDurationText.setText(mintsTotalDuration + ":" + secondsTotalDuration);
                        }
                        musicService.runSeekbar++;
                        handler.postDelayed(this, 10);

                    }
                }
            };

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int newPosition = musicService.getDuration() * progress / 100;
                    musicService.seekTo(newPosition);
                    if (musicService.isPlaying()){
                        imagePlay.setImageResource(R.drawable.baseline_pause_24);
                    }else{
                        imagePlay.setImageResource(R.drawable.baseline_play_arrow_24);
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 nextClicked(position);
            }
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevClicked(position);
            }
        });
        imagePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playClicked(position);
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffleBoolean){
                    shuffleBoolean=false;
                    shuffleBtn.setImageResource(R.drawable.no_shuffle_24);
                }else {
                    shuffleBoolean=true;
                    shuffleBtn.setImageResource(R.drawable.baseline_shuffle_24);
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatBoolean){
                    repeatBoolean=false;
                    repeatBtn.setImageResource(R.drawable.direction_start_24);
                }else{
                    repeatBoolean=true;
                    repeatBtn.setImageResource(R.drawable.baseline_repeat_24);
                }
            }
        });
        getIntentMethod(position,false);
        setShuffleBtnAndRepeatBtn();
        handler.postDelayed(mUpdateSeekbar, 10);
        if (emptyList){
            insertCustomData(listSongs);
        }else {
            delete(listSongs);
        }
//        showData(listSongs);
    }
    

    @SuppressLint("WrongViewCast")
    private void initViews(){
        songName=findViewById(R.id.nameSong);
        singer=findViewById(R.id.nameSinger);
        currentPositionText=findViewById(R.id.currentPosition);
        totalDurationText=findViewById(R.id.totalDuration);
        imageMain=findViewById(R.id.imageMain);
        nextBtn=findViewById(R.id.nextButton);
        prevBtn=findViewById(R.id.onPrev);
        shuffleBtn=findViewById(R.id.onShuffle);
        repeatBtn=findViewById(R.id.onRepeat);
        imagePlay=findViewById(R.id.playButton);
        seekBar=findViewById(R.id.seekBar);
        constraintLayout=findViewById(R.id.constraintLayout);
    }
    private  void getIntentMethod(int position,boolean nextPrev){

        String sender=getIntent().getExtras().getString("senderAlbum");
        if (sender!=null &&sender.equals("albumDetails")){
            listSongs=albumFiles;
        }else{
            listSongs=musicFiles;
        }
        if (valueNowPlaying!=null){
            listSongs=LIST_DATA_TO_FRAG;
        }
            if (listSongs!=null){
                imagePlay.setImageResource(R.drawable.baseline_pause_24);
                songName.setText(listSongs.get(position).getTitle());
                singer.setText(listSongs.get(position).getArtist());
                imageAnimation(SongActivity.this,imageMain,listSongs.get(position).getArtUri());
                palette(listSongs.get(position).getPath());
            }
        if (nextPrev){
            musicService.setMediaPlayer(position, listSongs);
        }else {
            if (valueNowPlaying != null) {
                if (musicService.mediaPlayer != null) {
                    musicService.start();
                } else {
                    musicService.setMediaPlayer(position, listSongs);
                }
            } else {
                musicService.setMediaPlayer(position, listSongs);
            }
        }
        musicService.showNotification(R.drawable.baseline_pause_24);
    }

    public void imageAnimation(Context context, ImageView imageView,Uri artUri){
        Animation animOut= AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn=AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TypedArray ta = SongActivity.this.getTheme().obtainStyledAttributes(R.styleable.ViewStyle);
                Drawable imagePlay = ta.getDrawable(R.styleable.ViewStyle_imagePlay);
                Glide.with(context)
                        .load(artUri)
                        .apply(new RequestOptions().placeholder(imagePlay))
                        .into(imageMain);
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }
    public void palette(String artUri){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(artUri);
        imageMain.setBackgroundResource(R.drawable.backgrand_image_main);
        constraintLayout.setBackgroundResource(R.drawable.backgrand_backgrand);
        byte[] art=retriever.getEmbeddedPicture();
        if (art!=null){
            Bitmap bitmap=BitmapFactory.decodeByteArray(art,0,art.length);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch=palette.getDominantSwatch();
                    if(swatch!=null){
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{swatch.getRgb(),0*00000000});
                        imageMain.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{swatch.getRgb(),swatch.getRgb()});
                        constraintLayout.setBackground(gradientDrawableBg);
                        songName.setTextColor(swatch.getBodyTextColor());
                        singer.setTextColor(swatch.getBodyTextColor());
                        currentPositionText.setTextColor(swatch.getBodyTextColor());
                        totalDurationText.setTextColor(swatch.getBodyTextColor());
                    }else {
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{
                                Color.BLACK,0*00000000});
                        imageMain.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{
                                Color.BLACK,Color.BLACK});
                        constraintLayout.setBackground(gradientDrawableBg);
                        songName.setTextColor(Color.WHITE);
                        singer.setTextColor(Color.WHITE);
                    }
                }});
        }

    }

    public void setShuffleBtnAndRepeatBtn(){
        if (shuffleBoolean){
            shuffleBtn.setImageResource(R.drawable.baseline_shuffle_24);

        }else {
            shuffleBtn.setImageResource(R.drawable.no_shuffle_24);
        }
        if (repeatBoolean){
            repeatBtn.setImageResource(R.drawable.baseline_repeat_24);
        }
        else {
            repeatBtn.setImageResource(R.drawable.direction_start_24);
        }
    }
    private void insertCustomData(ArrayList<MusicFiles> listSongs) {
        compositeDisposable.add(
                viewModelListSong.insertData(listSongs).subscribeOn(Schedulers.io()).subscribe(new Action() {
                    @Override
                    public void run() throws Throwable {
                        Log.d("TEST_TAG", "insert Completed");
                    }
                })
        );


    }
    private void delete(ArrayList<MusicFiles> arrayList){
        compositeDisposable.add(viewModelListSong.deleteData().subscribeOn(Schedulers.io()).subscribe(new Action() {
            @Override
            public void run() throws Throwable {
                Log.d("TEST_TAG", "insertPet Completed");
                insertCustomData(arrayList);
            }
        }));
    }

    @Override
    protected void onDestroy() {
        if (musicService.isPlaying()){
            musicService.showNotification(R.drawable.baseline_pause_24);
        }else{
            musicService.showNotification(R.drawable.baseline_play_arrow_24);
        }
        super.onDestroy();
    }

    @Override
    public void nextClicked(int p) {
        Log.e("@actionN","play");
        if (shuffleBoolean&&!repeatBoolean){
            position=getRandome(musicFiles.size()-1);
        }
        else if (!shuffleBoolean&&!repeatBoolean){
            position++;
        }
        Log.e("testSize",listSongs.size()+"||||"+position);
        if (listSongs.size()==position){
            if (!musicService.isPlaying()){
               imagePlay.setImageResource(R.drawable.baseline_play_arrow_24);
            }
            position--;
            Toast.makeText(musicService, R.string.endList, Toast.LENGTH_SHORT).show();
        }else {
            getIntentMethod(position, true);
        }
    }

    @Override
    public void prevClicked(int p) {
        if (!musicService.timePrev()){
            getIntentMethod(position,  true);
        }else{
        if (shuffleBoolean&&!repeatBoolean){
            position=getRandome(musicFiles.size()-1);
        }
        else if (!shuffleBoolean&&!repeatBoolean){
            position--;
        }
        Log.e("testSize",listSongs.size()+"||||"+position);
        if (0>position){
            if (!musicService.isPlaying()){
                imagePlay.setImageResource(R.drawable.baseline_play_arrow_24);
            }
            position++;
            Toast.makeText(musicService, R.string.endList, Toast.LENGTH_SHORT).show();
        }else {
            getIntentMethod(position,  true);
        }}
    }

    @Override
    public void playClicked(int position) {
        if (musicService.isPlaying()){
            musicService.pause();
            imagePlay.setImageResource(R.drawable.baseline_play_arrow_24);
            musicService.showNotification(R.drawable.baseline_play_arrow_24);
        }
        else {
            musicService.start();
            imagePlay.setImageResource(R.drawable.baseline_pause_24);
            musicService.showNotification(R.drawable.baseline_pause_24);
        }
    }

    @Override
    public void cancel() {
        musicService.pause();
    }
    private int getRandome(int i) {
        Random random=new Random();
        return random.nextInt(i+1);
    }
}