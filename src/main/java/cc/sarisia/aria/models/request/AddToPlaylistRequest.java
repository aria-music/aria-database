package cc.sarisia.aria.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class AddToPlaylistRequest {
    @Size(min = 1) @NotNull
    @Setter @Getter
    private List<String> uris;
}

