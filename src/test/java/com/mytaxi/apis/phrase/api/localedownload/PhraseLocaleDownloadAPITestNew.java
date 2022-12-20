package com.mytaxi.apis.phrase.api.localedownload;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mytaxi.apis.phrase.api.format.Format;
import com.mytaxi.apis.phrase.api.format.JavaPropertiesFormat;
import com.mytaxi.apis.phrase.config.TestConfig;
import java.util.List;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertNotNull;

/**
 * Created by m.winkelmann on 24.11.15.
 */
public class PhraseLocaleDownloadAPITestNew
{
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(9999));

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

        PhraseLocaleDownloadAPI localeDownloadAPI = new PhraseLocaleDownloadAPI(authToken, scheme, host);

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
        List<String> branches = cfg.branches();


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
