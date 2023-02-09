package com.freenow.apis.phrase.api.localedownload;

import com.freenow.apis.phrase.api.format.Format;
import com.freenow.apis.phrase.api.format.JavaPropertiesFormat;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.freenow.apis.phrase.config.TestConfig;
import java.util.List;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PhraseLocaleDownloadAPITest
{
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(9999));

    @Captor
    ArgumentCaptor<ResponseEntity<byte[]>> responseEntityCaptor;

    private TestConfig cfg;


    @Before
    public void beforeTest()
    {
        cfg = ConfigFactory.create(TestConfig.class, System.getenv(), System.getProperties());
    }


    @Test
    public void tesDownloadLocales_integration() throws Exception
    {
        // GIVEN
        String authToken = cfg.authToken();
        String projectId = cfg.projectId();
        String localeIdDe = cfg.localeIdDe();
        String host = cfg.host();
        String scheme = cfg.scheme();

        PhraseLocaleDownloadAPI localeDownloadAPI = new PhraseLocaleDownloadAPI(authToken, scheme, host);

        // WHEN
        byte[] fileBytes = localeDownloadAPI.downloadLocale(projectId, localeIdDe);

        // THEN
        assertNotNull(fileBytes);
    }

    @Test
    public void tesDownloadLocales_etag()
    {
        // GIVEN
        String authToken = cfg.authToken();
        String projectId = cfg.projectId();
        String localeIdDe = cfg.localeIdDe();
        String host = cfg.host();
        String scheme = cfg.scheme();

        PhraseLocaleDownloadAPI localeDownloadAPI = Mockito.spy(new PhraseLocaleDownloadAPI(authToken, scheme, host));

        // WHEN doing two request
        byte[] fileBytes1 = localeDownloadAPI.downloadLocale(projectId, localeIdDe);
        byte[] fileBytes2 = localeDownloadAPI.downloadLocale(projectId, localeIdDe);

        // THEN assert same result
        assertNotNull(fileBytes1);
        assertNotNull(fileBytes2);
        assertArrayEquals(fileBytes1, fileBytes2);

        // AND assert status codes 200 and 304 to verify E-Tag handling
        verify(localeDownloadAPI, times(2)).handleResponse(eq(projectId), anyString(), responseEntityCaptor.capture());
        List<ResponseEntity<byte[]>> statusCodes = responseEntityCaptor.getAllValues();

        assertThat(statusCodes)
            .hasSize(2)
            .extracting("statusCode").containsExactly(HttpStatus.OK, HttpStatus.NOT_MODIFIED);
    }


    @Test
    public void tesDownloadLocales_withFormatOptions() throws Exception
    {
        // GIVEN
        String authToken = cfg.authToken();
        String projectId = cfg.projectId();
        String localeIdDe = cfg.localeIdDe();
        List<String> branches = cfg.branches();
        String host = cfg.host();
        String scheme = cfg.scheme();

        PhraseLocaleDownloadAPI localeDownloadAPI = new PhraseLocaleDownloadAPI(authToken, scheme, host);

        Format format = JavaPropertiesFormat.newBuilder()
            .setEscapeSingleQuotes(false)
            .build();

        // WHEN
        byte[] fileBytes = localeDownloadAPI.downloadLocale(projectId, branches.get(0), localeIdDe, format);

        // THEN
        assertNotNull(fileBytes);
    }
}
