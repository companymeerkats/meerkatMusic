package ir.companymeerkats.myapplication.view;

import static ir.companymeerkats.myapplication.view.MainActivity.albums;
import static ir.companymeerkats.myapplication.view.MainActivity.musicFiles;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ir.companymeerkats.myapplication.R;

public class AlbumFragment extends Fragment {
    RecyclerView recyclerView;
    AdapterAlbum albumAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_album,container,false);
        recyclerView=view.findViewById(R.id.recyclerViewAlbum);
        if (!albums.isEmpty()) {
            albumAdapter=new AdapterAlbum(getContext(),albums);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
            recyclerView.setAdapter(albumAdapter);

        }
        return view;    }


}