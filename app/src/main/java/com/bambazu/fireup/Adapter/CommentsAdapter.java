package com.bambazu.fireup.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bambazu.fireup.Model.Comments;
import com.bambazu.fireup.R;

import java.util.ArrayList;

/**
 * Created by Sil on 15/08/2015.
 */
public class CommentsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Comments> listComment;

    public CommentsAdapter(Context context, ArrayList<Comments> listComment){
        this.context = context;
        this.listComment = listComment;
    }

    @Override
    public int getCount() {
        return listComment.size();
    }

    @Override
    public Object getItem(int i) {
        return listComment.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = view;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_comment, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.user = (TextView) convertView.findViewById(R.id.user);
            holder.datePublish = (TextView) convertView.findViewById(R.id.datePublish);
            holder.postMsg = (TextView) convertView.findViewById(R.id.post);
            holder.ratingValue = (TextView) convertView.findViewById(R.id.ratingValue);

            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.user.setText(listComment.get(i).getNick());
        holder.datePublish.setText(listComment.get(i).getDate());
        holder.postMsg.setText(listComment.get(i).getPost());
        holder.ratingValue.setText(String.valueOf(listComment.get(i).getRating()));

        return convertView;
    }

    static class ViewHolder {
        TextView user;
        TextView datePublish;
        TextView postMsg;
        TextView ratingValue;
    }
}
