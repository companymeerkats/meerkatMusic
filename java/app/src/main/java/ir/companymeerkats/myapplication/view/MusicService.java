package ir.companymeerkats.myapplication.view;

import static ir.companymeerkats.myapplication.Application.ACTION_CANCEL;
import static ir.companymeerkats.myapplication.Application.ACTION_NEXT;
import static ir.companymeerkats.myapplication.Application.ACTION_PLAY;
import static ir.companymeerkats.myapplication.Application.ACTION_PREV;
import static ir.companymeerkats.myapplication.Application.channelId2;
import static ir.companymeerkats.myapplication.view.MainActivity.CURRENT_POSITION;
import static ir.companymeerkats.myapplication.view.MainActivity.PLAY_SONG;
import static ir.companymeerkats.myapplication.view.MainActivity.SONG_PO_TO_FRAG;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSession;
import android.net.Uri;
import android.support.v4.media.session.MediaSessionCompat;

import android.os.Binder;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ir.companymeerkats.myapplication.R;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener{
    private IBinder binder=new BinderMusicService();
    private Handler handlerCurrentPosition=new Handler();
    private Runnable runnableCurrentPosition;
    String actionName;
    MediaPlayer mediaPlayer;
    int runSeekbar=0;
    int position=-1;
    Uri uri;
    static boolean opennessSongActivity;
    ActionPlaying actionPlaying;
    MediaSession mediaSession;
    public static final String MUSIC_LAST_PLAYED="LAST_PLAYED";
    public static final String MUSIC_FILE="STORED_MUSIC";
    public static final String SINGER_NAME="SINGER_NAME";
    public static final String SONG_NAME="SONG_NAME";
    public static final String SONG_PO="SONG_PO";
    static ArrayList<MusicFiles> listFiles=new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSession(this, "playAudio");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public class BinderMusicService extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        actionName=intent.getStringExtra("ActionName");
        if (actionName!=null){
            switch(actionName){
                case ACTION_PLAY:
                    setActionPlay();
                    break;
                case ACTION_NEXT:
                    setActionNext();
                    break;
                case ACTION_PREV:
                    setActionPrev();
                    break;
                case ACTION_CANCEL:
                    setActionCancel();
                    break;
            }
        }
        return START_STICKY;
    }

    public void setCallBack(ActionPlaying actionPlaying){
        this.actionPlaying=actionPlaying;
    }
    void start(){
        mediaPlayer.start();
    }
    Boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    void pause(){
        mediaPlayer.pause();
    }
    int getDuration(){
        return mediaPlayer.getDuration();
    }
    void seekTo(int position){
        mediaPlayer.seekTo(position);
    }
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }
    void setMediaPlayer(int po,ArrayList<MusicFiles> listSong){
        this.position=po;
        listFiles=listSong;
        uri=Uri.parse(listFiles.get(position).getPath());
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        else {
            create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        sleep();
        SharedPreferences.Editor editor=getSharedPreferences(MUSIC_LAST_PLAYED,
                MODE_PRIVATE).edit();
        editor.putString(MUSIC_FILE,uri.toString());
        editor.putString(SINGER_NAME,listFiles.get(position).getArtist());
        editor.putString(SONG_NAME,listFiles.get(position).getTitle());
        editor.putInt(SONG_PO,position);
        editor.apply();
        mediaPlayer.setOnCompletionListener(this);
        PLAY_SONG=true;
    }
    public String getTitle() {
        if (listFiles.size() != 0){
            return listFiles.get(position).getTitle();
    }else{
        return "null";
    }
    }
    public String getArt(){
        if (listFiles.size() != 0){
            return listFiles.get(position).getPath();
        }else{
            return "null";
        }
    }
    public String getSinger(){
        if (listFiles.size() != 0){
            return listFiles.get(position).getArtist();
        }else{
            return "null";
        }
    }

    public void create(Context context, Uri songAddress) {
        mediaPlayer=MediaPlayer.create(context, songAddress);
    }
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        actionPlaying.nextClicked(position);
    }
    public void  showNotification(int playPauseBtu){
        Intent intent= new Intent(this,SongActivity.class);
        intent.putExtra("test","");
        intent.putExtra("pos",position);
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            String packageName = topActivity.getPackageName();
            String className = topActivity.getClassName();
            if (packageName.equals("ir.companymeerkats.myapplication") && className.equals("ir.companymeerkats.myapplication.view.SongActivity")) {
                opennessSongActivity=true;

            }else{
                opennessSongActivity=false;
            }
        }
        PendingIntent contentIntent=PendingIntent.getActivity(this,
                position, intent, PendingIntent.FLAG_IMMUTABLE);
        Intent prevIntent=new Intent(this,
                NotificationReceiver.class)
                .setAction(ACTION_PREV);
        PendingIntent prevPendingIntent=PendingIntent
                .getBroadcast(this,0
                        ,prevIntent,
                        PendingIntent.FLAG_IMMUTABLE);
        Intent cancelIntent=new Intent(this,NotificationReceiver.class).setAction(ACTION_CANCEL);
        PendingIntent cancelPendingIntent=PendingIntent.getBroadcast(this
                ,0,cancelIntent
                ,PendingIntent.FLAG_IMMUTABLE);
        Intent playIntent=new Intent(this,
                NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent playPendingIntent=PendingIntent
                .getBroadcast(this,0
                        ,playIntent,
                        PendingIntent.FLAG_IMMUTABLE);
        Intent nextIntent=new Intent(this,
                NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent=PendingIntent
                .getBroadcast(this,0
                        ,nextIntent,
                        PendingIntent.FLAG_IMMUTABLE);
        byte[] picture;
        picture=getAlbumArt(listFiles.get(position).getPath());
        Bitmap thumb;
        if (picture!=null){
            thumb=BitmapFactory.decodeByteArray(picture,0,picture.length);
        }else {
            thumb=BitmapFactory.decodeResource(getResources(), R.drawable.play_dark);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId2)
                    .setSmallIcon(playPauseBtu)
                    .setLargeIcon(thumb)
                    .setContentTitle(listFiles.get(position).getTitle())
                    .setContentText(listFiles.get(position).getArtist())
                    .addAction(R.drawable.baseline_skip_previous_24, "previous", prevPendingIntent)
                    .addAction(playPauseBtu, "play", playPendingIntent)
                    .addAction(R.drawable.baseline_skip_next_24, "next", nextPendingIntent)
                    .addAction(R.drawable.baseline_cancel_24, "cancel", cancelPendingIntent)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(MediaSessionCompat.Token.fromToken(mediaSession.getSessionToken())))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(contentIntent)
                    .setSilent(true)
                    .setOngoing(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .build();
            startForeground(222, notification);
            if (opennessSongActivity){
                notification.contentIntent.cancel();
            }
        }
    }
    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever =new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        return art;
    }
    void setActionPlay(){
        if (actionPlaying!=null){
            actionPlaying.playClicked(position);
        }
    }
    void setActionNext(){
        if (actionPlaying!=null){
            actionPlaying.nextClicked(position);

        }
    }
    void setActionPrev(){
        if (actionPlaying!=null){
            actionPlaying.prevClicked(position);
        }
    }
    void setActionCancel(){
        if (actionPlaying!=null){
            actionPlaying.cancel();
            stopForeground(true);
        }
    }
    void playNowPlaying(int po ,ArrayList<MusicFiles> listSong,boolean prev){
            setMediaPlayer(po,listSong);
            showNotification(R.drawable.baseline_pause_24);
    }
    boolean timePrev(){
        int mintsCurrentPosition = getCurrentPosition() / 1000 / 60;
        int secondsCurrentPosition = getCurrentPosition() / 1000 - mintsCurrentPosition * 60;
        if (mintsCurrentPosition==0 && secondsCurrentPosition<11){
            return true;
        }else {
            return false;
        }
    }
    void sleep (){
        handlerCurrentPosition = new Handler();
        runnableCurrentPosition = new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor editor=getSharedPreferences(CURRENT_POSITION,
                        MODE_PRIVATE).edit();
                editor.putInt(CURRENT_POSITION,getCurrentPosition());
                editor.apply();

                handlerCurrentPosition.postDelayed(this, 2000);
            }
        };
        handlerCurrentPosition.postDelayed(runnableCurrentPosition, 2000);

    }
}
