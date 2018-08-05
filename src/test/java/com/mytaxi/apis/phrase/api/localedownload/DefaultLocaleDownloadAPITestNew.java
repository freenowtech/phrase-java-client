package com.mytaxi.apis.phrase.api.localedownload;

import com.mytaxi.apis.phrase.api.format.Format;
import com.mytaxi.apis.phrase.api.format.JavaPropertiesFormat;
import com.mytaxi.apis.phrase.config.TestConfig;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by m.winkelmann on 24.11.15.
 */
public class DefaultLocaleDownloadAPITestNew
{

    private TestConfig cfg;


    @Before
    public void beforeTest()
    {
        cfg = ConfigFactory.create(TestConfig.class, System.getenv(), System.getProperties());
    }


    @Test
    public void tesDownloadLocales_customHost()
    {
        // GIVEN
        String authToken = cfg.authToken();
        String projectId = cfg.projectId();
        String localeIdDe = cfg.localeIdDe();
        String host = cfg.host();
        String scheme = cfg.scheme();

        DefaultPhraseLocaleDownloadAPI localeDownloadAPI = new DefaultPhraseLocaleDownloadAPI(authToken, scheme, host);

        // WHEN
        byte[] fileBytes = localeDownloadAPI.downloadLocale(projectId, localeIdDe);

        // THEN
        assertNotNull(fileBytes);
    }


    @Test
    public void tesDownloadLocales_withFormatOptions() throws Exception
    {
        // GIVEN
        String authToken = cfg.authToken();
        String projectId = cfg.projectId();
        String localeIdDe = cfg.localeIdDe();
        String host = cfg.host();
        String scheme = cfg.scheme();

        DefaultPhraseLocaleDownloadAPI localeDownloadAPI = new DefaultPhraseLocaleDownloadAPI(authToken, scheme, host);

        Format format = JavaPropertiesFormat.newBuilder()
            .setEscapeSingleQuotes(false)
            .build();

        // WHEN
        byte[] fileBytes = localeDownloadAPI.downloadLocale(projectId, localeIdDe, format);

        // THEN
        assertNotNull(fileBytes);
    }
}
