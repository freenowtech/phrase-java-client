package com.freenow.apis.phrase.api.tag;

import com.freenow.apis.phrase.api.tag.dto.PhraseTagWithStatsDTO;
import com.freenow.apis.phrase.config.TestConfig;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class PhraseTagAPITest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(9999));

    private TestConfig cfg;


    @Before
    public void beforeTest() {
        cfg = ConfigFactory.create(TestConfig.class, System.getenv(), System.getProperties());
    }

    @Test
    public void testGetSingleTag() {
        // GIVEN
        String authToken = cfg.authToken();
        String projectId = cfg.projectId();
        String scheme = cfg.scheme();
        String host = cfg.host();
        String tagName = cfg.tags();

        PhraseTagAPI phraseTagAPI = new PhraseTagAPI(authToken, scheme, host);

        // WHEN
        PhraseTagWithStatsDTO phraseTagWithStatsDTO = phraseTagAPI.getSingleTag(projectId, tagName);

        // THEN
        assertNotNull(phraseTagWithStatsDTO);
    }
}
