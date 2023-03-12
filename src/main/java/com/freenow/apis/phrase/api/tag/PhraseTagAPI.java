package com.freenow.apis.phrase.api.tag;

import com.freenow.apis.phrase.api.GenericPhraseAPI;
import com.freenow.apis.phrase.api.tag.dto.PhraseTagWithStatsDTO;
import com.freenow.apis.phrase.exception.PhraseAppApiException;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.freenow.apis.phrase.api.localedownload.PhraseLocaleDownloadAPI.DEFAULT_BRANCH;

public class PhraseTagAPI extends GenericPhraseAPI<PhraseTagWithStatsDTO>
{
    private static final Logger LOG = LoggerFactory.getLogger(PhraseTagAPI.class);

    // ---- configuration -----
    private static final String PLACEHOLDER_PROJECT_ID = "{projectid}";

    private static final String PLACEHOLDER_TAG_NAME = "{tagName}";

    private static final String PHRASE_TRANSLATIONS_PATH = "/api/v2/projects/" + PLACEHOLDER_PROJECT_ID + "/tags/" + PLACEHOLDER_TAG_NAME;

    private static final String PARAMETER_BRANCH = "branch";


    protected PhraseTagAPI(final RestTemplate restTemplate, final String authToken)
    {
        super(restTemplate, authToken);
    }


    public PhraseTagAPI(final String authToken, final String scheme, final String host)
    {
        super(createRestTemplateWithConverter(), scheme, host, authToken);
    }


    public PhraseTagAPI(final String authToken)
    {
        super(createRestTemplateWithConverter(), authToken);
    }


    public PhraseTagWithStatsDTO getSingleTag(String projectId, String tagName) throws PhraseAppApiException
    {
        return getSingleTag(projectId, DEFAULT_BRANCH, tagName);
    }


    public PhraseTagWithStatsDTO getSingleTag(final String projectId, final String branch, final String tagName) throws PhraseAppApiException
    {
        Preconditions.checkNotNull(projectId, "ProjectId must not be null.");
        Preconditions.checkNotNull(tagName, "Tag name must not be null.");
        LOG.trace("Start to retrieve tag for projectId: {} and name: {}", projectId, tagName);

        try
        {
            final String requestPath = createRequestPath(projectId, tagName);

            LOG.trace("Call requestPath: {} to get tag from phrase.", requestPath);

            final List<NameValuePair> parameters;
            if (!DEFAULT_BRANCH.equals(branch))
            {
                parameters = new ArrayList<>(1);
                parameters.add(new BasicNameValuePair(PARAMETER_BRANCH, branch));
            }
            else
            {
                parameters = Collections.emptyList();
            }

            final URI uri = createUriBuilder(requestPath, parameters)
                .build();

            final HttpEntity<Object> requestEntity = createHttpEntity(requestPath);

            final ResponseEntity<PhraseTagWithStatsDTO> responseEntity = requestPhrase(
                requestEntity,
                uri,
                PhraseTagWithStatsDTO.class);

            return handleResponse(projectId, requestPath, responseEntity);
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException("Something goes wrong due building of the request URI", e);
        }
    }


    private String createRequestPath(final String projectId, final String tagName)
    {
        Map<String, String> placeholders = new HashMap<String, String>();
        placeholders.put(PLACEHOLDER_PROJECT_ID, projectId);
        placeholders.put(PLACEHOLDER_TAG_NAME, tagName);
        return createPath(PHRASE_TRANSLATIONS_PATH, placeholders);
    }

}
