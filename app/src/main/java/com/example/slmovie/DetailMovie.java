package com.example.slmovie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slmovie.ui.dashboard.DashboardFragment;
import com.example.slmovie.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class DetailMovie extends AppCompatActivity
{
    TextView name;
    TextView genres;
    TextView description;
    ImageView img;
    Button add;
    Button delete;
    Movie it;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie);

        Intent intent = getIntent();

        name = findViewById(R.id.textView3);
        genres = findViewById(R.id.textView4);
        description = findViewById(R.id.textView8);
        img = findViewById(R.id.imageView2);
        add = findViewById(R.id.add);
        delete = findViewById(R.id.delete);

        String id = intent.getStringExtra("id");
        for(Movie movie : User.movies)
        {
            if(movie.id.equals(id))
            {
                it = movie;
                name.setText("Название: " + movie.name);
                //description.setText(movie.descr);
                String s = "Жанр: ";
                for(String genre : movie.genre)
                {
                    s += genre + ", ";
                }
                genres.setText(s.substring(0,s.length()-2));
                Picasso.get().load(movie.url_img).into(img);
                break;
            }
        }

        if (User.my_movie == null)
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
                                            }
                                            else
                                            {
                                                Log.d("TAG", "get failed with ", task.getException());
                                            }
                                        }
                                    });
                                }
                            }
                            else
                            {
                                Log.e("TAGload", "Error getting documents.", task.getException());
                            }
                            boolean find = false;
                            for(Movie movie: User.my_movie)
                            {
                                if(movie.id.equals(it.id))
                                {
                                    find = true;
                                    add.setEnabled(false);
                                    delete.setEnabled(true);
                                    break;
                                }
                            }
                            if(!find)
                            {
                                add.setEnabled(true);
                                delete.setEnabled(false);
                            }
                        }
                    });
        }
        else
        {
            boolean find = false;
            for(Movie movie: User.my_movie)
            {
                if(movie.id.equals(it.id))
                {
                    find = true;
                    add.setEnabled(false);
                    delete.setEnabled(true);
                    break;
                }
            }
            if(!find)
            {
                add.setEnabled(true);
                delete.setEnabled(false);
            }
        }
        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                User.add_movie(it);
                add.setEnabled(false);
                delete.setEnabled(true);
                DashboardFragment.unsafe();
            }
        });
        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                User.delete_movie(it);
                delete.setEnabled(false);
                add.setEnabled(true);
                DashboardFragment.unsafe();
            }
        });
    }
}
