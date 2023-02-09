package com.freenow.apis.phrase.api.translation;

import com.freenow.apis.phrase.api.translation.dto.PhraseTranslationDTO;
import com.freenow.apis.phrase.domainobject.translation.PhraseTranslation;
import com.google.common.base.Preconditions;
import com.freenow.apis.phrase.api.GenericPhraseAPI;
import com.freenow.apis.phrase.exception.PhraseAppApiException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.freenow.apis.phrase.api.localedownload.PhraseLocaleDownloadAPI.DEFAULT_BRANCH;

/**
 * Created by m.winkelmann on 05.11.15.
 */
public class PhraseTranslationAPI extends GenericPhraseAPI<PhraseTranslationDTO[]>
{
    private static final Logger LOG = LoggerFactory.getLogger(PhraseTranslationAPI.class);

    // ---- configuration -----
    private static final String PLACEHOLDER_PROJECT_ID = "{projectid}";

    private static final String PLACEHOLDER_LOCALE_ID = "{localeid}";

    private static final String PHRASE_TRANSLATIONS_PATH =
        "/api/v2/projects/" + PLACEHOLDER_PROJECT_ID + "/locales/" + PLACEHOLDER_LOCALE_ID + "/translations";

    private static final String PARAMETER_BRANCH = "branch";


    protected PhraseTranslationAPI(final RestTemplate restTemplate, final String authToken)
    {
        super(restTemplate, authToken);
    }


    public PhraseTranslationAPI(
        final String authToken,
        final String scheme,
        final String host
    )
    {
        super(createRestTemplateWithConverter(), scheme, host, authToken);
    }


    public PhraseTranslationAPI(final String authToken)
    {
        super(createRestTemplateWithConverter(), authToken);
    }


    public List<PhraseTranslation> listTranslations(String projectId, String localeId) throws PhraseAppApiException
    {
        return listTranslations(projectId, DEFAULT_BRANCH, localeId);
    }


    public List<PhraseTranslation> listTranslations(String projectId, String branch, String localeId) throws PhraseAppApiException
    {
        Preconditions.checkNotNull(projectId, "ProjectId must not be null.");
        Preconditions.checkNotNull(localeId, "LocaleId must not be null.");
        LOG.trace("Start to retrieve translations for projectId: {} and localeId: {}", projectId, localeId);

        PhraseTranslationDTO[] requestedTranslations = null;
        try
        {
            String requestPath = createRequestPath(projectId, localeId);

            LOG.trace("Call requestPath: {} to get translations from phrase.", requestPath);

            final List<NameValuePair> parameters = new ArrayList<>();

            if (!DEFAULT_BRANCH.equals(branch))
            {
                parameters.add(new BasicNameValuePair(PARAMETER_BRANCH, branch));
            }

            final URIBuilder builder = createUriBuilder(requestPath, parameters);

            HttpEntity<Object> requestEntity = createHttpEntity(requestPath);

            URI uri = builder.build();

            ResponseEntity<PhraseTranslationDTO[]> responseEntity = requestPhrase(requestEntity, uri, PhraseTranslationDTO[].class);

            requestedTranslations = handleResponse(projectId, requestPath, responseEntity);
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException("Something goes wrong due building of the request URI", e);
        }

        LOG.trace("Successfully retrieved {} translations for projectId: {} and localeId: {}", requestedTranslations.length, projectId, localeId);
        return PhraseTranslationMapper.makePhraseTranslations(requestedTranslations);
    }


    private String createRequestPath(String projectId, String localeId)
    {
        Map<String, String> placeholders = new HashMap<String, String>();
        placeholders.put(PLACEHOLDER_PROJECT_ID, projectId);
        placeholders.put(PLACEHOLDER_LOCALE_ID, localeId);
        return createPath(PHRASE_TRANSLATIONS_PATH, placeholders);
    }

}
