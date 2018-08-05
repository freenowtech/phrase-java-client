package com.mytaxi.apis.phrase.api.translation;

import com.mytaxi.apis.phrase.api.locale.dto.PhraseLocaleDTO;
import com.mytaxi.apis.phrase.api.translation.dto.PhraseKeyDTO;
import com.mytaxi.apis.phrase.api.translation.dto.PhraseTranslationDTO;
import com.mytaxi.apis.phrase.config.TestConfig;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseLocale;
import com.mytaxi.apis.phrase.domainobject.translation.PhraseTranslation;
import com.mytaxi.apis.phrase.exception.PhraseAppApiException;
import java.net.URI;
import java.util.List;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by m.winkelmann on 05.11.15.
 */
public class DefaultPhraseTranslationAPITestNew
{
    private RestTemplate restTemplate;

    private DefaultPhraseTranslationAPI translationAPI;

    private String projectId;

    private String localeId;

    private TestConfig cfg;


    @Before
    public void beforeTest()
    {
        cfg = ConfigFactory.create(TestConfig.class, System.getenv(), System.getProperties());
        restTemplate = Mockito.mock(RestTemplate.class);
        String authTokenMock = "authMockTockenString";
        translationAPI = new DefaultPhraseTranslationAPI(restTemplate, authTokenMock);
        projectId = "SomeProjectId";
        localeId = "SomeLocaleId";
    }


    @Test(expected = PhraseAppApiException.class)
    public void testListTranslations_ERROR() throws Exception
    {
        // GIVEN
        ResponseEntity responseEntity = Mockito.mock(ResponseEntity.class);
        HttpStatus someHttpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        Mockito.when(responseEntity.getStatusCode()).thenReturn(someHttpStatus);
        Mockito.when(restTemplate.exchange(Matchers.any(URI.class), Matchers.any(HttpMethod.class),
            Matchers.any(HttpEntity.class), Matchers.any(Class.class))).thenReturn(responseEntity);

        // WHEN
        translationAPI.listTranslations(projectId, localeId);

        // THEN - throw Exception
    }


    @Test
    public void testListTranslations_OK() throws Exception
    {
        // GIVEN
        ResponseEntity responseEntity = Mockito.mock(ResponseEntity.class);
        PhraseTranslationDTO[] phraseTranslations = generatePhraseTranslationsMock();
        Mockito.when(responseEntity.getBody()).thenReturn(phraseTranslations);

        String eTag = "fdbfhse5741fbndnk";
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(headers.getETag()).thenReturn(eTag);
        Mockito.when(responseEntity.getHeaders()).thenReturn(headers);
        Mockito.when(headers.getETag()).thenReturn(eTag);

        HttpStatus someHttpStatus = HttpStatus.OK;
        Mockito.when(responseEntity.getStatusCode()).thenReturn(someHttpStatus);
        Mockito.when(restTemplate.exchange(Matchers.any(URI.class), Matchers.any(HttpMethod.class),
            Matchers.any(HttpEntity.class), Matchers.any(Class.class))).thenReturn(responseEntity);

        // WHEN
        List<PhraseTranslation> responseTranslations = translationAPI.listTranslations(projectId, localeId);

        // THEN
        Assert.assertNotNull("", responseTranslations);
        Assert.assertTrue("", responseTranslations.size() == 1);
    }


    private PhraseTranslationDTO[] generatePhraseTranslationsMock()
    {
        PhraseTranslationDTO translationMock = Mockito.mock(PhraseTranslationDTO.class);
        Mockito.when(translationMock.getId()).thenReturn("");
        PhraseKeyDTO phraseKeyDTO = Mockito.mock(PhraseKeyDTO.class);
        Mockito.when(phraseKeyDTO.getName()).thenReturn("test.test");
        Mockito.when(translationMock.getKey()).thenReturn(phraseKeyDTO);
        PhraseLocaleDTO phraseLocaleDTO = Mockito.mock(PhraseLocaleDTO.class);
        Mockito.when(phraseLocaleDTO.getName()).thenReturn("Deutschland");
        Mockito.when(phraseLocaleDTO.getId()).thenReturn("12323refwe321212");
        Mockito.when(phraseLocaleDTO.getCode()).thenReturn("DE");
        Mockito.when(translationMock.getLocale()).thenReturn(phraseLocaleDTO);
        Mockito.when(translationMock.getTranslation()).thenReturn("");
        return new PhraseTranslationDTO[] {translationMock};
    }


    @Test
    public void testListTranslations() throws Exception
    {
        // GIVEN
        String authToken = cfg.authToken();
        String host = cfg.host();
        String scheme = cfg.scheme();
        DefaultPhraseTranslationAPI translationAPI = new DefaultPhraseTranslationAPI(authToken, scheme, host);

        String projectId = cfg.projectId();

        String localeIdDe = cfg.localeIdDe();

        // WHEN
        List<PhraseTranslation> phraseTranslations = translationAPI.listTranslations(projectId, localeIdDe);

        // THEN
        Assert.assertNotNull("PhraseTranslations must not be null", phraseTranslations);
        Assert.assertTrue("PhraseTranslations must not be empty", phraseTranslations.size() > 0);
        for (PhraseTranslation translation : phraseTranslations)
        {
            String id = translation.getId();
            Assert.assertNotNull(id);
            PhraseLocale locale = translation.getLocale();
            Assert.assertNotNull(locale);
            String key = translation.getKey();
            Assert.assertNotNull(key);
        }
    }

}
