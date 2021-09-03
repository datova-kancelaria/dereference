package datalan.digital.dereference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriTemplate;

import datalab.digital.dereference.model.RefIdIndividuumTemplate;
import datalab.digital.dereference.model.RefIdIndividuumTemplateParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(MetaISDereferenceController.RESOURCE)
@Slf4j
public class MetaISDereferenceController {

    public static final String RESOURCE = "/refid";
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private DatabaseRepository databaseRepository;
    private static final String REFID_ID_TEMPLATE = "https://data.gov.sk/id/";
    private static final String REFID_DOC_TEMPLATE = "https://data.gov.sk/doc/";
    private static final String REFID_SET_TEMPLATE = "https://data.gov.sk/set/";
    private static final String REFID_DEF_TEMPLATE = "https://data.gov.sk/def/";
    private static final String META_IS_DETAIL_TEMPLATE = "https://metais.vicepremier.gov.sk/uri/detail/{id}";
    private static final String META_IS_CODELIST_DETAIL = "https://metais.vicepremier.gov.sk/codelists/detail/{id}";
    private static final String OPENDATA_DATASET_DETAIL = "https://data.gov.sk/dataset/{id}";
    private static final String ZNALOSTI_GOV_SK_DETAIL_TEMPLATE = "https://znalosti.gov.sk/resource?uri={id}";

    @RequestMapping(value = "/def/**", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity redirectDef() {
        String path = request.getRequestURI().substring("/refid/def/".length());
        String entityIdentifier = databaseRepository.findRefIdDefEntity(REFID_DEF_TEMPLATE + path);
        if (entityIdentifier == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UriTemplate uriTemplate = null;
        Map<String, String> uriVariables = new HashMap<String, String>();

        //hardcoded redirect to znalosti.gov.sk
        if (request.getContentType() != null && "application/ld+json".equals(request.getContentType())
                || "application/rdf+xml".equals(request.getContentType())) {
            uriTemplate = new UriTemplate(ZNALOSTI_GOV_SK_DETAIL_TEMPLATE);
            uriVariables.put("id", REFID_DEF_TEMPLATE + path);
        } else {
            uriTemplate = new UriTemplate(META_IS_DETAIL_TEMPLATE);
            uriVariables.put("id", entityIdentifier);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriTemplate.expand(uriVariables));
        try {
            if (request.getContentType() != null) {
                headers.setContentType(MediaType.parseMediaType(request.getContentType()));
            }
        } catch (InvalidMediaTypeException ex) {
            log.info("Unknown media type {} for requested URI {}. Error code message {} ", request.getContentType(), request.getRequestURI(), ex.getMessage(),
                    ex);
        }
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);

    }


    @RequestMapping(value = "/set/**", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity redirectSet() {

        //specialne handlovanie /set/codelist/{ID_CISELNIKA} tak specialne na MetaIS
        //specialne handlovanie /set/dataset/{ID_datasetu na data.gov.sk} tak specialne na MetaIS
        //specialne handlovanie /set/taxonomy/{rovno na znalosti.sk} 

        String path = request.getRequestURI().substring("/refid/set/".length());

        if (!path.contains("/")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UriTemplate uriTemplate = null;
        Map<String, String> uriVariables = new HashMap<String, String>();

        String type = path.substring(0, path.indexOf("/"));
        String uriIdentifier = path.substring(path.indexOf("/") + 1);

        String metaIsPath = null;
        String entityIdentifier = null;

        if ("codelist".equalsIgnoreCase(type)) {
            metaIsPath = META_IS_CODELIST_DETAIL;
            entityIdentifier = databaseRepository.findCodelistIdentifier(uriIdentifier);
        } else if ("dataset".equalsIgnoreCase(type)) {
            metaIsPath = OPENDATA_DATASET_DETAIL;
            entityIdentifier = uriIdentifier;
        } else if ("taxonomy".equalsIgnoreCase(type)) {
            metaIsPath = ZNALOSTI_GOV_SK_DETAIL_TEMPLATE;
            entityIdentifier =  REFID_SET_TEMPLATE + path;
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //hardcoded redirect to znalosti.gov.sk
        if (request.getContentType() != null && "application/ld+json".equals(request.getContentType())
                || "application/rdf+xml".equals(request.getContentType())) {
            uriTemplate = new UriTemplate(ZNALOSTI_GOV_SK_DETAIL_TEMPLATE);
            uriVariables.put("id", REFID_SET_TEMPLATE + path);
        } else {
            uriTemplate = new UriTemplate(metaIsPath);
            uriVariables.put("id", entityIdentifier);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriTemplate.expand(uriVariables));
        try {
            if (request.getContentType() != null) {
                headers.setContentType(MediaType.parseMediaType(request.getContentType()));
            }
        } catch (InvalidMediaTypeException ex) {
            log.info("Unknown media type {} for requested URI {}. Error code message {} ", request.getContentType(), request.getRequestURI(), ex.getMessage(),
                    ex);
        }
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }


    @RequestMapping(value = "/id/**", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity redirectId() {
        //TODO ak ziadna neexistuje tak nech sa zobrazi nejaka defaultna stranka ze nie je zaregistrovane???

        String path = request.getRequestURI().substring("/refid/id/".length());
        RefIdIndividuumTemplate refIdTemplate = databaseRepository.findRefIdIndividuumTemplate(REFID_ID_TEMPLATE + path);
        if (refIdTemplate == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        UriTemplate uriTemplate = new UriTemplate(refIdTemplate.getRedirectTemplate());

        path = path.substring(refIdTemplate.getTemplate().substring(REFID_ID_TEMPLATE.length()).indexOf("/") + 1);
        Map<String, String> uriVariables = new HashMap<String, String>();
        if (path.contains("/")) {
            path = path.substring(0, path.indexOf("/"));
            uriVariables.put("id", path.substring(0, path.indexOf("/")));
        } else {
            uriVariables.put("id", path);
        }
        if (refIdTemplate.isVersionable() && !path.contains("/")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (refIdTemplate.isVersionable()) {
            uriVariables.put("version", uriVariables.get("id").substring(0, path.indexOf("/")));
        }
        for (Entry<String, String[]> param : request.getParameterMap().entrySet()) {
            if (refIdTemplate.getAllowedQueryParams() != null
                    && refIdTemplate.getAllowedQueryParams().contains(new RefIdIndividuumTemplateParam(param.getKey()))) {
                uriVariables.put(param.getKey(), param.getValue()[0]);//TODO musime standardizovat ze ci dovolime aj array ako query param???
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriTemplate.expand(uriVariables));
        Iterator<String> headerIt = request.getHeaderNames().asIterator();
        while (headerIt.hasNext()) {
            String headerName = headerIt.next();
            headers.set(headerName, request.getHeader(headerName));
        }

        try {
            if (request.getInputStream() != null) {
                InputStreamResource inputStreamResource = new InputStreamResource(request.getInputStream());
                headers.setContentLength(request.getContentLengthLong());
                return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.MOVED_PERMANENTLY);
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }


    @RequestMapping(value = "/doc/**", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity redirectDoc() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
