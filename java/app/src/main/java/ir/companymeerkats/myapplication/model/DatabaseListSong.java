package ir.companymeerkats.myapplication.model;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ir.companymeerkats.myapplication.model.Converters.ConvertersListSong;
import ir.companymeerkats.myapplication.view.MusicFiles;

@androidx.room.Database(entities = {MusicFiles.class}, version = 2)
@TypeConverters({ConvertersListSong.class})
public abstract class DatabaseListSong extends RoomDatabase {
    public final static String DATABASE_NAME = "ListSong";

    private static DatabaseListSong database;

    public abstract DataListSong data();

    public static DatabaseListSong getInstance(Context context) {
        if (database == null) {
            synchronized (DatabaseListSong.class) {
                database = Room.databaseBuilder(context, DatabaseListSong.class, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return database;
    }
}
