package com.mytaxi.apis.phrase.api.localedownload;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.mytaxi.apis.phrase.api.GenericPhraseAPI;
import com.mytaxi.apis.phrase.api.format.Format;
import com.mytaxi.apis.phrase.api.format.JavaPropertiesFormat;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

public class PhraseLocaleDownloadAPI extends GenericPhraseAPI<byte[]>
{

    private static final Logger LOG = LoggerFactory.getLogger(PhraseLocaleDownloadAPI.class);

    // ---- configuration -----
    private static final String PLACEHOLDER_PROJECT_ID = "{projectid}";

    private static final String PLACEHOLDER_LOCALE_ID = "{localeid}";

    private static final String PLACEHOLDER_FILEFORMAT = "file_format";

    private static final String PHRASE_LOCALES_DOWNLOAD_PATH = "/api/v2/projects/{projectid}/locales/{localeid}/download";

    public static final Format DEFAULT_FILE_FORMAT = JavaPropertiesFormat.newBuilder().build();


    public PhraseLocaleDownloadAPI(final String authToken)
    {
        super(createRestTemplateWithConverter(), authToken);
    }


    public byte[] downloadLocale(final String projectId, final String localeId)
    {
        return downloadLocale(projectId, localeId, DEFAULT_FILE_FORMAT);
    }


    public byte[] downloadLocale(final String projectId, final String localeId, final Format format)
    {
        Preconditions.checkNotNull(projectId, "ProjectIds may not be null.");
        Preconditions.checkNotNull(format, "format may not be null.");

        LOG.trace("Start to retrieve locales for projectId: {}", projectId);

        try
        {
            final String requestPath = createDownloadLocaleRequestPath(projectId, localeId);

            LOG.trace("Call requestPath: {} to get locales from phrase.", requestPath);

            final List<NameValuePair> parameters = new ArrayList<>();
            parameters.add(new BasicNameValuePair(PLACEHOLDER_FILEFORMAT, format.getName()));
            parameters.addAll(format.getOptions());

            final URIBuilder builder = createUriBuilder(requestPath, parameters);

            final HttpEntity<Object> requestEntity = createHttpEntity(requestPath);

            final URI uri = builder.build();

            final ResponseEntity<byte[]> responseEntity = requestPhrase(requestEntity, uri, byte[].class);

            return handleResponse(projectId, requestPath, responseEntity);

        }
        catch (final URISyntaxException e)
        {
            throw new RuntimeException("Something goes wrong due building of the request URI", e);
        }
    }


    private String createDownloadLocaleRequestPath(final String projectId, final String localeId)
    {
        return createPath(PHRASE_LOCALES_DOWNLOAD_PATH, ImmutableMap.of(
            PLACEHOLDER_PROJECT_ID, projectId,
            PLACEHOLDER_LOCALE_ID, localeId));
    }

}
