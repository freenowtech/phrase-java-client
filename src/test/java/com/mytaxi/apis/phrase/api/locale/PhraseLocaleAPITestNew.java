package com.mytaxi.apis.phrase.api.locale;

import com.mytaxi.apis.phrase.api.locale.dto.PhraseLocaleDTO;
import com.mytaxi.apis.phrase.config.TestConfig;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseBranch;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseLocale;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseProject;
import java.util.Collections;
import java.util.List;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by m.winkelmann on 30.10.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class PhraseLocaleAPITestNew
{

    private RestTemplate restTemplate;

    @Captor
    ArgumentCaptor<ResponseEntity<PhraseLocaleDTO[]>> responseEntityCaptor;

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
        String projectId = cfg.projectId();
        String host = cfg.host();
        String scheme = cfg.scheme();
        List<String> branches = cfg.branches();

        PhraseLocaleAPI phraseAppApiV2 = new PhraseLocaleAPI(authToken, scheme, host);

        // WHEN
        List<PhraseProject> phraseProjects = phraseAppApiV2.listLocales(Collections.singletonList(projectId), branches);

        // THEN
        for (PhraseProject phraseProject : phraseProjects)
        {
            for (PhraseBranch phraseBranch : phraseProject.getBranches())
            {
                List<PhraseLocale> locales = phraseBranch.getLocales();
                assertTrue(locales.size() >= 2);

                assertContainsPhraseLocaleCode(locales, "de");
                assertContainsPhraseLocaleCode(locales, "en");
            }
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

    @Test
    @Ignore
    public void listLocales_getTwoTimes_statusCode200and200_XRateLimitRemainingNotChanged()
    {
        // GIVEN
        String authToken = cfg.authToken();
        String projectId = cfg.projectId();
        String host = cfg.host();
        String scheme = cfg.scheme();
        List<String> branches = cfg.branches();

        PhraseLocaleAPI phraseLocaleAPI = Mockito.spy(new PhraseLocaleAPI(authToken, scheme, host));

        // WHEN doing two request
        List<PhraseProject> phraseProject1 = phraseLocaleAPI.listLocales(Collections.singletonList(projectId), branches);
        List<PhraseProject> phraseProject2 = phraseLocaleAPI.listLocales(Collections.singletonList(projectId), branches);

        // THEN assert same result
        assertNotNull(phraseProject1);
        assertNotNull(phraseProject2);
        assertEquals(phraseProject1, phraseProject2);

        // AND assert status codes 200 and 304 to verify E-Tag handling
        verify(phraseLocaleAPI, times(2))
            .handleResponse(eq(projectId), anyString(), responseEntityCaptor.capture());
        List<ResponseEntity<PhraseLocaleDTO[]>> responseEntities = responseEntityCaptor.getAllValues();

        assertEquals(HttpStatus.OK, responseEntities.get(0).getStatusCode());
        assertEquals(HttpStatus.OK, responseEntities.get(1).getStatusCode());

        // AND assert X-Rate-Limit-Remaining to verify Limits not changed
        assertEquals(
            responseEntities.get(0).getHeaders().get("X-Rate-Limit-Remaining"),
            responseEntities.get(1).getHeaders().get("X-Rate-Limit-Remaining")
        );
    }
}
