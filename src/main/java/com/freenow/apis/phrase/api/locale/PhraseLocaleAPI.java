package com.freenow.apis.phrase.api.locale;

import com.freenow.apis.phrase.api.GenericPhraseAPI;
import com.freenow.apis.phrase.api.locale.dto.PhraseLocaleDTO;
import com.freenow.apis.phrase.api.localedownload.PhraseLocaleDownloadAPI;
import com.freenow.apis.phrase.domainobject.locale.PhraseBranch;
import com.freenow.apis.phrase.domainobject.locale.PhraseProject;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.freenow.apis.phrase.exception.PhraseAppApiException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by m.winkelmann on 30.10.15.
 */
public class PhraseLocaleAPI extends GenericPhraseAPI<PhraseLocaleDTO[]>
{
    private static final Logger LOG = LoggerFactory.getLogger(PhraseLocaleAPI.class);

    // ---- configuration -----
    private static final String PLACEHOLDER_PROJECT_ID = "{projectid}";
    private static final String PHRASE_LOCALES_PATH = "/api/v2/projects/" + PLACEHOLDER_PROJECT_ID + "/locales";
    private static final String PARAMETER_BRANCH = "branch";


    public PhraseLocaleAPI(final RestTemplate restTemplate, final String authToken)
    {
        super(restTemplate, authToken);
    }


    public PhraseLocaleAPI(final String authToken, final String scheme, final String host)
    {
        super(createRestTemplateWithConverter(), scheme, host, authToken);
    }


    public PhraseLocaleAPI(final String authToken)
    {
        super(createRestTemplateWithConverter(), authToken);
    }


    public List<PhraseProject> listLocales(final List<String> projectIds) throws PhraseAppApiException
    {
        return listLocales(projectIds, Arrays.asList(PhraseLocaleDownloadAPI.DEFAULT_BRANCH));
    }


    public List<PhraseProject> listLocales(final List<String> projectIds, final List<String> branches) throws PhraseAppApiException
    {
        Preconditions.checkNotNull(projectIds, "ProjectIds may not be null.");

        String projectIdsString = Joiner.on(",").join(projectIds);
        String branchesString = Joiner.on(",").join(branches);
        LOG.trace("Start to retrieve locales for projectIds: {}", projectIdsString, " and branches: {}", branchesString);

        ArrayList<PhraseProject> phraseProjects = new ArrayList<>(projectIds.size());

        for (String projectId : projectIds)
        {
            ArrayList<PhraseBranch> phraseBranches = new ArrayList<>(branches.size());

            for (String branch : branches)
            {
                ResponseEntity<PhraseLocaleDTO[]> responseEntity = null;

                try
                {
                    String requestPath = createRequestPath(projectId, branch);
                    LOG.trace("Call requestPath: {} to get locales from phrase.", requestPath);

                    if (!PhraseLocaleDownloadAPI.DEFAULT_BRANCH.equals(branch))
                    {
                        final List<NameValuePair> parameters = new ArrayList<>();
                        parameters.add(new BasicNameValuePair(PARAMETER_BRANCH, branch));
                    }

                    URIBuilder builder = createUriBuilder(requestPath);

                    HttpEntity<Object> requestEntity = createHttpEntity(requestPath);

                    URI uri = builder.build();

                    responseEntity = requestPhrase(requestEntity, uri, PhraseLocaleDTO[].class);

                    PhraseLocaleDTO[] requestedLocales = handleResponse(projectId, requestPath, responseEntity);

                    PhraseBranch phraseBranch = PhraseLocaleMapper.makePhraseBranch(branch, requestedLocales);

                    phraseBranches.add(phraseBranch);


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

            PhraseProject phraseProject = PhraseLocaleMapper.makePhraseProject(projectId, phraseBranches);

            phraseProjects.add(phraseProject);
        }

        LOG.trace("Successfully retrieved {} locales for projectIds: {}", phraseProjects.size(), projectIdsString);
        return phraseProjects;
    }


    private String createRequestPath(String projectId, String branch)
    {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(PLACEHOLDER_PROJECT_ID, projectId);
        return createPath(PHRASE_LOCALES_PATH, placeholders);
    }

}
