package co.bondtech.bondplayer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends YouTubeBaseActivity {
    public static final String API_KEY = "AIzaSyBv4gDlHvqwhV9ef3qdsdqYh8lF9YDUEPE";
    ArrayList<String> playList;
    int nowPlaying=0;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playList= new ArrayList<>();
        loadList();
        YouTubePlayerView youTubePlayerView =(YouTubePlayerView) findViewById(R.id.player);
        youTubePlayerView.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
                        // do any work here to cue video, play video, etc.
//                        youTubePlayer.cueVideo("5xVh-7ywKpE");
                        // or to play immediately
                        youTubePlayer.loadVideo("JesZWS0att8");
                        youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                            @Override
                            public void onLoading() {
                            }
                            @Override
                            public void onLoaded(String s) {
                            }
                            @Override
                            public void onAdStarted() {
                            }
                            @Override
                            public void onVideoStarted() {
                            }
                            @Override
                            public void onVideoEnded() {
                                loadList();
                                int next=whatsNext();
//                                Toast.makeText(MainActivity.this, "Coming next : "+next, Toast.LENGTH_SHORT).show();
                                youTubePlayer.loadVideo(playList.get(next));
                            }
                            @Override
                            public void onError(YouTubePlayer.ErrorReason errorReason) {
                            }
                        });
                    }
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,YouTubeInitializationResult youTubeInitializationResult) {
                        Toast.makeText(MainActivity.this, "Youtube Failed!", Toast.LENGTH_SHORT).show();
                    }

                });

    }

    private void loadList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ReadJSON().execute("http://bondplayer.iamimam.com/playlist.php");
            }
        });
    }
    private int whatsNext() {
        return (nowPlaying++)%playList.size();
    }

    public void showPlayList(View view) {
        for(int c=0;c<playList.size();c++){
            Log.v("LISTis "+c," "+playList.get(c));
        }
    }

    class ReadJSON extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MainActivity.this, "please wait", "processing");
        }
        @Override
        protected String doInBackground(String... params) {
            return readURL(params[0]);
        }

        @Override
        protected void onPostExecute(String content) {
            dialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(content);
                Log.v("jsonresp", String.valueOf(jsonObject));
                JSONArray jsonArray =  jsonObject.getJSONArray("result");
                for(int i =0;i<jsonArray.length(); i++){
                    JSONObject productObject = jsonArray.getJSONObject(i);
                    String requestedLink=productObject.getString("youtubeID");
                    String requested="";
                    if(requestedLink.length()==11){
                        requested=requestedLink;
                        Log.v("LinktoIDr",requested);
                        Log.v("LinktoIDrlength",requested.length()+"");
                    }
                    else {
                        requested = linkToID(requestedLink);
                    }


                    if(checkValid(requested)){
                        playList.add(requested);
//                        Toast.makeText(MainActivity.this, "ID"+requested, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, "ERROR route, try again", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private String linkToID(String link) {
        String id;
        if(link.contains(".be")){
            String[] parts = link.split(".be/");
            id = parts[1].substring(0,11);
        }else {
            String[] parts = link.split("=");
            id = parts[1].substring(0,11);
        }
        return id;
    }

    private boolean checkValid(String requested) {
        for(int c=0;c<playList.size();c++){
            if (playList.get(c).equals(requested)){
                return false;
            }
        }
        return true;
    }


    private static String readURL(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}