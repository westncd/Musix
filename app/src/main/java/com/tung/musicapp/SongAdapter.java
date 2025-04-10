package com.tung.musicapp;
import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
public class SongAdapter extends BaseAdapter {
    private Context context;
    private List<Song> songList;
    private DatabaseHelper dbHelper;
    private String userEmail;
    public SongAdapter(Context context, List<Song> songList, String userEmail) {
        this.context = context;
        this.songList = songList;
        this.dbHelper = new DatabaseHelper(context);
        this.userEmail = userEmail;
    }
    @Override
    public int getCount() {
        return songList.size();
    }
    @Override
    public Object getItem(int position) {
        return songList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return songList.get(position).getId();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        }
        Song song = songList.get(position);
        TextView songNameText = convertView.findViewById(R.id.song_name);
        TextView artistNameText = convertView.findViewById(R.id.artist_name);
        ImageButton btnAddToPlaylist = convertView.findViewById(R.id.btn_add_to_playlist);

        songNameText.setText(song.getName());
        artistNameText.setText(song.getArtist());

        btnAddToPlaylist.setOnClickListener(v -> {
            showAddToPlaylistDialog(song);
        });

        return convertView;
    }
    private void showAddToPlaylistDialog(Song song) {
        List<Playlist> playlists = dbHelper.getAllPlaylists(userEmail);
        if (playlists.isEmpty()) {
            Toast.makeText(context, "Bạn chưa có playlist nào", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] playlistNames = new String[playlists.size()];
        for (int i = 0; i < playlists.size(); i++) {
            playlistNames[i] = playlists.get(i).getPlaylistName();
        }
        Context darkContext = new ContextThemeWrapper(context, R.style.AlertDialogDarkTheme);
        new AlertDialog.Builder(darkContext)
                .setTitle("Thêm vào playlist")
                .setItems(playlistNames, (dialog, which) -> {
                    Playlist selected = playlists.get(which);
                    dbHelper.addSongToPlaylist(song.getId(), selected.getId());
                    Toast.makeText(context, "Đã thêm vào " + selected.getPlaylistName(), Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
