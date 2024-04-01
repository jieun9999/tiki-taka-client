package com.android.tiki_taka.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.listeners.DateMarkerListener;
import com.android.tiki_taka.models.dto.Message;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.TimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> messages;
    private static final int VIEW_TYPE_MESSAGE_SENT =1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_DATE_MARKER =3;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);

        if(message.getDateMarker() == 1){
            return VIEW_DATE_MARKER;
        } else if (message.isSent()) {
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
        }else if(viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.left_chat_message, parent, false);
            return new ReceivedMessageViewHolder(view);
        }else if(viewType == VIEW_DATE_MARKER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_date_bar, parent, false);
            return new DateMarkerViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if(holder.getItemViewType() == VIEW_TYPE_MESSAGE_SENT){
            ((SentMessageViewHolder)holder).bind(message);
        }else if(holder.getItemViewType() == VIEW_TYPE_MESSAGE_RECEIVED){
            ((ReceivedMessageViewHolder)holder).bind(message);
        }else if(holder.getItemViewType() == VIEW_DATE_MARKER) {
            ((DateMarkerViewHolder)holder).bind(message);
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
        ImageView heartIcon;
        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.imageView_profile);
            messageBody = itemView.findViewById(R.id.textView_messageBody);
            timeStamp = itemView.findViewById(R.id.textView_timeStamp);
            heartIcon = itemView.findViewById(R.id.imageView32);
        }

        void bind(Message message){
            ImageUtils.loadImage(message.getProfileImageUrl(), profileImg, itemView.getContext());
            messageBody.setText(message.getContent());
            timeStamp.setText(TimeUtils.convertToAmPm(message.getCreatedAt()));
            if (message.getIsRead() == 1) {
                heartIcon.setVisibility(View.GONE);
            } else {
                heartIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    // Received Message ViewHolder
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImg;
        TextView messageBody, timeStamp;
        ImageView heartIcon;
        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.imageView_profile);
            messageBody = itemView.findViewById(R.id.textView_messageBody);
            timeStamp = itemView.findViewById(R.id.textView_timeStamp);
            heartIcon = itemView.findViewById(R.id.imageView32);
        }

        void bind(Message message){
            ImageUtils.loadImage(message.getProfileImageUrl(), profileImg, itemView.getContext());
            messageBody.setText(message.getContent());
            timeStamp.setText(TimeUtils.convertToAmPm(message.getCreatedAt()));
            if (message.getIsRead() == 1) {
                heartIcon.setVisibility(View.GONE);
            } else {
                heartIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    //Date Marker ViewHolder
    static class DateMarkerViewHolder extends RecyclerView.ViewHolder{
        TextView dateMarker;
        public DateMarkerViewHolder(@NonNull View itemView) {
            super(itemView);
            dateMarker = itemView.findViewById(R.id.dateText);

        }

        void bind(Message message){
            dateMarker.setText(message.getContent());
        }
    }

    // 데이터를 처음 설정하는 setData 메서드 추가
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Message> newData, int userId){
        messages.clear();

        //isSent 값은 어댑터 뷰의 바인딩 로직보다는, 데이터를 처리하는 로직에서 수행하는 것이 더 적절함
        // 서버로부터 데이터를 받아와서 리스트에 추가될 때 설정
        for (Message message: newData){
            if(message.getDateMarker() == 0){
                boolean isSent = message.getSenderId() == userId;
                message.setSent(isSent);
            }
            messages.add(message);
        }
        notifyDataSetChanged(); // 어댑터에 데이터가 변경되었음을 알림
    }

    public void addMessage(Message newMessage, int userId, int roomId, DateMarkerListener dateMarkerListener){
        // 뷰타입 설정
        boolean isSent = newMessage.getSenderId() == userId;
        newMessage.setSent(isSent);
        //화면에 띄우자 마자 상대방 메세지 읽었기 때문에 1 없어짐
        newMessage.setIsRead(1);

        if(!messages.isEmpty()){
            // 마지막 메세지와 날짜 비교
            Message lastMessage = messages.get(messages.size() -1);

            // 날짜가 다르면, 날짜 뷰 표시 로직 구현
            String newMessageDateWithoutTime = TimeUtils.getDateWithoutTime(newMessage.getCreatedAt());
            String lastMessageDateWithoutTime = TimeUtils.getDateWithoutTime(lastMessage.getCreatedAt());// null이 나옴 , 12:27 AM

            if(!newMessageDateWithoutTime.equals(lastMessageDateWithoutTime)){
                Message dateMarker = new Message(newMessageDateWithoutTime, roomId);
                messages.add(dateMarker);
                if(dateMarkerListener != null && newMessage.isSent()){
                    dateMarkerListener.onMessageAdded(dateMarker); // UI 업데이트 후 서버에 데이터 전송

                }
            }
        }

        // 객체 속성은 추가할때는 건들이지 않고, 뷰 바인딩에서 수정
        messages.add(newMessage);
        notifyItemInserted(messages.size() -1);
    }

    public int getMessageIdAtPosition(int position){
        if(position >= 0 && position< messages.size()){
            return messages.get(position).getMessageId();
        }else {
            return -1;
        }
    }

    // 가장 마지막에 읽은 메세지 까지 UI 업데이트
    // 메시지의 '읽음' 상태를 업데이트하는 로직과 UI 업데이트를 분리하여 처리
    public void setRead(int messageId, int readerId){
        boolean hasUpdate = false;
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            if(message.getMessageId() <= messageId && message.getSenderId() != readerId){
                message.setIsRead(1);
                hasUpdate = true;
            }
        }

        if(hasUpdate){
            // 전체 목록에 대해 UI 업데이트 알림
            notifyItemRangeChanged(0, messages.size());
        }
    }

}
