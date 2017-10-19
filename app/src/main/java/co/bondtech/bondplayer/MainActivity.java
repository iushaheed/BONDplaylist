package co.bondtech.bondplayer;

import android.os.Bundle;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;

public class MainActivity extends YouTubeBaseActivity {
    public static final String API_KEY = "AIzaSyBv4gDlHvqwhV9ef3qdsdqYh8lF9YDUEPE";
    ArrayList<String> playList;
    int nowPlaying=0;
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
                        youTubePlayer.loadVideo(playList.get(nowPlaying));
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
                                int next=whatsNext();
                                Toast.makeText(MainActivity.this, "Coming next : "+next, Toast.LENGTH_SHORT).show();
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
        playList.add("uqkm0MnHAv4");
        playList.add("HcHry3z4FCQ");
        playList.add("w5N6Zw-pWtY");
        playList.add("XOgZo66gKQA");
        playList.add("mOnGAQHbrK4");
    }

    private int whatsNext() {
        return (nowPlaying++)%playList.size();
    }
}