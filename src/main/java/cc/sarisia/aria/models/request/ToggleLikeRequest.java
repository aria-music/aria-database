package cc.sarisia.aria.models.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.CodePointLength;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ToggleLikeRequest {
    @NotNull
    @Getter @Setter
    private Boolean like;

    @Size(min = 1)
    @Getter @Setter
    private String uri;
}
