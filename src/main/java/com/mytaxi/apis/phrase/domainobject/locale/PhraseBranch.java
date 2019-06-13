package com.mytaxi.apis.phrase.domainobject.locale;

import java.util.List;
import java.util.Objects;

public class PhraseBranch
{
    private String branchName;
    private List<PhraseLocale> locales;


    private PhraseBranch(String branchName, List<PhraseLocale> locales)
    {
        this.branchName = branchName;
        this.locales = locales;
    }


    public String getBranchName()
    {
        return branchName;
    }


    public List<PhraseLocale> getLocales()
    {
        return locales;
    }


    public static PhraseBranch.Builder newBuilder()
    {
        return new PhraseBranch.Builder();
    }


    public static class Builder
    {
        private String branchName;
        private List<PhraseLocale> locales;


        public PhraseBranch.Builder withBranchName(String branchName)
        {
            this.branchName = branchName;
            return this;
        }


        public PhraseBranch.Builder withLocales(List<PhraseLocale> locales)
        {
            this.locales = locales;
            return this;
        }


        public PhraseBranch build()
        {
            return new PhraseBranch(branchName, locales);
        }
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        PhraseBranch that = (PhraseBranch) o;
        return Objects.equals(branchName, that.branchName) &&
            Objects.equals(locales, that.locales);
    }


    @Override
    public int hashCode()
    {
        return Objects.hash(branchName, locales);
    }


    @Override
    public String toString()
    {
        return "PhraseBranch{" +
            "branchName='" + branchName + '\'' +
            ", locales=" + locales +
            '}';
    }
}

