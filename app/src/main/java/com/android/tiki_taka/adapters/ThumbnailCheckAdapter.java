package com.android.tiki_taka.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.listeners.ThumbnailStringUpdateListener;
import com.android.tiki_taka.listeners.ThumbnailUriUpdateLister;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.UriUtils;
import com.android.tiki_taka.utils.VideoUtils;

import java.util.ArrayList;

public class ThumbnailCheckAdapter extends RecyclerView.Adapter<ThumbnailCheckAdapter.ViewHolder> {
    private ArrayList<Uri> uriList;
    private ArrayList<String> urlList;
    private Context context;
    private int currentPosition = -1; // 어댑터 수준에서 현재 선택된 위치 관리
    private ThumbnailStringUpdateListener thumbnailStringUpdateListener;
    private ThumbnailUriUpdateLister thumbnailUriUpdateLister;

    private ThumbnailCheckAdapter() {
    }

    public static ThumbnailCheckAdapter ThumbnailCheckAdapterFromUri(ArrayList<Uri> uriList, Context context, ThumbnailUriUpdateLister thumbnailUriUpdateLister) {
        ThumbnailCheckAdapter adapter = new ThumbnailCheckAdapter();
        adapter.uriList = uriList;
        adapter.context = context;
        adapter.thumbnailUriUpdateLister =thumbnailUriUpdateLister;

        if(!uriList.isEmpty()){
            adapter.currentPosition = 0; // 초기 체크박스 설정
        }
        return adapter;
    }

    public static ThumbnailCheckAdapter ThumbnailCheckAdapterFromString(ArrayList<String> urlList, Context context,  ThumbnailStringUpdateListener thumbnailStringUpdateListener) {
        ThumbnailCheckAdapter adapter = new ThumbnailCheckAdapter();
        adapter.urlList = urlList;
        adapter.context = context;
        adapter.thumbnailStringUpdateListener = thumbnailStringUpdateListener;

        if(!urlList.isEmpty()){
            adapter.currentPosition = 0; // 초기 체크박스 설정
        }
        return adapter;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_check_story_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    // checkBox 클릭 리스너는 onBindViewHolder에서 설정
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(uriList != null){
            Uri uri = uriList.get(position);
            if(UriUtils.isImageUri(uri, context)){
                ImageUtils.loadImage(String.valueOf(uri), holder.imageView, context);
            } else if (UriUtils.isVideoUri(uri, context)) {
                VideoUtils.loadVideoThumbnail(context, uri, holder.imageView);

            }

            //  단일 선택만 가능한 체크박스를 구현
            holder.checkBox.setChecked(position == currentPosition); // 현재 위치가 선택된 위치와 같다면 체크 박스 선택
            holder.checkBox.setOnClickListener(v -> {
                if (holder.checkBox.isChecked()) {
                    int previousPosition = currentPosition;
                    currentPosition = position;
                    notifyItemChanged(previousPosition); // 이전 선택 해제
                    notifyItemChanged(currentPosition); // 현재 선택
                    // 썸네일 업데이트 콜백 호출
                    if(thumbnailUriUpdateLister != null){
                        thumbnailUriUpdateLister.onUpdateThumbnail(uri);
                    }
                } else {
                    currentPosition = -1; // 선택 해제
                }
            });

        } else if (urlList != null) {
            String url = urlList.get(position);
            ImageUtils.loadImage(url, holder.imageView, context);

            //  단일 선택만 가능한 체크박스를 구현
            holder.checkBox.setChecked(position == currentPosition); // 현재 위치가 선택된 위치와 같다면 체크 박스 선택
            holder.checkBox.setOnClickListener(v -> {
                if (holder.checkBox.isChecked()) {
                    int previousPosition = currentPosition;
                    currentPosition = position;
                    notifyItemChanged(previousPosition); // 이전 선택 해제
                    notifyItemChanged(currentPosition); // 현재 선택
                    // 썸네일 업데이트 콜백 호출
                    if(thumbnailStringUpdateListener != null){
                        thumbnailStringUpdateListener.onUpdateThumbnail(url);
                    }
                } else {
                    currentPosition = -1; // 선택 해제
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(uriList != null) {
            return uriList.size();
        }else {
            return urlList.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
