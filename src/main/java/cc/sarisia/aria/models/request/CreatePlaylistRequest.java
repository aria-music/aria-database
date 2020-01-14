package cc.sarisia.aria.models.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ToString
public class CreatePlaylistRequest {
    @NotBlank
    @Getter @Setter
    private String name;
}
