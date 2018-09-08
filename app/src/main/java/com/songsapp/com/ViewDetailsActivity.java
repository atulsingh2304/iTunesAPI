package com.songsapp.com;


import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.songsapp.com.common.interfaces.OnVideoSourceLoadCompleteListener;
import com.songsapp.com.common.ui.CircularImageView;
import com.songsapp.com.common.utils.Constants;
import com.songsapp.com.common.utils.GetVideoDataSourceUtils;
import com.songsapp.com.model.MediaModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ViewDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    // Member variables.
    private Context mContext;
    private View audioLayout;
    private CircularImageView ivProfile;
    private TextView tvTitle, tvOwnerName, tvReleaseDate, tvPrice;
    private TextView tvPlay, tvMusicIcon, tvDuration, tvBufferDuration;
    private ProgressBar pbMusicLoading, pbVideoLoading;
    private SeekBar sbMusic;
    private VideoView videoView;

    // Class variables and custom classes.
    private long mediaFileLengthInMilliseconds;
    private boolean isMusicLoaded = false, isSongPlaying = false;

    private final Handler handler = new Handler();
    private MediaPlayer mPlayer;
    private MediaModel mediaModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        mContext = ViewDetailsActivity.this;
        // get views.
        getViews();

        mediaModel = getIntent().getParcelableExtra(Constants.MEDIA_MODEL);
        // set data in views.
        displayInformation();
    }

    private void getViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Typeface typeface = Constants.getFontIconTypeFace(mContext);

        audioLayout = findViewById(R.id.audioLayout);
        ivProfile = findViewById(R.id.image_profile);
        tvTitle = findViewById(R.id.title);
        tvOwnerName = findViewById(R.id.owner);
        tvReleaseDate = findViewById(R.id.release_date);
        tvPrice = findViewById(R.id.price);

        tvOwnerName.setTypeface(typeface);
        tvReleaseDate.setTypeface(typeface);
        tvPrice.setTypeface(typeface);

        tvPlay = findViewById(R.id.playButtonIcon);
        tvMusicIcon = findViewById(R.id.musicIcon);
        tvDuration = findViewById(R.id.textDuration);
        tvBufferDuration = findViewById(R.id.textBufferDuration);
        pbMusicLoading = findViewById(R.id.progress);
        sbMusic = findViewById(R.id.playback_view_seekbar);
        sbMusic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        tvPlay.setTypeface(typeface);
        tvPlay.setText("\uf04b");
        tvMusicIcon.setTypeface(typeface);
        tvMusicIcon.setText("\uf001");
        tvPlay.setOnClickListener(this);

        //Setting up local video player
        pbVideoLoading = findViewById(R.id.videoLoadingBar);
        videoView = findViewById(R.id.video_player);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.requestFocus();
    }

    /**
     * Method to show the view information.
     */
    private void displayInformation() {
        tvOwnerName.setText("\uf007 " + mediaModel.getOwnerName());
        tvReleaseDate.setText("\uf073 " + mediaModel.getReleaseDate());
        tvPrice.setText("\uf155 " + mediaModel.getPrice());

        if (mediaModel.isAudioMedia()) {
            videoView.setVisibility(View.GONE);
            audioLayout.setVisibility(View.VISIBLE);
            ivProfile.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);
            if (mediaModel.getArtworkImage() != null && !mediaModel.getArtworkImage().isEmpty()) {
                ivProfile.hideText();
                Picasso.with(mContext)
                        .load(mediaModel.getArtworkImage())
                        .into(ivProfile);
            } else {
                ivProfile.showText(mediaModel.getTrackName(), mediaModel.getProfileColor());
            }

            tvTitle.setText(mediaModel.getTrackName());
            showMusicPlayer();
        } else {
            ivProfile.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            audioLayout.setVisibility(View.GONE);
            showVideoPlayer();
        }
    }

    /**
     * Method to load music player if media is audio type.
     */
    private void showMusicPlayer() {
        // for audio player.
        pbMusicLoading.bringToFront();
        pbMusicLoading.setVisibility(View.VISIBLE);
        tvPlay.setText("");
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(mediaModel.getMediaPreviewUrl());
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    if (mediaPlayer != null) {
                        pbMusicLoading.setVisibility(View.GONE);
                        sbMusic.setEnabled(true);
                        tvPlay.setClickable(true);
                        isMusicLoaded = true;
                        if (isSongPlaying) {
                            tvPlay.setText("\uf04c");
                        } else {
                            tvPlay.setText("\uF04B");
                        }
                    }
                }
            });

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    tvPlay.setText("\uF04B");
                    isSongPlaying = false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isMusicLoaded) {
            pbMusicLoading.setVisibility(View.GONE);
            sbMusic.setEnabled(true);
            tvPlay.setClickable(true);
            if (isSongPlaying) {
                tvPlay.setText("\uf04c");
            } else {
                tvPlay.setText("\uF04B");
            }
        } else {
            pbMusicLoading.bringToFront();
            pbMusicLoading.setVisibility(View.VISIBLE);
            sbMusic.setEnabled(false);
            tvPlay.setClickable(false);
            tvPlay.setText("");
        }
    }

    /**
     * Method to load video player if media is video type.
     */
    private void showVideoPlayer() {
        try {
            pbVideoLoading.bringToFront();
            pbVideoLoading.setVisibility(View.VISIBLE);
            new GetVideoDataSourceUtils(mediaModel.getMediaPreviewUrl(), new OnVideoSourceLoadCompleteListener() {
                @Override
                public void onSuccess(String videoPath) {
                    if (videoPath != null && !videoPath.isEmpty()) {
                        videoView.setVideoPath(videoPath);
                        videoView.start();
                        videoView.requestFocus();
                    }
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            videoView.setBackground(null);
                            pbVideoLoading.setVisibility(View.GONE);
                        }
                    });
                }
            }).execute();

        } catch (Exception e) {
            if (videoView != null) {
                videoView.stopPlayback();
            }
            e.printStackTrace();
        }
    }

    /**
     * Function to update the seek bar progress when a audio file is played
     */
    private void primarySeekBarProgressUpdater() {
        if (sbMusic != null && mPlayer != null) {
            tvBufferDuration.setVisibility(View.VISIBLE);
            tvBufferDuration.setText(Constants.getDuration(mPlayer.getCurrentPosition()));

            tvDuration.setVisibility(View.VISIBLE);
            tvDuration.setText(Constants.getDuration(mediaFileLengthInMilliseconds));
            sbMusic.setEnabled(true);
            // This math construction give a percentage of "was playing"/"song length"
            sbMusic.setProgress((int) (((float) mPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100));
            if (mPlayer.isPlaying()) {
                handler.postDelayed(notification, 1000);
            }
        }
    }

    Runnable notification = new Runnable() {
        public void run() {
            primarySeekBarProgressUpdater();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null && mPlayer.isPlaying() && isMusicLoaded && isSongPlaying) {
            mPlayer.pause();
            mPlayer.release();
        }

        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
        handler.removeCallbacks(notification);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        try {
            if (isMusicLoaded && mPlayer != null) {
                if (!mPlayer.isPlaying()) {
                    isSongPlaying = true;
                    tvPlay.setText("\uf04c");
                    mPlayer.start();
                    mediaFileLengthInMilliseconds = 0;
                    mediaFileLengthInMilliseconds = mediaModel.getDuration();
                    primarySeekBarProgressUpdater();
                } else {
                    isSongPlaying = false;
                    mPlayer.pause();
                    tvPlay.setText("\uF04B");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
