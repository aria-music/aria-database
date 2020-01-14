package cc.sarisia.aria.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class DeleteFromPlaylistRequest {
    @NotBlank
    @Getter @Setter
    private String uri;
}
