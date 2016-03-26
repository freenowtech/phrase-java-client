package com.mytaxi.apis.phrase.api.locale;

import com.mytaxi.apis.phrase.api.locale.dto.PhraseLocaleDTO;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseLocale;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseProjectLocale;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by m.winkelmann on 11.11.15.
 */
public class PhraseLocaleMapper
{
    public static PhraseProjectLocale makePhraseProjectLocale(String projectId, PhraseLocaleDTO[] requestedLocales)
    {
        PhraseProjectLocale.Builder builder = PhraseProjectLocale.newBuilder();
        builder.withProjectId(projectId);
        builder.withLocales(makePhraseLocales(requestedLocales));
        return builder.build();
    }


    public static List<PhraseLocale> makePhraseLocales(PhraseLocaleDTO[] requestedLocales)
    {
        List<PhraseLocale> locales = new ArrayList<PhraseLocale>(requestedLocales.length);
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
