package com.endpoint.nadres.activities_fragments.activity_video;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.endpoint.nadres.R;
import com.endpoint.nadres.databinding.ActivityVideoBinding;
import com.endpoint.nadres.language.Language;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.Locale;

import io.paperdb.Paper;

public class VideoActivity extends AppCompatActivity {
    private ActivityVideoBinding binding;
    private SimpleExoPlayer player;
    private int currentWindow = 0;
    private long currentPosition = 0;
    private boolean playWhenReady = true;
    private String url;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video);
        getDataFromIntent();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
    }


    private void initPlayer(String url) {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd());
        player = ExoPlayerFactory.newSimpleInstance(this);
        binding.videoView.setPlayer(player);
        MediaSource mediaSource = buildMediaSource(Uri.parse(url));

        player.seekTo(currentWindow, currentPosition);
        player.setPlayWhenReady(playWhenReady);
        player.prepare(mediaSource, false, false);



    }

    private MediaSource buildMediaSource(Uri uri) {
        String userAgent = "exoPlayer_youtube";


        if (uri.getLastPathSegment().contains("mp3") || uri.getLastPathSegment().contains("mp4")) {
            return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);
        } else if (uri.getLastPathSegment().contains("m3u8")) {

            return new HlsMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);
        } else {

            DefaultDashChunkSource.Factory factory = new DefaultDashChunkSource.Factory(new DefaultHttpDataSourceFactory(userAgent));

            return new DashMediaSource.Factory(factory, new DefaultDataSourceFactory(this, userAgent)).createMediaSource(uri);
        }

    }




    private void releasePlayer(){
        if (player!=null){
            playWhenReady = player.getPlayWhenReady();
            currentWindow = player.getCurrentWindowIndex();
            currentPosition = player.getCurrentPosition();
            player.release();
            player =null;

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT>=24){
            initPlayer(url);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.SDK_INT<24||player==null){
            initPlayer(url);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT>=24){
            releasePlayer();
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT<24){
            releasePlayer();
        }
    }





        @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}

