package com.tung.musicapp;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
public class LibraryAdapter extends BaseAdapter {
    private Context context;
    private List<Playlist> playlists;
    private String userEmail, userName, userRole;
    public LibraryAdapter(Context context, List<Playlist> playlists, String userEmail, String userName, String userRole) {
        this.context = context;
        this.playlists = playlists;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userRole = userRole;
    }
    @Override
    public int getCount() {
        return playlists.size();
    }
    @Override
    public Object getItem(int position) {
        return playlists.get(position);
    }
    @Override
    public long getItemId(int position) {
        return playlists.get(position).getId();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_library, parent, false);
        }
        TextView songCountText = convertView.findViewById(R.id.song_count);
        Playlist playlist = playlists.get(position);
        TextView playlistNameText = convertView.findViewById(R.id.playlist_name);
        playlistNameText.setText(playlist.getPlaylistName());
        songCountText.setText(playlist.getSongCount() + " bài hát");
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlaylistActivity.class);
            intent.putExtra("playlist_id", String.valueOf(playlist.getId()));
            intent.putExtra("playlist_name", playlist.getPlaylistName());
            intent.putExtra("user_email", userEmail);
            intent.putExtra("user_name", userName);
            intent.putExtra("user_role", userRole);
            context.startActivity(intent);
        });
        return convertView;
    }
}