package datalab.digital.dereference.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RefIdIndividuumTemplate {

    @NotNull
    private String owner; //referencia na ciselnik vlastnikov
    @NotBlank
    private String template;
    @NotBlank
    private String name;
    @NotNull
    private String type; //referencia na zaregistrovany a schvaleny typ individua
    boolean isVersionable = false;
    @NotBlank
    private String redirectTemplate;
    private String description;
    @NotNull
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Set<RefIdIndividuumTemplateParam> allowedQueryParams = new HashSet<>();

}
