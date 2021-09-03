package datalab.digital.dereference.model;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "name" })
public class RefIdIndividuumTemplateParam {

    public RefIdIndividuumTemplateParam(@NotBlank String name) {
        this.name = name;
    }

    @NotBlank
    private String name;
    private String description;

}
