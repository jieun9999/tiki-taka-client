package com.android.tiki_taka.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.listeners.FolderSelectListener;
import com.android.tiki_taka.listeners.ItemClickListener;
import com.android.tiki_taka.models.dto.StoryFolder;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.TimeUtils;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.util.List;

public class ChangeFolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int IMAGE_TYPE = 1;
    private static final int TEXT_TYPE = 2;

    private List<StoryFolder> storyFolders;
    private FolderSelectListener folderSelectListener;

    public ChangeFolderAdapter(List<StoryFolder> storyFolders, FolderSelectListener folderSelectListener) {
        this.storyFolders = storyFolders;
        this.folderSelectListener = folderSelectListener;
    }

    //정적 팩토리 메서드
    public static ChangeFolderAdapter withFolderSelection(List<StoryFolder> storyFolders, FolderSelectListener folderSelectListener){
        return new ChangeFolderAdapter(storyFolders, folderSelectListener);
    }

    @Override
    public int getItemViewType(int position) {
        StoryFolder storyFolderDto = storyFolders.get(position);
        switch (storyFolderDto.getDataType()){
            case "image" :
                return IMAGE_TYPE;
            case "text" :
                return TEXT_TYPE;
            default :
                return -1;
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView folderImageView;
        TextView folderTitle;
        RadioButton radioButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            folderImageView = itemView.findViewById(R.id.folderImageView);
            folderTitle = itemView.findViewById(R.id.folderTitle);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder{
        TextView folderTextView;
        RadioButton radioButton;

        public TextViewHolder(View itemView){
            super(itemView);
            folderTextView = itemView.findViewById(R.id.folderTextView);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == IMAGE_TYPE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_select_folder, parent, false);
            return new ChangeFolderAdapter.ImageViewHolder(view);

        } else if (viewType == TEXT_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_select_folder, parent, false);
            return new ChangeFolderAdapter.TextViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StoryFolder folder = storyFolders.get(position);

        if(holder.getItemViewType() == IMAGE_TYPE){
            ChangeFolderAdapter.ImageViewHolder imageViewHolder = (ChangeFolderAdapter.ImageViewHolder) holder;
            imageViewHolder.folderTitle.setText(folder.getTitle());
            ImageUtils.loadImage(folder.getDisplayImage(),imageViewHolder.folderImageView, imageViewHolder.itemView.getContext());

        } else if (holder.getItemViewType() == TEXT_TYPE) {
            ChangeFolderAdapter.TextViewHolder textViewHolder = (ChangeFolderAdapter.TextViewHolder) holder;
            textViewHolder.folderTextView.setText(folder.getTitle());

        }

        holder.itemView.findViewById(R.id.radioButton).setOnClickListener( v -> folderSelectListener.onFolderItemSelect(position));
    }

    @Override
    public int getItemCount() {
        return storyFolders.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<StoryFolder> newData){
        storyFolders.clear(); // 기존 데이터를 모두 제거
        storyFolders.addAll(newData); // 새 데이터를 추가
        notifyDataSetChanged(); // 어댑터에 데이터가 변경되었음을 알림
    }

}
