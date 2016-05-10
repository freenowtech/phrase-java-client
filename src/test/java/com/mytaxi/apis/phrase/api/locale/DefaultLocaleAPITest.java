package com.mytaxi.apis.phrase.api.locale;

import com.mytaxi.apis.phrase.config.TestConfig;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseLocale;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseProjectLocale;
import java.util.Collections;
import java.util.List;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

        assertNotNull(cfg.authToken());
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
        List<PhraseProjectLocale> phraseProjectLocales = phraseAppApiV2.listLocales(Collections.singletonList(projectId));

        // THEN
        for (PhraseProjectLocale projectLocale : phraseProjectLocales)
        {
            List<PhraseLocale> locales = projectLocale.getLocales();
            assertTrue(locales.size() >= 2);

            assertContainsPhraseLocaleCode(locales, "de");
            assertContainsPhraseLocaleCode(locales, "en");
        }
    }

    private void assertContainsPhraseLocaleCode(List<PhraseLocale> locales, String phraseLocaleCode)
    {
        for (PhraseLocale locale : locales)
        {
            if(locale.getCode().equals(phraseLocaleCode))
            {
                return;
            }
        }

        fail("locale with code: " + phraseLocaleCode + " not found in list: " + locales);
    }

    // TODO - create test for etag functionality
}