package cc.sarisia.aria.models.response;

import cc.sarisia.aria.models.Entry;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

public class SearchResult {
    @Getter @Setter
    private int hit;

    @Getter
    private int contains;

    @Getter
    private List<@Valid Entry> results;

    public void setResults(List<Entry> entries) {
        this.results = entries;
        this.contains = entries.size();
    }
}
