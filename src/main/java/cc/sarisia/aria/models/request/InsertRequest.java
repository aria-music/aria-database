package cc.sarisia.aria.models.request;

import cc.sarisia.aria.models.Entry;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@ToString
public class InsertRequest {
    @Size(min = 1) @NotNull
    @Getter @Setter
    private List<Entry> entries;
}
