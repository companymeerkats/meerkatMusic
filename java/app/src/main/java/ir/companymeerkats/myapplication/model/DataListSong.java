package ir.companymeerkats.myapplication.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface DataListSong {
    @Query("SELECT * FROM musicfile")
    Flowable<List<MusicFiles>> getData();

    @Query("SELECT * FROM musicfile WHERE id == :id")
    Single<MusicFiles> getDataById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertData(ArrayList<MusicFiles> data);


    @Query("DELETE FROM musicfile")
    Completable deleteData();
}
