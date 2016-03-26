package com.mytaxi.apis.phrase.api.locale;

import com.mytaxi.apis.phrase.exception.PhraseAppApiException;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseProjectLocale;
import java.util.List;

/**
 * Downloads the locales from phraseApp as POJOs.
 *
 * @author m.winkelmann
 */
public interface PhraseLocaleAPI
{
    /**
     * Retrieves all locales for the given projectIds.
     *
     * @param projectIds - ids of the projects you want to have
     * @return list of PhraseProjectLocales
     * @throws PhraseAppApiException - if some error occured due the whole process
     */
    List<PhraseProjectLocale> listLocales(List<String> projectIds) throws PhraseAppApiException;
}
