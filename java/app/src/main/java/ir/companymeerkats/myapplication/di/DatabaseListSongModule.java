package ir.companymeerkats.myapplication.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import ir.companymeerkats.myapplication.model.DataListSongRepository;
import ir.companymeerkats.myapplication.vm.ViewModelListSong;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseListSongModule {
    @Singleton
    @Provides
    public Context provideContext(@ApplicationContext Context context) {
        return context;
    }

    @Singleton
    @Provides
    public DataListSongRepository provideDataRepository(@ApplicationContext Context context) {
        return new DataListSongRepository(context);
    }


    @Singleton
    @Provides
    public ViewModelListSong provideViewModelData(DataListSongRepository dataRepository) {
        return new ViewModelListSong(dataRepository);
    }
}
