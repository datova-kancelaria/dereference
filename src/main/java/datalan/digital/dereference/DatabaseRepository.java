package datalan.digital.dereference;

import org.springframework.stereotype.Service;

import datalab.digital.dereference.model.RefIdIndividuumTemplate;

@Service
public class DatabaseRepository {

    public RefIdIndividuumTemplate findRefIdIndividuumTemplate(String path) {
        // TODO query do MetaIS DB -> WHERE RefIdIndividuumTemplate.template = path AND (RefIdIndividuumTemplate.validTo is null OR RefIdIndividuumTemplate.validTo < LocalDate.now())
        return null;
    }

    
    public String findCodelistIdentifier(String codelistCode) {
        // TODO query do MetaIS DB
        return null;
    }
    
    public String findOpenDataDatasetIdentifier(String datasetIdentifier) {
        // TODO query do MetaIS DB
        return null;
    }
    
    
    public String findRefIdDefEntity(String referenceIdentifier) {
        // TODO query do MetaIS DB
        return null;
    }
    
}
