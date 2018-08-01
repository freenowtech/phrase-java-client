package com.mytaxi.apis.phrase.domainobject.locale;

import java.util.List;
import java.util.Objects;

/**
 * Created by m.winkelmann on 11.11.15.
 */
public class PhraseProjectLocale
{
    private String projectId;
    private List<PhraseLocale> locales;


    private PhraseProjectLocale(String projectId, List<PhraseLocale> locales)
    {
        this.projectId = projectId;
        this.locales = locales;
    }


    public String getProjectId()
    {
        return projectId;
    }


    public List<PhraseLocale> getLocales()
    {
        return locales;
    }


    public static Builder newBuilder()
    {
        return new Builder();
    }


    public static class Builder
    {
        private String projectId;
        private List<PhraseLocale> locales;


        public Builder withProjectId(String projectId)
        {
            this.projectId = projectId;
            return this;
        }


        public Builder withLocales(List<PhraseLocale> locales)
        {
            this.locales = locales;
            return this;
        }


        public PhraseProjectLocale build()
        {
            return new PhraseProjectLocale(projectId, locales);
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
        PhraseProjectLocale that = (PhraseProjectLocale) o;
        return Objects.equals(projectId, that.projectId) &&
            Objects.equals(locales, that.locales);
    }


    @Override
    public int hashCode()
    {
        return Objects.hash(projectId, locales);
    }


    @Override
    public String toString()
    {
        return "PhraseProjectLocale{" +
            "projectId='" + projectId + '\'' +
            ", locales=" + locales +
            '}';
    }
}
