package cc.sarisia.aria.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class Playlist {
    @Getter @Setter
    private int id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private int ownerId;

    @Getter @Setter
    private int groupId;

    @Getter @Setter
    private int length;

    @Getter @Setter
    private List<String> thumbnails;

    @Getter
    private List<Entry> entries;

    @Getter @Setter
    private List<String> unknownURIs;

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
        this.length = entries.size();
    }
}
