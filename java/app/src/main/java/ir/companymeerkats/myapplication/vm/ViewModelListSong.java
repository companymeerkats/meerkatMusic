package ir.companymeerkats.myapplication.vm;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import ir.companymeerkats.myapplication.model.DataListSongRepository;
import ir.companymeerkats.myapplication.view.MusicFiles;

public class ViewModelListSong {
    private final DataListSongRepository dataListSongRepository;

    public ViewModelListSong(DataListSongRepository dataListSongRepository) {
        this.dataListSongRepository = dataListSongRepository;
    }
    public Flowable<List<MusicFiles>> getData(){
        return dataListSongRepository.getData();
    }
    public Single<MusicFiles>getDataById(long id){
        return dataListSongRepository.getDataById(id);
    }
    public Completable insertData(ArrayList<MusicFiles> dataClassListSong){
        return dataListSongRepository.insertData(dataClassListSong);
    }

    public Completable deleteData(){
        return dataListSongRepository.deleteData();
    }
}
