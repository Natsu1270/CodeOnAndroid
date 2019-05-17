package com.example.codeonandroid.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.codeonandroid.R;
import com.example.codeonandroid.User;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private List<User> users;
    private int layout;

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    public UserAdapter(Context context,int layout,List<User> users) {
        this.context = context;
        this.users = users;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(layout,null);

        TextView leader_name = convertView.findViewById(R.id.lead_name);
        ImageView leader_avatar = convertView.findViewById(R.id.lead_avatar);
        TextView leader_xp = convertView.findViewById(R.id.exp_count);
        User user = users.get(position);

        leader_name.setText(user.getName());
        leader_xp.setText(Integer.toString(user.getExp()));

        if(user.getAvatar().equals("empty")){
            leader_avatar.setImageResource(R.drawable.people);
        }else{
            new DownloadImageTask(leader_avatar).execute(user.getAvatar());
        }

        return convertView;
    }
}
