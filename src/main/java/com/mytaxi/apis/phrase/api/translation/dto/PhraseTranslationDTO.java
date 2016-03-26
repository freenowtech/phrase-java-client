package com.mytaxi.apis.phrase.api.translation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mytaxi.apis.phrase.api.locale.dto.PhraseLocaleDTO;

/**
 * Created by m.winkelmann on 05.11.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhraseTranslationDTO
{
    private String id;
    @JsonProperty("content")
    private String translation;
    private PhraseKeyDTO key;
    private PhraseLocaleDTO locale;


    private PhraseTranslationDTO()
    {

    }


    public String getId()
    {
        return id;
    }


    public String getTranslation()
    {
        return translation;
    }


    public PhraseKeyDTO getKey()
    {
        return key;
    }


    public PhraseLocaleDTO getLocale()
    {
        return locale;
    }


    @Override
    public String toString()
    {
        return "PhraseTranslationDTO{" +
            "id='" + id + '\'' +
            ", translation='" + translation + '\'' +
            ", key='" + key + '\'' +
            ", locale=" + locale +
            '}';
    }
}
