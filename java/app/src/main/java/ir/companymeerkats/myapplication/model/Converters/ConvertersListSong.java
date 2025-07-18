package ir.companymeerkats.myapplication.model.Converters;

import android.net.Uri;

import androidx.room.TypeConverter;

public class ConvertersListSong {
    @TypeConverter
    public Uri fromString(String value){
        return Uri.parse(value);
    }
    @TypeConverter
    public String toString(Uri uri){
        return uri.toString();
    }

}