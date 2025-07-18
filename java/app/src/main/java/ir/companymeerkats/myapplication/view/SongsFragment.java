package ir.companymeerkats.myapplication.view;

import static ir.companymeerkats.myapplication.view.MainActivity.musicFiles;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ir.companymeerkats.myapplication.R;


public class SongsFragment extends Fragment implements SearchView.OnQueryTextListener {
    RecyclerView recyclerView;
    static AdapterSong musicAdapter;
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_songs,container,false);
        recyclerView=view.findViewById(R.id.recyclerViewSong);
        setHasOptionsMenu(true);
        if (!musicFiles.isEmpty()) {
            musicAdapter=new AdapterSong(MainActivity.context,musicFiles);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
            recyclerView.setAdapter(musicAdapter);

        }
        return view;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search,menu);
        MenuItem searchItem=menu.findItem(R.id.search_option);
        SearchView searchView= (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
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
}