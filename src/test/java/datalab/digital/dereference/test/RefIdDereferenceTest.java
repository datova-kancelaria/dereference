package datalab.digital.dereference.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import datalab.digital.dereference.model.RefIdIndividuumTemplate;
import datalab.digital.dereference.model.RefIdIndividuumTemplateParam;
import datalan.digital.dereference.DatabaseRepository;
import datalan.digital.dereference.MetaISDereferenceApplication;

@SpringBootTest(classes = { MetaISDereferenceApplication.class })
@AutoConfigureMockMvc
public class RefIdDereferenceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    DatabaseRepository databaseRepository;

    @Test
    public void refIdIndividuumBasicWithoutQueryParams() throws Exception {
        Mockito.when(databaseRepository.findRefIdIndividuumTemplate(Mockito.anyString()))
                .thenReturn(new RefIdIndividuumTemplate().setRedirectTemplate("http://finstat.sk/{id}").setTemplate("https://data.gov.sk/id/legal-subject/")
                        .setAllowedQueryParams(Set.of(new RefIdIndividuumTemplateParam("nieco1"), new RefIdIndividuumTemplateParam("nieco2"))));
        this.mockMvc.perform(get("/refid/id/legal-subject/00151742")).andDo(print()).andExpect(status().isMovedPermanently());
    }


    @Test
    public void refIdIndividuumBasicWithAllowedQueryParams() throws Exception {
        Mockito.when(databaseRepository.findRefIdIndividuumTemplate(Mockito.anyString()))
                .thenReturn(new RefIdIndividuumTemplate().setRedirectTemplate("http://finstat.sk/{id}").setTemplate("https://data.gov.sk/id/legal-subject/")
                        .setAllowedQueryParams(Set.of(new RefIdIndividuumTemplateParam("nieco1"), new RefIdIndividuumTemplateParam("nieco2"))));
        this.mockMvc.perform(get("/refid/id/legal-subject/00151742?nieco1=1&nieco2=2")).andDo(print()).andExpect(status().isMovedPermanently());
    }


    @Test
    public void refIdIndividuumBasicWithMoreThanAllowedQueryParams() throws Exception {
        Mockito.when(databaseRepository.findRefIdIndividuumTemplate(Mockito.anyString()))
                .thenReturn(new RefIdIndividuumTemplate().setRedirectTemplate("http://finstat.sk/{id}").setTemplate("https://data.gov.sk/id/legal-subject/")
                        .setAllowedQueryParams(Set.of(new RefIdIndividuumTemplateParam("nieco1"), new RefIdIndividuumTemplateParam("nieco2"))));
        this.mockMvc.perform(get("/refid/id/legal-subject/00151742?nieco1=1&nieco2=2&nieco3=3")).andDo(print()).andExpect(status().isMovedPermanently());
    }


    @Test
    public void refIdIndividuumUnknownTemplate() throws Exception {
        Mockito.when(databaseRepository.findRefIdIndividuumTemplate(Mockito.anyString())).thenReturn(null);
        this.mockMvc.perform(get("/refid/id/unknown-template/12345678")).andDo(print()).andExpect(status().isBadRequest());
    }


    @Test
    public void refIdOpenDataDataset() throws Exception {
        this.mockMvc.perform(get("/refid/set/dataset/register-adries-register-okresov")).andDo(print()).andExpect(status().isMovedPermanently())
                .andExpect(header().string(HttpHeaders.LOCATION, "https://data.gov.sk/dataset/register-adries-register-okresov"));
    }


    @Test
    public void refIdOntology() throws Exception {
        Mockito.when(databaseRepository.findRefIdDefEntity(Mockito.anyString())).thenReturn("57ef9690-b750-4ba7-b7c2-be37e2edd95f");

        this.mockMvc.perform(get("/refid/def/ontology/physical-person")).andDo(print()).andExpect(status().isMovedPermanently())
                .andExpect(header().string(HttpHeaders.LOCATION, "https://metais.vicepremier.gov.sk/uri/detail/57ef9690-b750-4ba7-b7c2-be37e2edd95f"));
    }


    @Test
    public void refIdOntologyDistribution() throws Exception {
        Mockito.when(databaseRepository.findRefIdDefEntity(Mockito.anyString())).thenReturn("7e53e318-0fe7-4ff1-9f41-7cbb43fc89ec");

        this.mockMvc.perform(get("/refid/def/ontology/physical-person/2016-09-04.owl")).andDo(print()).andExpect(status().isMovedPermanently())
                .andExpect(header().string(HttpHeaders.LOCATION, "https://metais.vicepremier.gov.sk/uri/detail/7e53e318-0fe7-4ff1-9f41-7cbb43fc89ec"));
    }


    @Test
    public void refIdCodelist() throws Exception {
        Mockito.when(databaseRepository.findCodelistIdentifier("CL030001")).thenReturn("3049");
        this.mockMvc.perform(get("/refid/set/codelist/CL030001")).andDo(print()).andExpect(status().isMovedPermanently())
                .andExpect(header().string(HttpHeaders.LOCATION, "https://metais.vicepremier.gov.sk/codelists/detail/3049"));
    }


    @Test
    public void refIdOntologyDataElement() throws Exception {
        Mockito.when(databaseRepository.findRefIdDefEntity("ontology/legal-subject/LegalSubject")).thenReturn("3945862b-c12c-4fa7-831a-812edef93fa8");
        this.mockMvc.perform(get("/refid/def/ontology/legal-subject/LegalSubject")).andDo(print()).andExpect(status().isMovedPermanently())
                .andExpect(header().string(HttpHeaders.LOCATION, "https://metais.vicepremier.gov.sk/uri/detail/3945862b-c12c-4fa7-831a-812edef93fa8"));
    }
}
