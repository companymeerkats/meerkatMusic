package ir.companymeerkats.myapplication.view;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ir.companymeerkats.myapplication.R;
import ir.companymeerkats.myapplication.vm.ViewModelListSong;
import io.reactivex.rxjava3.functions.Consumer;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, ServiceConnection,ActionPlaying {
    ViewPager viewPager;
    TabLayout tabLayout;
    public static  Context context ;
    FrameLayout frameLayout;
    public static final int REQUEST_CODE=1;
    static ArrayList <MusicFiles> musicFiles;
    static ArrayList<MusicFiles> albums=new ArrayList<>();
    static boolean shuffleBoolean=false,repeatBoolean=false;
    static Uri uri;
    public static String SORT_PREF="sortOrder";
    public static final String MUSIC_LAST_PLAYED="LAST_PLAYED";
    public static final String MUSIC_FILE="STORED_MUSIC";
    public static boolean SHOW_MINI_PLAYER=false;
    public static String PATH_TO_FRAG=null;
    public static String SINGER_TO_FRAG=null;
    public static String SONG_NAME_TO_FRAG=null;
    public static int SONG_PO_TO_FRAG=0;
    public static final String SINGER_NAME="SINGER_NAME";
    public static final String SONG_NAME="SONG_NAME";
    public static final String SONG_PO="SONG_PO";
    public static ArrayList<MusicFiles> LIST_DATA_TO_FRAG=null;
    public static boolean PLAY_SONG=false;
    public static final String CURRENT_POSITION="CURRENT_POSITION";
    public static int CURRENT_POSITION_TO_FRAG=0;

    static MusicService musicService;
    private static CompositeDisposable compositeDisposable;
    @Inject
    public ViewModelListSong viewModelListSong;
    public static boolean emptyList=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=MainActivity.this;
        compositeDisposable = new CompositeDisposable();
        setData();
        Intent intent = new Intent(this, MusicService.class);
                bindService(intent, this, BIND_ABOVE_CLIENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    startForegroundService(intent);
                    startService(intent);
                }
                else {
                    startService(intent);
                }
        permission();

    }
    private void initViews(){
        viewPager=findViewById(R.id.viewPager);
        tabLayout=findViewById(R.id.tabLayout);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongsFragment(),"Songs");
        viewPagerAdapter.addFragments(new AlbumFragment(),"Album");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        frameLayout=findViewById(R.id.frag_bottom_player);
    }
    private void permission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }else {
            musicFiles=getAllAudio(this);
            initViews();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_CODE){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                musicFiles=getAllAudio(this);
                initViews();
            }else {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

            }
        }
    }
    public static class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment>fragments;
        private ArrayList<String>titles;
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }
        public void addFragments(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
    public static ArrayList<MusicFiles> getAllAudio(Context context){
        SharedPreferences preferences= context.getSharedPreferences(SORT_PREF,MODE_PRIVATE);
        String sortOrder=preferences.getString("sorting","sortByName");
        String order=null;
        if (sortOrder=="sortByName"){
            order=MediaStore.MediaColumns.DISPLAY_NAME+" ASC";
        } else if (sortOrder == "sortByDate") {
            order=MediaStore.MediaColumns.DATE_ADDED+" ASC";
        }else if (sortOrder== "sortBySize"){
            order=MediaStore.MediaColumns.SIZE+" DESC";
        }
        ArrayList<String> duplicate=new ArrayList<>();
        albums.clear();
        ArrayList<MusicFiles> tempAudioList=new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri=MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        }else {
            uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        String[]projection={
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,//path
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID
        };
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,order);
        if (cursor!=null){
            while (cursor.moveToNext()){
                String album=cursor.getString(0);
                String title=cursor.getString(1);
                String duration=cursor.getString(2);
                String path=cursor.getString(3);
                String artist=cursor.getString(4);
                long albumId=cursor.getLong(5);
//                long album_id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                Uri imageUri = Uri.withAppendedPath(sArtworkUri, String.valueOf(albumId));
//                ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),albumId);
                MusicFiles musicFiles=new MusicFiles(path,title,artist,album,duration, imageUri);

                tempAudioList.add(musicFiles);
                if (!duplicate.contains(album)){
                    albums.add(musicFiles);
                    duplicate.add(album);
                }
            }
            cursor.close();

        }

        return tempAudioList;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput=newText.toLowerCase();
        ArrayList<MusicFiles> searchFiles=new ArrayList<>();
        for (MusicFiles song:musicFiles){
            if (song.getTitle().toLowerCase().contains(userInput)){
                searchFiles.add(song);
            }
        }
        SongsFragment.musicAdapter.updateList(searchFiles);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor=getSharedPreferences(SORT_PREF,MODE_PRIVATE).edit();
        int getItem=item.getItemId();
        if (getItem==R.id.by_name){
            editor.putString("sorting","sortByName");
            editor.apply();
            this.recreate();
        } else if (getItem == R.id.by_date) {
            editor.putString("sorting","sortByDate");
            editor.apply();
            this.recreate();
        } else if (getItem == R.id.by_size) {
            editor.putString("sorting","sortBySize");
            editor.apply();
            this.recreate();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Intent intent =new Intent(this,MusicService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
        super.onResume();
        Handler handler = new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferencesCurrent=getSharedPreferences(CURRENT_POSITION,MODE_PRIVATE);
                int currentPo=preferencesCurrent.getInt(CURRENT_POSITION,0);
                CURRENT_POSITION_TO_FRAG=currentPo;
                handler.postDelayed(this, 1);
            }
        };
        handler.postDelayed(runnable, 2000);
        SharedPreferences preferences=getSharedPreferences(MUSIC_LAST_PLAYED,
                MODE_PRIVATE);
        String songName=preferences.getString(SONG_NAME,null);
        String path=preferences.getString(MUSIC_FILE,null);
        String singer=preferences.getString(SINGER_NAME,null);
        int po=preferences.getInt(SONG_PO,0);
        setData();
        if (path!=null){
            SHOW_MINI_PLAYER=true;
            PATH_TO_FRAG=path;
            SINGER_TO_FRAG=singer;
            SONG_NAME_TO_FRAG=songName;
            SONG_PO_TO_FRAG=po;
        }else {
            SHOW_MINI_PLAYER=false;
            PATH_TO_FRAG=null;
            SINGER_TO_FRAG=null;
            SONG_NAME_TO_FRAG=null;
            SONG_PO_TO_FRAG=0;
        }
    }
    @Override
    protected void onPause() {
        unbindService(this);
        super.onPause();
    }
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicService.BinderMusicService binder= (MusicService.BinderMusicService) iBinder;
        musicService=binder.getService();
        musicService.setCallBack(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService=null;
    }

    @Override
    public void nextClicked(int position) {
        Log.e("@action","next");
        if (shuffleBoolean&&!repeatBoolean){
            position=getRandom(musicFiles.size()-1);
        }
        else if (!shuffleBoolean&&!repeatBoolean){
            position++;
        }
        if (LIST_DATA_TO_FRAG.size()==position){
            position--;
            Toast.makeText(musicService, R.string.endList, Toast.LENGTH_SHORT).show();
        }else {
            musicService.setMediaPlayer(position, LIST_DATA_TO_FRAG);
        }
        musicService.showNotification(R.drawable.baseline_pause_24);

    }

    @Override
    public void prevClicked(int position) {
        Log.e("@action","prev");
        if (!musicService.timePrev()) {
            musicService.setMediaPlayer(position, LIST_DATA_TO_FRAG);
        }else {
        if (shuffleBoolean&&!repeatBoolean){
            position=getRandom(musicFiles.size()-1);
        }
        else if (!shuffleBoolean&&!repeatBoolean){
            position--;
        }
        if (0>position) {
            position++;
            Toast.makeText(musicService, R.string.endList, Toast.LENGTH_SHORT).show();
        }else {
            musicService.setMediaPlayer(position, LIST_DATA_TO_FRAG);
        }}
        musicService.showNotification(R.drawable.baseline_pause_24);
    }

    @Override
    public void playClicked(int position) {
        Log.e("@action","play");
        if (musicService.isPlaying()){
            musicService.pause();

            musicService.showNotification(R.drawable.baseline_play_arrow_24);
        }else {
            musicService.start();
            musicService.showNotification(R.drawable.baseline_pause_24);
        }
    }

    @Override
    public void cancel() {
        Log.e("@action","cancel");
        musicService.pause();
    }
    private int getRandom(int i) {
        Random random=new Random();
        return random.nextInt(i+1);
    }
    private void setData() {
        compositeDisposable.add(
                viewModelListSong.getData().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<MusicFiles>>() {
                    @Override
                    public void accept(List<MusicFiles> dataClassListSongs) throws Throwable {
                        StringBuilder builder = new StringBuilder("");
                        if (!dataClassListSongs.isEmpty()) {
                            emptyList=false;
                            LIST_DATA_TO_FRAG= (ArrayList<MusicFiles>) dataClassListSongs;
                        }else{
                            emptyList = true;
                        }
                        for (MusicFiles dataClass : dataClassListSongs) {
                            builder.append(" : ")
                                    .append(dataClass.getId())
                                    .append(" : ")
                                    .append(dataClass.getTitle());
                        }
                        Log.e("dataClass",""+builder);
                    }
                })
        );
    }


}