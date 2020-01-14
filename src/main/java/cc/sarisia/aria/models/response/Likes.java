package cc.sarisia.aria.models.response;

import cc.sarisia.aria.models.Entry;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Likes {
    @Getter @Setter
    private List<Entry> entries;
}
