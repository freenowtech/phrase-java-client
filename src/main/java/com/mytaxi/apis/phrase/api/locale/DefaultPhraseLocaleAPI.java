package com.mytaxi.apis.phrase.api.locale;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.mytaxi.apis.phrase.api.GenericPhraseAPI;
import com.mytaxi.apis.phrase.api.locale.dto.PhraseLocaleDTO;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseProjectLocale;
import com.mytaxi.apis.phrase.exception.PhraseAppApiException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by m.winkelmann on 30.10.15.
 */
public class DefaultPhraseLocaleAPI extends GenericPhraseAPI<PhraseLocaleDTO[]> implements PhraseLocaleAPI
{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultPhraseLocaleAPI.class);

    // ---- configuration -----
    private static final String PLACEHOLDER_PROJECT_ID = "{projectid}";

    private static final String PHRASE_LOCALES_PATH = "/api/v2/projects/" + PLACEHOLDER_PROJECT_ID + "/locales";


    public DefaultPhraseLocaleAPI(final RestTemplate restTemplate, final String authToken)
    {
        super(restTemplate, authToken);
    }


    public DefaultPhraseLocaleAPI(final String authToken, final String scheme, final String host)
    {
        super(createRestTemplateWithConverter(), scheme, host, authToken);
    }

    public DefaultPhraseLocaleAPI(final String authToken)
    {
        super(createRestTemplateWithConverter(), authToken);
    }


    @Override
    public List<PhraseProjectLocale> listLocales(final List<String> projectIds) throws PhraseAppApiException
    {
        Preconditions.checkNotNull(projectIds, "ProjectIds may not be null.");

        String projectIdsString = Joiner.on(",").join(projectIds);
        LOG.trace("Start to retrieve locales for projectIds: {}", projectIdsString);

        ArrayList<PhraseProjectLocale> phraseLocales = new ArrayList<>(projectIds.size());
        for (String projectId : projectIds)
        {
            ResponseEntity<PhraseLocaleDTO[]> responseEntity = null;
            try
            {
                String requestPath = createRequestPath(projectId);

                LOG.trace("Call requestPath: {} to get locales from phrase.", requestPath);

                URIBuilder builder = createUriBuilder(requestPath);

                HttpEntity<Object> requestEntity = createHttpEntity(requestPath);

                URI uri = builder.build();

                responseEntity = requestPhrase(requestEntity, uri, PhraseLocaleDTO[].class);

                PhraseLocaleDTO[] requestedLocales = handleResponse(projectId, requestPath, responseEntity);

                PhraseProjectLocale phraseProjectLocale = PhraseLocaleMapper.makePhraseProjectLocale(projectId, requestedLocales);

                phraseLocales.add(phraseProjectLocale);
            }
            catch (HttpClientErrorException e)
            {
                e.getResponseHeaders().forEach((key, value) -> LOG.debug("Header : [" + key + "] = " + value));
                throw new PhraseAppApiException("API execution error", e);
            }
            catch (URISyntaxException e)
            {
                throw new RuntimeException("Something goes wrong due building of the request URI", e);
            }
            finally
            {
                if (responseEntity != null)
                {
                    responseEntity.getHeaders().forEach((key, value) -> LOG.debug("Header : [" + key + "] = " + value));
                }
            }
        }

        LOG.trace("Successfully retrieved {} locales for projectIds: {}", phraseLocales.size(), projectIdsString);
        return phraseLocales;
    }


    private String createRequestPath(String projectId)
    {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(PLACEHOLDER_PROJECT_ID, projectId);
        return createPath(PHRASE_LOCALES_PATH, placeholders);
    }

}
