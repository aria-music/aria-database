package cc.sarisia.aria.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class Entry {
    @NotBlank
    @Getter @Setter
    private String uri;

    @NotBlank
    @Getter @Setter
    private String provider;

    @NotBlank
    @Getter @Setter
    private String title;

    @Setter
    private String thumbnail;

    @Getter @Setter
    private boolean liked;

    @Getter @Setter
    private String meta;

    public String getThumbnail() {
        return this.thumbnail == null ? "" : this.thumbnail;
    }
}
