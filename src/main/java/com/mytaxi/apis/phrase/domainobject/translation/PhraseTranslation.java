package com.mytaxi.apis.phrase.domainobject.translation;

import com.google.common.base.Preconditions;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseLocale;

/**
 * Created by m.winkelmann on 11.11.15.
 */
public class PhraseTranslation
{
    private String id;
    private String translation;
    private String key;
    private PhraseLocale locale;


    private PhraseTranslation(String id, String translation, String key, PhraseLocale locale)
    {
        this.id = id;
        this.translation = translation;
        this.key = key;
        this.locale = locale;
    }


    public String getId()
    {
        return id;
    }


    public String getTranslation()
    {
        return translation;
    }


    public String getKey()
    {
        return key;
    }


    public PhraseLocale getLocale()
    {
        return locale;
    }


    public static Builder newBuilder()
    {
        return new Builder();
    }


    public static class Builder
    {
        private String id;
        private String translation;
        private String key;
        private PhraseLocale locale;


        public Builder withId(String id)
        {
            this.id = id;
            return this;
        }


        public Builder withTranslation(String translation)
        {
            this.translation = translation;
            return this;
        }


        public Builder withKey(String key)
        {
            this.key = key;
            return this;
        }


        public Builder withLocale(PhraseLocale locale)
        {
            this.locale = locale;
            return this;
        }


        public PhraseTranslation build()
        {
            Preconditions.checkNotNull(id);
            Preconditions.checkNotNull(translation);
            Preconditions.checkNotNull(key);
            Preconditions.checkNotNull(locale);
            return new PhraseTranslation(id, translation, key, locale);
        }
    }
}