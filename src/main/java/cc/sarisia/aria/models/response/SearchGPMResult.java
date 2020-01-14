package cc.sarisia.aria.models.response;

import cc.sarisia.aria.models.GPMEntry;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import javax.validation.Valid;
import java.util.List;

public class SearchGPMResult {
    @Getter @Setter
    private List<@Valid GPMEntry> results;
}
