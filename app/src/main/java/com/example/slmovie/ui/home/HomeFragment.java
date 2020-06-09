package com.example.slmovie.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slmovie.Movie;
import com.example.slmovie.MovieAdapter;
import com.example.slmovie.R;
import com.example.slmovie.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        final HomeFragment th = this;

        final List<Movie> films = new ArrayList<>();
        if(User.movies == null)
        {
            User.movies = new ArrayList<>();
            User.db.collection("movie")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            if (task.isSuccessful())
                            {
                                Gson gson = new Gson();
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    Log.e("TAGload", document.getId() + " => " + document.getData());
                                    //movies.add(document.toObject(Movie.class));
                                    String movie_whith_id = document.getData().toString();
                                    movie_whith_id = movie_whith_id.substring("{movie_json={".length(),movie_whith_id.length()-2);
                                    movie_whith_id = "{" + movie_whith_id + ",\"id\":" + "\"" + document.getId() + "\"" + "}";

                                    Log.e("TAGMY", movie_whith_id);
                                    User.movies.add(gson.fromJson(movie_whith_id, Movie.class));
                                }
                            }
                            else
                            {
                                Log.e("TAGload", "Error getting documents.", task.getException());
                            }
                            if(User.my_movie == null)
                            {
                                User.my_movie = new ArrayList<>();
                                User.db.collection("user_movie")
                                        .whereEqualTo("id", User.auth.getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                                            {
                                                if (task.isSuccessful())
                                                {
                                                    for (QueryDocumentSnapshot document : task.getResult())
                                                    {
                                                        DocumentReference docRef = User.db.collection("movie")
                                                                .document((String) document.getData().get("movie_id"));
                                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                Gson gson = new Gson();
                                                                if (task.isSuccessful())
                                                                {
                                                                    DocumentSnapshot document = task.getResult();
                                                                    if (document.exists())
                                                                    {
                                                                        String movie_whith_id = document.getData().toString();
                                                                        movie_whith_id = movie_whith_id.substring("{movie_json={".length(),movie_whith_id.length()-2);
                                                                        movie_whith_id = "{" + movie_whith_id + ",\"id\":" + "\"" + document.getId() + "\"" + "}";

                                                                        Log.e("TAGMY", movie_whith_id);
                                                                        User.my_movie.add(gson.fromJson(movie_whith_id, Movie.class));
                                                                    }
                                                                    else
                                                                    {
                                                                        Log.d("TAG", "No such document");
                                                                    }
                                                                    Log.e("TAGMY", "TEST1");
                                                                }
                                                                else
                                                                {
                                                                    Log.d("TAG", "get failed with ", task.getException());
                                                                }
                                                                Log.e("TAGMY", "TEST2");

                                                            }
                                                        });
                                                    }
                                                    films.clear();
                                                    Log.e("TTT",User.my_movie.size() + "");
                                                    for(Movie movie: User.movies)
                                                    {
                                                        boolean find = false;
                                                        for(Movie my: User.my_movie)
                                                        {
                                                            if(my.id.equals(movie.id))
                                                            {
                                                                find = true;
                                                                break;
                                                            }
                                                        }
                                                        if(!find)
                                                        {
                                                            films.add(movie);
                                                            Log.e("BAG","3");
                                                        }
                                                    }
                                                    RecyclerView rv = root.findViewById(R.id.rv_movie);
                                                    rv.setLayoutManager(new GridLayoutManager(User.hz, 1));
                                                    rv.setAdapter(new MovieAdapter(films));
                                                    Log.e("BAG",films.size() + "");
                                                }
                                                else
                                                {
                                                    Log.e("TAGload", "Error getting documents.", task.getException());
                                                }
                                            }
                                        });
                            }
                            else
                            {
                                films.clear();
                                for(Movie movie: User.movies)
                                {
                                    boolean find = false;
                                    for(Movie my: User.my_movie)
                                    {
                                        if(my.id.equals(movie.id))
                                        {
                                            find = true;
                                            break;
                                        }
                                    }
                                    if(!find)
                                    {
                                        films.add(movie);
                                    }
                                }
                                RecyclerView rv = root.findViewById(R.id.rv_movie);
                                rv.setLayoutManager(new GridLayoutManager(User.hz, 1));
                                Log.e("BAG",films.size() + "");
                                rv.setAdapter(new MovieAdapter(films));
                            }
                        }
                    });
        }
        else
        {
            films.clear();
            for(Movie movie: User.movies)
            {
                boolean find = false;
                for(Movie my: User.my_movie)
                {
                    if(my.id.equals(movie.id))
                    {
                        find = true;
                        break;
                    }
                }
                if(!find)
                {
                    Log.e("BAG","1");
                    films.add(movie);
                }
            }
            Log.e("BAG",films.size() + "");
            RecyclerView rv = root.findViewById(R.id.rv_movie);
            rv.setLayoutManager(new GridLayoutManager(User.hz, 1));
            rv.setAdapter(new MovieAdapter(films));
        }


        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }
}