package cc.sarisia.aria.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class AddToPlaylistSQLParams {
    @Setter
    @Getter
    private int id;

    @Setter @Getter
    private String uri;

    @Setter @Getter
    private boolean thumbnail;
}
