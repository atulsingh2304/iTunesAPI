package com.songsapp.com.common.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.songsapp.com.R;
import com.songsapp.com.common.ui.CircularImageView;
import com.songsapp.com.model.MediaModel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ListAdapter extends ArrayAdapter<MediaModel> {

    private Context mContext;
    private List<MediaModel> mediaModelList;
    private int mLayoutResID;
    private View mRootView;

    /**
     * Public constructor to add data to the list
     *
     * @param context          Context of calling class.
     * @param layoutResourceID Layout resource id which is going to inflate on ListView
     * @param listItem         ListItems which are added to the inflating layout
     */
    public ListAdapter(Context context, int layoutResourceID, List<MediaModel> listItem) {

        super(context, layoutResourceID, listItem);
        this.mContext = context;
        this.mLayoutResID = layoutResourceID;
        this.mediaModelList = listItem;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        mRootView = convertView;
        final ListItemHolder listItemHolder;

        if (mRootView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemHolder = new ListItemHolder();
            mRootView = inflater.inflate(mLayoutResID, parent, false);
            listItemHolder.tvTitle = mRootView.findViewById(R.id.contentTitle);
            listItemHolder.ivProfile = mRootView.findViewById(R.id.contentImage);
            mRootView.setTag(listItemHolder);

        } else {
            listItemHolder = (ListItemHolder) mRootView.getTag();
        }

        // Showing data into list.
        MediaModel mediaModel = mediaModelList.get(position);
        if (mediaModel.getArtworkImage() != null && !mediaModel.getArtworkImage().isEmpty()) {
            listItemHolder.ivProfile.hideText();
            int placeHolder = mediaModel.getPlaceHolder();
            Picasso.with(mContext)
                    .load(mediaModel.getArtworkImage())
                    .placeholder(placeHolder)
                    .error(placeHolder)
                    .resizeDimen(R.dimen.user_image_width_height,
                            R.dimen.user_image_width_height)
                    .into(listItemHolder.ivProfile);
        } else {
            listItemHolder.ivProfile.showText(mediaModel.getTrackName(), mediaModel.getProfileColor());
        }

        listItemHolder.tvTitle.setText(mediaModel.getTrackName());

        return mRootView;
    }

    @Override
    public int getCount() {
        return mediaModelList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private static class ListItemHolder {

        TextView tvTitle;
        CircularImageView ivProfile;
    }

}
