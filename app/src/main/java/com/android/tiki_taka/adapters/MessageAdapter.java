package com.android.tiki_taka.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.dto.Message;
import com.android.tiki_taka.utils.ImageUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> messages;
    private static final int VIEW_TYPE_MESSAGE_SENT =1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(message.isSent()){
            return VIEW_TYPE_MESSAGE_SENT;
        }else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == VIEW_TYPE_MESSAGE_SENT){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.right_chat_message,parent,false);
            return new SentMessageViewHolder(view);
        }else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.left_chat_message, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if(holder.getItemViewType() == VIEW_TYPE_MESSAGE_SENT){
            ((SentMessageViewHolder)holder).bind(message);
        }else {
            ((ReceivedMessageViewHolder)holder).bind(message);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // Sent Message ViewHolder
    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImg;
        TextView messageBody, timeStamp;
        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.imageView_profile);
            messageBody = itemView.findViewById(R.id.textView_messageBody);
            timeStamp = itemView.findViewById(R.id.textView_timeStamp);
        }

        void bind(Message message){
            ImageUtils.loadImage(message.getProfileImageUrl(), profileImg, itemView.getContext());
            messageBody.setText(message.getContent());
            timeStamp.setText(message.getCreatedAt());
        }
    }

    // Received Message ViewHolder
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImg;
        TextView messageBody, timeStamp;
        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.imageView_profile);
            messageBody = itemView.findViewById(R.id.textView_messageBody);
            timeStamp = itemView.findViewById(R.id.textView_timeStamp);
        }

        void bind(Message message){
            ImageUtils.loadImage(message.getProfileImageUrl(), profileImg, itemView.getContext());
            messageBody.setText(message.getContent());
            timeStamp.setText(message.getCreatedAt());
        }
    }

    // 데이터를 설정하는 setData 메서드 추가
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Message> newData, int userId){
        messages.clear();

        //isSent 값은 어댑터 뷰의 바인딩 로직보다는, 데이터를 처리하는 로직에서 수행하는 것이 더 적절함
        // 서버로부터 데이터를 받아와서 리스트에 추가될 때 설정
        for (Message message: newData){
            boolean isSent = message.getSenderId() == userId;
            message.setSent(isSent);
            messages.add(message);
        }
        notifyDataSetChanged(); // 어댑터에 데이터가 변경되었음을 알림
    }

}
