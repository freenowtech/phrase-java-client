package com.mytaxi.apis.phrase.api.localedownload;

import com.mytaxi.apis.phrase.api.format.Format;
import com.mytaxi.apis.phrase.api.format.JavaPropertiesFormat;
import com.mytaxi.apis.phrase.config.TestConfig;
import java.util.List;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by m.winkelmann on 24.11.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultLocaleDownloadAPITest
{

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

        DefaultPhraseLocaleDownloadAPI localeDownloadAPI = new DefaultPhraseLocaleDownloadAPI(authToken);

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

        DefaultPhraseLocaleDownloadAPI localeDownloadAPI = Mockito.spy(new DefaultPhraseLocaleDownloadAPI(authToken));

        // WHEN doing two request
        byte[] fileBytes1 = localeDownloadAPI.downloadLocale(projectId, localeIdDe);
        byte[] fileBytes2 = localeDownloadAPI.downloadLocale(projectId, localeIdDe);

        // THEN assert same result
        assertNotNull(fileBytes1);
        assertNotNull(fileBytes2);
        assertEquals(fileBytes1, fileBytes2);

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

        DefaultPhraseLocaleDownloadAPI localeDownloadAPI = new DefaultPhraseLocaleDownloadAPI(authToken);

        Format format = JavaPropertiesFormat.newBuilder()
            .setEscapeSingleQuotes(false)
            .build();

        // WHEN
        byte[] fileBytes = localeDownloadAPI.downloadLocale(projectId, localeIdDe, format);

        // THEN
        assertNotNull(fileBytes);
    }
}
