package com.mytaxi.apis.phrase.api.translation;

import com.mytaxi.apis.phrase.api.locale.PhraseLocaleMapper;
import com.mytaxi.apis.phrase.api.translation.dto.PhraseTranslationDTO;
import com.mytaxi.apis.phrase.domainobject.translation.PhraseTranslation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by m.winkelmann on 11.11.15.
 */
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
