package com.freenow.apis.phrase.api.translation;

import com.freenow.apis.phrase.api.translation.dto.PhraseTranslationDTO;
import com.freenow.apis.phrase.domainobject.translation.PhraseTranslation;
import com.freenow.apis.phrase.api.locale.PhraseLocaleMapper;

import java.util.ArrayList;
import java.util.List;

public class PhraseTranslationMapper
{
    public static List<PhraseTranslation> makePhraseTranslations(PhraseTranslationDTO[] requestedTranslations)
    {
        ArrayList<PhraseTranslation> phraseTranslations = new ArrayList<PhraseTranslation>();
        for (PhraseTranslationDTO requestedPhraseTranslation : requestedTranslations)
        {
            phraseTranslations.add(makePhraseTranslation(requestedPhraseTranslation));
        }
        return phraseTranslations;
    }


    private static PhraseTranslation makePhraseTranslation(PhraseTranslationDTO requestedPhraseTranslation)
    {
        PhraseTranslation.Builder builder = PhraseTranslation.newBuilder()
            .withId(requestedPhraseTranslation.getId())
            .withKey(requestedPhraseTranslation.getKey().getName())
            .withLocale(PhraseLocaleMapper.makePhraseLocale(requestedPhraseTranslation.getLocale()))
            .withTranslation(requestedPhraseTranslation.getTranslation());
        return builder.build();
    }
}
