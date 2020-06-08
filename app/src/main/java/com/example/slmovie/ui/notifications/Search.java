package com.example.slmovie.ui.notifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slmovie.Movie;
import com.example.slmovie.MovieAdapter;
import com.example.slmovie.R;
import com.example.slmovie.User;
import com.example.slmovie.ui.dashboard.DashboardFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Search extends Activity
{
    List<Movie> result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_result_search);
        Intent intent = getIntent();

        final String name = intent.getStringExtra("name");

        final String genre = intent.getStringExtra("genre");
        Log.e("res","\'" + name + "\' \'" + genre + "\'");

        if (result == null) {
            User.movies = new ArrayList<>();
            User.db.collection("movie")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Gson gson = new Gson();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.e("TAGload", document.getId() + " => " + document.getData());
                                    //movies.add(document.toObject(Movie.class));
                                    String movie_whith_id = document.getData().toString();
                                    movie_whith_id = movie_whith_id.substring("{movie_json={".length(), movie_whith_id.length() - 2);
                                    movie_whith_id = "{" + movie_whith_id + ",\"id\":" + "\"" + document.getId() + "\"" + "}";

                                    Log.e("TAGMY", movie_whith_id);
                                    User.movies.add(gson.fromJson(movie_whith_id, Movie.class));
                                }
                            } else {
                                Log.e("TAGload", "Error getting documents.", task.getException());
                            }

                            result = new ArrayList<>();
                            RecyclerView rv = findViewById(R.id.films);
                            List<Movie> search_movies_name = new ArrayList<>();
                            if(name.equals(""))
                            {

                                search_movies_name = User.movies;
                                Log.e("res","GOOD " + search_movies_name.size());
                            }
                            else
                            {
                                for(Movie movie: User.movies)
                                {
                                    if(movie.name.equals(name))
                                    {
                                        search_movies_name.add(movie);
                                        Log.e("res",search_movies_name.size() + "");
                                    }
                                }
                            }

                            List<Movie> result = new ArrayList<>();
                            if(genre.equals(""))
                            {
                                result = search_movies_name;
                            }
                            else
                            {
                                for(Movie movie: search_movies_name)
                                {
                                    boolean find = false;
                                    for(String _genre: movie.genre)
                                    {
                                        if(genre.equals(_genre))
                                        {
                                            find = true;
                                            break;
                                        }
                                    }
                                    if(find)
                                    {
                                        result.add(movie);
                                    }
                                }
                            }
                            Log.e("res",result.size() + "");
                            rv.setLayoutManager(new GridLayoutManager(User.hz, 1));
                            rv.setAdapter(new MovieAdapter(result));
                        }
                    });
        }
        else
        {
            RecyclerView rv = findViewById(R.id.films);
            rv.setLayoutManager(new GridLayoutManager(User.hz, 1));
            rv.setAdapter(new MovieAdapter(result));
        }

    }
}
