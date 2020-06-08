package com.example.slmovie.ui.dashboard;

import android.app.Activity;
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
import com.example.slmovie.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    public static View root;
    private DashboardViewModel dashboardViewModel;
    private static DashboardFragment th;

    public DashboardFragment() {
    }

    public static void unsafe()
    {
        if (root != null && th != null)
        {
            RecyclerView rv = root.findViewById(R.id.rv);
            rv.setLayoutManager(new GridLayoutManager(User.hz, 1));
            rv.setAdapter(new MovieAdapter(User.my_movie));
        }
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        th = this;

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
                                            RecyclerView rv = root.findViewById(R.id.rv);
                                            rv.setLayoutManager(new GridLayoutManager(User.hz, 1));
                                            Log.e("TTT",User.my_movie.size() + "");
                                            rv.setAdapter(new MovieAdapter(User.my_movie));
                                        }
                                    });
                                }

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
            RecyclerView rv = root.findViewById(R.id.rv);
            rv.setLayoutManager(new GridLayoutManager(User.hz, 1));
            rv.setAdapter(new MovieAdapter(User.my_movie));
        }
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }
}