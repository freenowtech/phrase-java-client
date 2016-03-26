package com.mytaxi.apis.phrase.api.locale;

import com.mytaxi.apis.phrase.domainobject.locale.PhraseLocale;
import com.mytaxi.apis.phrase.config.TestConfig;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseProjectLocale;
import java.util.Arrays;
import java.util.List;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

/**
 * Created by m.winkelmann on 30.10.15.
 */
public class DefaultLocaleAPITest
{

    private RestTemplate restTemplate;


    private TestConfig cfg;


    @Before
    public void beforeTest()
    {
        cfg = ConfigFactory.create(TestConfig.class, System.getenv(), System.getProperties());
        restTemplate = Mockito.mock(RestTemplate.class);
    }

    // TODO: create tests for listLocales


    @Test
    public void testListLocales_integration() throws Exception
    {
        // GIVEN
        String authToken = cfg.authToken();
        DefaultPhraseLocaleAPI phraseAppApiV2 = new DefaultPhraseLocaleAPI(authToken);

        String projectId = cfg.projectId();

        // WHEN
        List<PhraseProjectLocale> phraseProjectLocales = phraseAppApiV2.listLocales(Arrays.asList(projectId));

        // THEN
        for (PhraseProjectLocale projectLocale : phraseProjectLocales)
        {
            List<PhraseLocale> locales = projectLocale.getLocales();
            Assert.assertTrue("2 Expected locals not responded", locales.size() == 2);
            PhraseLocale localeDe = locales.get(0);
            Assert.assertEquals("Code of DE locale not expected", localeDe.getCode(), "de");
            Assert.assertEquals("Name of DE locale not expected", localeDe.getName(), "de");

            PhraseLocale localeEn = locales.get(1);
            Assert.assertEquals("Code of EN locale not expected", localeEn.getCode(), "en");
            Assert.assertEquals("Name of EN locale not expected", localeEn.getName(), "en");
        }
    }

    // TODO - create test for etag functionality
}