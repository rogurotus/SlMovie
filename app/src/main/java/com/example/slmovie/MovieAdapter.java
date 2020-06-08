package com.example.slmovie;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slmovie.ui.home.HomeFragment;
import com.squareup.picasso.Picasso;

import java.io.FileReader;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private final List<Movie> values;

    public MovieAdapter(List<Movie> ms) {
        values = ms;
        Log.e("SIZE",values.size() + "");
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Picasso.get().load(values.get(position).url_img).into(holder.imageView);
        /*
        holder.itemView.setTag(values.get(position));
        holder.idView.setText(values.get(position).id);
        holder.contentView.setText(values.get(position).content);
        holder.itemView.setTag(values.get(position));
        */
        holder.name.setText(values.get(position).name);
        String s = "";
        for(String genre : values.get(position).genre)
        {
            s += genre + ", ";
        }
        holder.genre.setText(s.substring(0,s.length()-2));
        holder.itemView.setTag(values.get(position));
        holder.itemView.setOnClickListener(onClickListener);
    }
    @Override
    public int getItemCount() { return values.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView genre;
        //final TextView contentView;
        final ImageView imageView;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.textView);
            genre = view.findViewById(R.id.textView10);
            imageView = view.findViewById(R.id.imageView);
        }
    }

    final private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.e("click","1");
            Movie item = (Movie) view.getTag();
            Context context = view.getContext();
            Log.e("click","2");
            Intent intent = new Intent(context, DetailMovie.class);
            Log.e("click","3");
            intent.putExtra("id", item.id);
            Log.e("click","4");
            context.startActivity(intent);
        }
    };
}