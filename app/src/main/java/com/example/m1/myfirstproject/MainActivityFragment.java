package com.example.m1.myfirstproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivityFragment extends Fragment
{
    static GridView gridView;
    static boolean sortBy;
    static ArrayList<String> Posters;
    static PreferenceChangeListener listener;
    static SharedPreferences prefs;

    static ArrayList<String> originalTitle;
    static ArrayList<String> moviePosterThumbnail;
    static ArrayList<String> plotSynopsis;
    static ArrayList<String> userRating;
    static ArrayList<String> releaseDate;
    static String API_KEY="\n";
    public MainActivityFragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        if (getActivity() != null) {
            ArrayList<String> arrayList = new ArrayList<String>();
            PosterAdapter adapter = new PosterAdapter(getActivity(), arrayList);
            gridView = (GridView) rootView.findViewById(R.id.gridView);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class).
                            putExtra("original_title", originalTitle.get(position)).
                            putExtra("poster_path", moviePosterThumbnail.get(position)).
                            putExtra("overview", plotSynopsis.get(position)).
                            putExtra("vote_average", userRating.get(position)).
                            putExtra("release_date", releaseDate.get(position));
                    startActivity(intent);
                }
            });

        }
        return rootView;
    }

    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            gridView.setAdapter(null);
            onStart();
        }

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public void onStart()
    {
        super.onStart();
        prefs= PreferenceManager.getDefaultSharedPreferences(getActivity());
        listener=new PreferenceChangeListener();
        prefs.registerOnSharedPreferenceChangeListener(listener);
        if(prefs.getString("sortby","popularity").equals("popularity"))
        {
            getActivity().setTitle("Most Popular");
            sortBy=true;
        }
        else if(prefs.getString("sortby","rating").equals("rating"))
        {
            getActivity().setTitle("Highest Rated");
            sortBy=false;
        }

        TextView textView=(TextView)getView().findViewById(R.id.network_text);
        if(isNetworkAvailable())
        {
            gridView.setVisibility(GridView.VISIBLE);
            textView.setVisibility(View.GONE);
            new PosterLoadTask().execute();
        }
        else
        {
            textView.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        }
    }
    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();
    }

    public class PosterLoadTask extends AsyncTask<Void,Void,ArrayList<String>>
    {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            while (true) {
                try {
                    Posters = new ArrayList(Arrays.asList(getPathsFromAPI(sortBy)));
                } catch (Exception e) {
                    continue;
                }
                return Posters;
            }

        }

        protected void onPostExecute(ArrayList<String> result)
        {
            if(result!=null && getActivity()!=null) {
                PosterAdapter adapter = new PosterAdapter(getActivity(), result);
                gridView.setAdapter(adapter);
            }
        }

        public String[] getPathsFromAPI(boolean sort) throws IOException {
            while(true)
            {
                if(getActivity()!=null) {
                    HttpURLConnection connection = null;
                    BufferedReader bufferedReader = null;
                    String JSONResult;
                    try {
                        String URLString = null;
                        //Sort by Popularity
                        if (sortBy) {
                            URLString = "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;
                        }
                        //Sort by Most Rated
                        else {
                            URLString = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;
                        }
                        URL url = new URL(URLString);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.connect();
                        InputStream inputStream = connection.getInputStream();
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuffer stringBuffer = new StringBuffer();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuffer.append(line + "\n");
                        }

                        JSONResult = stringBuffer.toString();

                        try {
                            originalTitle = new ArrayList<String>(Arrays.asList(getStringFromJSON(JSONResult, "original_title")));
                            moviePosterThumbnail = new ArrayList<String>(Arrays.asList(getStringFromJSON(JSONResult, "poster_path")));
                            plotSynopsis = new ArrayList<String>(Arrays.asList(getStringFromJSON(JSONResult, "overview")));
                            userRating = new ArrayList<String>(Arrays.asList(getStringFromJSON(JSONResult, "vote_average")));
                            releaseDate = new ArrayList<String>(Arrays.asList(getStringFromJSON(JSONResult, "release_date")));
                            return PathsFromJSON(JSONResult);
                        } catch (JSONException e) {
                            return null;
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                            bufferedReader.close();
                        }
                    }
                    return null;
                }
            }
        }
        }

    public String[] getStringFromJSON(String JSONStringParam,String param) throws JSONException {
        JSONObject parentObject = new JSONObject(JSONStringParam);
        JSONArray moviesArray=parentObject.getJSONArray("results");
        String[] result=new String[moviesArray.length()];
        for(int i=0;i<moviesArray.length();i++)
        {
            JSONObject jsonObject=moviesArray.getJSONObject(i);
            if(param.equals("vote_average"))
            {
                Double vote=jsonObject.getDouble("vote_average");
                String rating=Double.toString(vote);
                result[i]=rating;
            }
            else {
                String data = jsonObject.getString(param);
                result[i] = data;
            }
        }
        return result;
    }

    public String[] PathsFromJSON(String JSONPaths) throws JSONException {
        JSONObject parentObject = new JSONObject(JSONPaths);
        JSONArray moviesArray=parentObject.getJSONArray("results");
        String[] result=new String[moviesArray.length()];
        for(int i=0;i<moviesArray.length();i++)
        {
            JSONObject jsonObject=moviesArray.getJSONObject(i);
            String moviePath=jsonObject.getString("poster_path");
            result[i]=moviePath;
        }
        return result;
    }
    }



