package com.example.slmovie;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User
{
    static public FirebaseUser auth;
    static public List<Movie> my_movie;
    static public FirebaseFirestore db;
    public static ArrayList<Movie> movies;
    public static Context hz;

    public static void init_user(FirebaseUser auth_)
    {
        auth = auth_;
        db = FirebaseFirestore.getInstance();
        // нельзя инициализировать так фильмы надо в макетах
        load_films();
        //load_all_films();
    }

    private static void add(Movie movie)
    {
        boolean find = false;
        for (int i = 0; i < my_movie.size(); ++i)
        {
            if(my_movie.get(i).id.equals(movie.id))
            {
                find = true;
                break;
            }
        }
        if(!find)
        {
            Map<String, Object> movie_new = new HashMap<>();
            movie_new.put("id", auth.getUid());
            movie_new.put("movie_id", movie.id);
            my_movie.add(movie);

            db.collection("user_movie")
                    .add(movie_new)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference)
                        {
                            Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error adding document", e);
                        }
                    });
        }
    }

    private static Movie temp;
    public static void add_movie(Movie movie)
    {
        temp = movie;
        if(my_movie != null)
        {
            add(movie);
        }
        else
        {
            my_movie = new ArrayList<>();
            db.collection("user_movie")
                    .whereEqualTo("id", auth.getUid())
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
                                    DocumentReference docRef = db.collection("movie")
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
                                                    my_movie.add(gson.fromJson(movie_whith_id, Movie.class));
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
                            add(temp);
                        }
                    });
        }
    }

    private static String temp_delete = "";
    private static void delete(Movie movie)
    {
        boolean find = false;
        for (int i = 0; i < my_movie.size(); ++i)
        {
            if(my_movie.get(i).id.equals(movie.id))
            {
                find = true;
                Log.d("TAG", movie.id + " нашел");
                my_movie.remove(i);
                break;
            }
        }
        if(find)
        {
            //User.my_movie = new ArrayList<>();
            Log.e("BAG",temp_delete);
            User.db.collection("user_movie").document(temp_delete)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "DocumentSnapshot successfully deleted!");
                            temp_delete = "";
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error deleting document", e);
                            temp_delete = "";
                        }
                    });
        }
    }

    public static void delete_movie(Movie movie)
    {
        temp = movie;
        if (temp_delete == "")
        {
            db.collection("user_movie")
                    .whereEqualTo("movie_id", movie.id)
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
                                    temp_delete = document.getId();
                                }
                            }
                            else
                            {
                                Log.e("TAGload", "Error getting documents.", task.getException());
                            }
                            delete(temp);
                        }
                    });
        }
        else
        {
            delete(temp);
        }
    }

    private static void load_films()
    {


    }

    public static void load_all_films()
    {
        db.collection("movie")
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
                                movies.add(gson.fromJson(movie_whith_id, Movie.class));
                            }
                        }
                        else
                        {
                            Log.e("TAGload", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public static Movie get_good_movie()
    {
        //TODO сделать любой рандомный генератор фильмов
        // например рандомный фильм схожий по жанру
        return null;
    }
}
