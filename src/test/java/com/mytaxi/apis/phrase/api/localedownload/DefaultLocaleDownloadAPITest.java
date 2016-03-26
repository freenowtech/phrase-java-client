package com.mytaxi.apis.phrase.api.localedownload;

import com.mytaxi.apis.phrase.config.TestConfig;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by m.winkelmann on 24.11.15.
 */
public class DefaultLocaleDownloadAPITest
{


    private TestConfig cfg;

    @Before
    public void beforeTest()
    {
        cfg = ConfigFactory.create(TestConfig.class);
    }

    // TODO: create tests for downloadLocale


    @Test
    public void tesDownloadLocales_integration() throws Exception
    {
        // GIVEN
        String authToken = cfg.authToken();
        DefaultPhraseLocaleDownloadAPI localeDownloadAPI = new DefaultPhraseLocaleDownloadAPI(authToken);

        String projectId = cfg.projectId();

        String localeIdDe = cfg.localeIdDe();

        // WHEN
        byte[] fileBytes = localeDownloadAPI.downloadLocale(projectId, localeIdDe);

        // THEN
        Assert.assertNotNull(fileBytes);
    }
}