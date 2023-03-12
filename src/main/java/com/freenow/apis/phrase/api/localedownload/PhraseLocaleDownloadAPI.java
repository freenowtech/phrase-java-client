package com.freenow.apis.phrase.api.localedownload;

import com.freenow.apis.phrase.api.GenericPhraseAPI;
import com.freenow.apis.phrase.api.format.Format;
import com.freenow.apis.phrase.api.format.JavaPropertiesFormat;
import com.google.common.base.Preconditions;

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

/**
 * Created by m.winkelmann on 30.10.15.
 */
public class PhraseLocaleDownloadAPI extends GenericPhraseAPI<byte[]>
{

    private static final Logger LOG = LoggerFactory.getLogger(PhraseLocaleDownloadAPI.class);

    // ---- configuration -----
    private static final String PLACEHOLDER_PROJECT_ID = "{projectid}";

    private static final String PLACEHOLDER_LOCALE_ID = "{localeid}";

    private static final String PARAMETER_FILEFORMAT = "file_format";

    private static final String PARAMETER_BRANCH = "branch";

    private static final String PARAMETER_TAGS = "tags";

    private static final String PHRASE_LOCALES_DOWNLOAD_PATH = "/api/v2/projects/" + PLACEHOLDER_PROJECT_ID +
        "/locales/" + PLACEHOLDER_LOCALE_ID + "/download";

    public static final Format DEFAULT_FILE_FORMAT = JavaPropertiesFormat.newBuilder().build();

    public static final String DEFAULT_BRANCH = "master";


    protected PhraseLocaleDownloadAPI(final RestTemplate restTemplate, final String authToken)
    {
        super(restTemplate, authToken);
    }


    public PhraseLocaleDownloadAPI(final String authToken)
    {
        super(createRestTemplateWithConverter(), authToken);
    }


    public PhraseLocaleDownloadAPI(final String authToken, final String scheme, final String host)
    {
        super(createRestTemplateWithConverter(), scheme, host, authToken);
    }

    public byte[] downloadLocale(final String projectId, final String localeId)
    {
        return downloadLocale(projectId, DEFAULT_BRANCH, localeId, DEFAULT_FILE_FORMAT, null);
    }


    public byte[] downloadLocale(final String projectId, final String localeId, final String tags)
    {
        return downloadLocale(projectId, DEFAULT_BRANCH, localeId, DEFAULT_FILE_FORMAT, tags);
    }


    public byte[] downloadLocale(final String projectId, final String localeId, final Format format)
    {
        return downloadLocale(projectId, DEFAULT_BRANCH, localeId, format, null);
    }


    public byte[] downloadLocale(final String projectId, final String localeId, final Format format, final String tags)
    {
        return downloadLocale(projectId, DEFAULT_BRANCH, localeId, format, tags);
    }

    public byte[] downloadLocale(final String projectId, final String branch, final String localeId, final Format format, final String tags)
    {
        Preconditions.checkNotNull(projectId, "ProjectIds may not be null.");
        Preconditions.checkNotNull(format, "format may not be null.");

        LOG.trace("Start to retrieve locales for projectId: {}", projectId);

        try
        {
            final String requestPath = createDownloadLocaleRequestPath(projectId, localeId);

            LOG.trace("Call requestPath: {} to get locales from phrase.", requestPath);

            final List<NameValuePair> parameters = new ArrayList<>();
            parameters.add(new BasicNameValuePair(PARAMETER_FILEFORMAT, format.getName()));
            parameters.addAll(format.getOptions());

            if (!DEFAULT_BRANCH.equals(branch))
            {
                parameters.add(new BasicNameValuePair(PARAMETER_BRANCH, branch));
            }

            if (tags != null && !tags.trim().isEmpty()) {
                parameters.add(new BasicNameValuePair(PARAMETER_TAGS, tags));
            }

            final URIBuilder builder = createUriBuilder(requestPath, parameters);

            final String requestPathWithTags = requestPath.concat(getNonNullString(tags));

            final HttpEntity<Object> requestEntity = createHttpEntity(requestPathWithTags);

            final URI uri = builder.build();

            final ResponseEntity<byte[]> responseEntity = requestPhrase(requestEntity, uri, byte[].class);

            return handleResponse(projectId, requestPathWithTags, responseEntity);

        }
        catch (final URISyntaxException e)
        {
            throw new RuntimeException("Something goes wrong due building of the request URI", e);
        }
    }


    private String createDownloadLocaleRequestPath(final String projectId, final String localeId)
    {
        final Map<String, String> placeholders = new HashMap<>();
        placeholders.put(PLACEHOLDER_PROJECT_ID, projectId);
        placeholders.put(PLACEHOLDER_LOCALE_ID, localeId);

        return createPath(PHRASE_LOCALES_DOWNLOAD_PATH, placeholders);
    }

    private String getNonNullString(final String value) {
        if (value == null) return "";
        return value.trim();
    }
}
