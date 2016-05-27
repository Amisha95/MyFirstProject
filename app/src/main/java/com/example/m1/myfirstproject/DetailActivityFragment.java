package com.example.m1.myfirstproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public static String title;
    public static String path;
    public static String overview;
    public static String rating;
    public static String date;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent=getActivity().getIntent();
        getActivity().setTitle("Movie Details");
        if(intent!=null&&intent.hasExtra("original_title"))
        {
            title=intent.getStringExtra("original_title");
            TextView textView=(TextView)rootView.findViewById(R.id.title);
            textView.setText(title);

        }
        if(intent!=null&&intent.hasExtra("overview"))
        {
            overview=intent.getStringExtra("overview");
            TextView textView=(TextView)rootView.findViewById(R.id.overview);
            textView.setText(overview);

        }
        if(intent!=null&&intent.hasExtra("vote_average"))
        {
            rating=intent.getStringExtra("vote_average");
            TextView textView=(TextView)rootView.findViewById(R.id.rating);
            textView.setText(rating);

        }
        if(intent!=null&&intent.hasExtra("release_date"))
        {
            date=intent.getStringExtra("release_date");
            TextView textView=(TextView)rootView.findViewById(R.id.date);
            textView.setText(date);

        }
        if(intent!=null&&intent.hasExtra("poster_path"))
        {
            path=intent.getStringExtra("poster_path");
            ImageView imageView=(ImageView)rootView.findViewById(R.id.image1);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/"+path).into(imageView);

        }
        return rootView;
    }
}
