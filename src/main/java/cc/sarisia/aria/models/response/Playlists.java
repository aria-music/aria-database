package cc.sarisia.aria.models.response;

import cc.sarisia.aria.models.Playlist;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Playlists {
    @Setter @Getter
    private List<Playlist> playlists;
}
