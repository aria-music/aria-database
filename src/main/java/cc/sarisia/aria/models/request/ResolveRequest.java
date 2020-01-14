package cc.sarisia.aria.models.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ToString
public class ResolveRequest {
    @NotBlank
    @Getter @Setter
    private String uri;
}
