package com.mytaxi.apis.phrase.api;

import com.mytaxi.apis.phrase.exception.PhraseAppApiException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by m.winkelmann on 05.11.15.
 */
public class GenericPhraseAPI<T>
{
    private static final Logger LOG = LoggerFactory.getLogger(GenericPhraseAPI.class);

    private static final String PHRASE_SCHEME = "https";

    private static final String PHRASE_HOST = "api.phraseapp.com";

    // --- internal services ---
    protected final RestTemplate restTemplate;
    protected final String authToken;

    private final Map<String, String> pathToETagCache = new HashMap<String, String>();

    private final Map<String, T> pathToResponseCache = new HashMap<String, T>();


    public GenericPhraseAPI(final RestTemplate restTemplate, final String authToken)
    {
        this.restTemplate = restTemplate;
        this.authToken = authToken;
    }


    protected static RestTemplate createRestTemplateWithConverter()
    {
        final RestTemplate restTemplate = new RestTemplate();
        final List<HttpMessageConverter<?>> httpMessageConverters = new ArrayList<HttpMessageConverter<?>>();
        httpMessageConverters.add(new MappingJackson2HttpMessageConverter());
        httpMessageConverters.add(new ByteArrayHttpMessageConverter());
        restTemplate.setMessageConverters(httpMessageConverters);
        return restTemplate;
    }


    protected String createPath(final String path, final Map<String, String> placeholders)
    {
        // TODO with URIBuilder or directly in createUriBuilder()
        String requestPath = new String(path);

        for (final Map.Entry<String, String> entity : placeholders.entrySet())
        {
            final String value = entity.getValue();
            final String key = entity.getKey();

            requestPath = requestPath.replace(key, value);
        }
        return requestPath;
    }


    protected URIBuilder createUriBuilder(final String path)
    {
        return createUriBuilder(path, null);
    }


    protected URIBuilder createUriBuilder(final String path, final List<NameValuePair> parameters)
    {
        final URIBuilder builder = new URIBuilder();
        builder.setScheme(PHRASE_SCHEME)
            .setHost(PHRASE_HOST)
            .setPath(path);

        if (parameters != null)
        {
            builder.setParameters(parameters);
        }
        return builder;
    }


    protected HttpEntity<Object> createHttpEntity(final String requestPath)
    {
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        requestHeaders.add(org.apache.http.HttpHeaders.AUTHORIZATION, "token " + authToken);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        if (pathToETagCache.containsKey(requestPath))
        {
            final String etag = pathToETagCache.get(requestPath);
            requestHeaders.setIfNoneMatch(etag);
            LOG.debug("Use etag: {} for requestPath: {}", etag, requestPath);
        }

        return new HttpEntity<Object>(requestHeaders);
    }


    protected void cacheETag(final String requestPath, final String eTag)
    {
        if (eTag != null)
        {
            pathToETagCache.put(requestPath, eTag);
        }
    }


    protected void cacheData(final String requestPath, final T cacheData)
    {
        pathToResponseCache.put(requestPath, cacheData);
    }


    protected T getCachedData(final String requestPath)
    {
        final T cachedData = pathToResponseCache.get(requestPath);
        if (cachedData == null)
        {
            throw new PhraseAppApiException("Could not get data from cache! Somethings goes wrong.");
        }
        return cachedData;
    }


    protected T handleResponse(final String projectId, final String requestPath, final ResponseEntity<T> responseEntity) throws PhraseAppApiException
    {
        T requestedData;
        final HttpStatus statusCode = responseEntity.getStatusCode();
        switch (statusCode)
        {
            case OK:
            {
                logResponseStatus(requestPath, statusCode);
                requestedData = responseEntity.getBody();
                final HttpHeaders headers = responseEntity.getHeaders();
                cacheETag(requestPath, headers != null ? headers.getETag() : null);
                cacheData(requestPath, requestedData);
                break;
            }
            case NOT_MODIFIED:
            {
                logResponseStatus(requestPath, statusCode);
                requestedData = getCachedData(requestPath);
                break;
            }
            case TOO_MANY_REQUESTS:
            {
                logResponseStatus(requestPath, statusCode);
                throw new PhraseAppApiException("Too many requests occured in the last minutes for project with id: " + projectId);
            }
            default:
            {
                LOG.debug("Unknown response status {} for requestPath: {}", statusCode, requestPath);
                throw new PhraseAppApiException("Error due request phraseapp for projectId: " + projectId + " response status: " + statusCode);
            }
        }
        return requestedData;
    }


    private void logResponseStatus(final String requestPath, final HttpStatus statusCode)
    {
        LOG.debug("Reponse status {} for requestPath: {}", statusCode, requestPath);
    }


    protected ResponseEntity<T> requestPhrase(final HttpEntity<Object> requestEntity, final URI uri, final Class<T> clazz)
    {
        try
        {
            LOG.trace("Call phrase ap URI: {}", uri.toString());
            return restTemplate.exchange(uri, HttpMethod.GET, requestEntity, clazz);
        }
        catch (final Throwable throwable)
        {
            throw new PhraseAppApiException("Error in executing http call", throwable);
        }
    }

}
