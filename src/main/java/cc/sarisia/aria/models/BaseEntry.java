package cc.sarisia.aria.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class BaseEntry {
    @NotBlank
    @Getter
    @Setter
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

    public String getThumbnail() {
        return this.thumbnail == null ? "" : this.thumbnail;
    }
}
