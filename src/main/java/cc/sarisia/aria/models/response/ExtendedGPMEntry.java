package cc.sarisia.aria.models.response;

import cc.sarisia.aria.models.BaseEntry;
import cc.sarisia.aria.models.GPMEntry;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;

@NoArgsConstructor
public class ExtendedGPMEntry extends BaseEntry {
    @Valid
    @Getter @Setter
    private GPMEntry meta;

    public ExtendedGPMEntry(GPMEntry gpm) {
        var base = gpm.toEntry();
        this.setUri(base.getUri());
        this.setProvider(base.getProvider());
        this.setTitle(base.getTitle());
        this.setThumbnail(base.getThumbnail());
        this.setLiked(base.isLiked());
        this.setMeta(gpm);
    }
}
