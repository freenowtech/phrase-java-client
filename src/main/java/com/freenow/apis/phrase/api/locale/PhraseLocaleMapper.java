package com.freenow.apis.phrase.api.locale;

import com.freenow.apis.phrase.api.locale.dto.PhraseLocaleDTO;
import com.freenow.apis.phrase.domainobject.locale.PhraseBranch;
import com.freenow.apis.phrase.domainobject.locale.PhraseLocale;
import com.freenow.apis.phrase.domainobject.locale.PhraseProject;

import java.util.ArrayList;
import java.util.List;

public class PhraseLocaleMapper
{
    public static PhraseProject makePhraseProject(String projectId, List<PhraseBranch> branches)
    {
        return PhraseProject.newBuilder()
            .withProjectId(projectId)
            .withBranches(branches)
            .build();
    }


    public static PhraseBranch makePhraseBranch(String branch, PhraseLocaleDTO[] requestedLocales)
    {
        return PhraseBranch.newBuilder()
            .withBranchName(branch)
            .withLocales(makePhraseLocales(requestedLocales))
            .build();
    }


    public static PhraseLocale makePhraseLocale(PhraseLocaleDTO localeDTO)
    {
        return PhraseLocale.newBuilder().withId(localeDTO.getId()).withName(localeDTO.getName()).withCode(localeDTO.getCode()).build();
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
}
