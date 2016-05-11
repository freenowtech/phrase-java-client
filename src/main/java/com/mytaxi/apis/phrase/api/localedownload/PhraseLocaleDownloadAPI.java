package com.mytaxi.apis.phrase.api.localedownload;

import com.mytaxi.apis.phrase.api.format.Format;

/**
 * Downloads the translations from phraseApp as file(byte[]).
 *
 * @author d.pohl
 * @author m.winkelmann
 */
public interface PhraseLocaleDownloadAPI
{
    /**
     * Downloads the translations for the specific projectId and localeId and saves this in the given fileformat.
     *
     * @param projectId
     * @param localeId
     * @param fileFormat
     * @return file as byte[]
     */
    byte[] downloadLocale(String projectId, String localeId, Format fileFormat);

    /**
     * Downloads the translations for the specific projectId and localeId and saves this in the given fileformat.
     *
     * @param projectId
     * @param localeId
     * @return file as byte[] in the default fileformat: .properties
     */
    byte[] downloadLocale(String projectId, String localeId);

}
