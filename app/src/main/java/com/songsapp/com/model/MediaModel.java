package com.songsapp.com.model;


import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.songsapp.com.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MediaModel implements Parcelable {

    // Member variables.
    private JSONObject mediaObject;
    private int profileColor;

    public MediaModel(JSONObject mediaObject) {
        this.mediaObject = mediaObject;
        setProfileColor();
    }

    public JSONObject getMediaObject() {
        return mediaObject;
    }

    public String getArtworkImage() {
        return mediaObject.optString("artworkUrl100");
    }

    public String getMediaPreviewUrl() {
        return mediaObject.optString("previewUrl");
    }

    public String getTrackName() {
        return mediaObject.optString("trackName");
    }

    public String getOwnerName() {
        return mediaObject.optString("artistName");
    }

    public String getReleaseDate() {
        return mediaObject.optString("releaseDate");
    }

    public String getPrice() {
        return mediaObject.optString("trackPrice");
    }

    public long getDuration() {
        return mediaObject.optLong("trackTimeMillis");
    }

    public int getPlaceHolder() {
        if (isAudioMedia()) {
            return R.drawable.ic_music;
        } else {
            return R.drawable.ic_video_play;
        }
    }

    public boolean isAudioMedia() {
        return mediaObject.optString("kind").equals("song");
    }

    public void setProfileColor() {
        Random random = new Random();
        this.profileColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public int getProfileColor() {
        return profileColor != 0 ? profileColor : R.color.colorAccent;
    }

    protected MediaModel(Parcel in) {
        profileColor = in.readInt();
        try {
            mediaObject = in.readByte() == 0x00 ? null : new JSONObject(in.readString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<MediaModel> CREATOR = new Creator<MediaModel>() {
        @Override
        public MediaModel createFromParcel(Parcel in) {
            return new MediaModel(in);
        }

        @Override
        public MediaModel[] newArray(int size) {
            return new MediaModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(profileColor);
        if (mediaObject == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(mediaObject.toString());
        }
    }

}
