package com.tung.musicapp.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.tung.musicapp.Models.Song;
import com.tung.musicapp.R;

import java.util.List;
public class PlaylistAdapter extends BaseAdapter {
    private Context context;
    private List<Song> songs;
    private LayoutInflater inflater;
    private OnSongActionListener actionListener;
    public interface OnSongActionListener {
        void onRemoveSong(Song song);
    }
    public PlaylistAdapter(Context context, List<Song> songs, OnSongActionListener actionListener) {
        this.context = context;
        this.songs = songs;
        this.actionListener = actionListener;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return songs.size();
    }
    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }
    @Override
    public long getItemId(int position) {
        return songs.get(position).getId();
    }
    static class ViewHolder {
        TextView songName;
        TextView artistName;
        ImageView menuButton;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Song song = songs.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_playlist_song, parent, false);
            holder = new ViewHolder();
            holder.songName = convertView.findViewById(R.id.song_name);
            holder.artistName = convertView.findViewById(R.id.artist_name);
            holder.menuButton = convertView.findViewById(R.id.menu_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.songName.setText(song.getName());
        holder.artistName.setText(song.getArtist());
        holder.menuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.inflate(R.menu.song_item_menu);
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_remove_song) {
                    if (actionListener != null) {
                        actionListener.onRemoveSong(song);
                    }
                    return true;
                }
                return false;
            });
            popup.show();
        });

        return convertView;
    }
}