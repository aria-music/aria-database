package cc.sarisia.aria.models.request;

import cc.sarisia.aria.models.GPMEntry;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

public class UpdateGPMRequest {
    @Size(min = 1)
    @Getter @Setter
    private List<@Valid GPMEntry> entries;
}
