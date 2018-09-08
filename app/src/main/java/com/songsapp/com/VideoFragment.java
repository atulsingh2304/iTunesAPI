package com.songsapp.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.songsapp.com.common.adapters.ListAdapter;
import com.songsapp.com.common.interfaces.OnResponseListener;
import com.songsapp.com.common.utils.ApiRequestUtil;
import com.songsapp.com.common.utils.Constants;
import com.songsapp.com.model.MediaModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class VideoFragment extends Fragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    // Member variables.
    private Context mContext;
    private View rootView;
    private ListView mListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private List<MediaModel> mediaModelList;
    private ListAdapter listAdapter;
    private ApiRequestUtil apiRequestUtil;


    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getContext();
        mediaModelList = new ArrayList<>();
        apiRequestUtil = new ApiRequestUtil(mContext);

        rootView = inflater.inflate(R.layout.list_view_layout, container,false);

        // get views.
        getViews();

        listAdapter = new ListAdapter(mContext, R.layout.list_items, mediaModelList);
        mListView.setAdapter(listAdapter);
        makeRequest();
        return rootView;
    }

    private void getViews() {
        progressBar = rootView.findViewById(R.id.progressBar);
        mListView = rootView.findViewById(R.id.list_item_view);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mListView.setOnItemClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void makeRequest() {
        apiRequestUtil.getResponse(Constants.API_VIDEO_URL, new OnResponseListener() {

            @Override
            public void onSuccess(JSONObject response) {
                mediaModelList.clear();

                if (response.optJSONArray("results") != null) {
                    JSONArray resultsArray = response.optJSONArray("results");
                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject mediaObject = resultsArray.optJSONObject(i);
                        mediaModelList.add(new MediaModel(mediaObject));
                    }
                }

                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MediaModel mediaModel = mediaModelList.get(position);
        Intent intent = new Intent(mContext, ViewDetailsActivity.class);
        intent.putExtra(Constants.MEDIA_MODEL, mediaModel);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        /*
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                makeRequest();
            }
        });
    }
}
