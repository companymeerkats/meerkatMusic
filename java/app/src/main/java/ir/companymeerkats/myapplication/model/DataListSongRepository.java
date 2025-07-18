package ir.companymeerkats.myapplication.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class DataListSongRepository {
    private final DataListSong dataListSongDao;

    public DataListSongRepository(Context context) {
        dataListSongDao = DatabaseListSong.getInstance(context).data();
    }
    public Flowable<List<MusicFiles>> getData() {
        return dataListSongDao.getData();
    }
    public Single<MusicFiles> getDataById(long id){
        return dataListSongDao.getDataById(id);
    }
    public Completable insertData(ArrayList<MusicFiles> DataClassListSong) {
        return dataListSongDao.insertData(DataClassListSong);
    }


    public Completable deleteData() {
        return dataListSongDao.deleteData();
    }
}
