package cc.sarisia.aria.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

public class BatchRequest<T> {
    @Size(min = 1)
    @Getter @Setter
    private List<@Valid T> entries;
}
