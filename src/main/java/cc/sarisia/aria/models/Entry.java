package cc.sarisia.aria.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class Entry extends BaseEntry {
    @Getter @Setter
    private String meta;
}
