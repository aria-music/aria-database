package cc.sarisia.aria.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class GPMEntry {
    @NotBlank
    @Getter @Setter
    private String uri;

    @NotBlank
    @Getter @Setter
    private String gpmUser;

    @NotBlank
    @Getter @Setter
    private String id;

    @NotBlank
    @Getter @Setter
    private String title;

    @Setter
    private String artist;

    @Setter
    private String album;

    @Getter
    private String thumbnail;

    @Getter @Setter
    private String thumbnailSmall;

    public String getArtist() {
        return this.artist == null ? "" : this.artist;
    }

    public String getAlbum() {
        return this.album == null ? "" : this.album;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        this.setThumbnailSmall(thumbnail+"=s158-c-e100-rwu-v1");
    }

    public String toFullTitle() {
        return this.title + " - " + this.artist + " - " + this.album;
    }

    public Entry toEntry() {
        var entry = new Entry();
        entry.setUri(this.getUri());
        entry.setProvider("gpm");
        entry.setTitle(this.toFullTitle());
        entry.setThumbnail(this.getThumbnail());
        return entry;
    }
}
