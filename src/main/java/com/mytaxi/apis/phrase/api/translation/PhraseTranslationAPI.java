package com.mytaxi.apis.phrase.api.translation;

import com.mytaxi.apis.phrase.domainobject.translation.PhraseTranslation;
import com.mytaxi.apis.phrase.exception.PhraseAppApiException;
import java.util.List;

/**
 * Downloads the translations from phraseApp as POJOs.
 *
 * @author m.winkelmann
 */
public interface PhraseTranslationAPI
{
    /**
     * Retrieves all translations for the given projectId and the localeId.
     *
     * @param projectId - id of the PhraseApp project
     * @param localeId  - id of the specific locale
     * @return list of retrieved PhraseTranslations
     * @throws PhraseAppApiException - if some error occured due the whole process
     */
    List<PhraseTranslation> listTranslations(String projectId, String localeId) throws PhraseAppApiException;
}
