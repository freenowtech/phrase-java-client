package com.mytaxi.apis.phrase.api.locale;

import com.mytaxi.apis.phrase.api.locale.dto.PhraseLocaleDTO;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseLocale;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseProjectLocale;
import java.util.ArrayList;
import java.util.List;

public class PhraseLocaleMapper
{
    public static PhraseProjectLocale makePhraseProjectLocale(String projectId, PhraseLocaleDTO[] requestedLocales)
    {
        return PhraseProjectLocale.newBuilder()
            .withProjectId(projectId)
            .withLocales(makePhraseLocales(requestedLocales))
            .build();
    }


    private static List<PhraseLocale> makePhraseLocales(PhraseLocaleDTO[] requestedLocales)
    {
        List<PhraseLocale> locales = new ArrayList<>(requestedLocales.length);
        for (PhraseLocaleDTO localeDTO : requestedLocales)
        {
            locales.add(makePhraseLocale(localeDTO));
        }
        return locales;
    }


    public static PhraseLocale makePhraseLocale(PhraseLocaleDTO localeDTO)
    {
        return PhraseLocale.newBuilder().withId(localeDTO.getId()).withName(localeDTO.getName()).withCode(localeDTO.getCode()).build();
    }
}
