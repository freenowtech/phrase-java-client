package com.mytaxi.apis.phrase.domainobject.locale;

import com.google.common.base.Preconditions;

/**
 * Created by m.winkelmann on 11.11.15.
 */
public class PhraseLocale
{
    private String id;
    private String code;
    private String name;


    private PhraseLocale(String id, String code, String name)
    {
        this.id = id;
        this.code = code;
        this.name = name;
    }


    public String getId()
    {
        return id;
    }


    public String getCode()
    {
        return code;
    }


    public String getName()
    {
        return name;
    }


    public static Builder newBuilder()
    {
        return new Builder();
    }


    public static class Builder
    {

        private String id;
        private String code;
        private String name;


        public Builder withId(String id)
        {
            this.id = id;
            return this;
        }


        public Builder withCode(String code)
        {
            this.code = code;
            return this;
        }


        public Builder withName(String name)
        {
            this.name = name;
            return this;
        }


        public PhraseLocale build()
        {
            validate();
            return new PhraseLocale(id, code, name);
        }


        private void validate()
        {
            Preconditions.checkNotNull(id);
            Preconditions.checkNotNull(code);
            Preconditions.checkNotNull(name);
        }
    }


    @Override
    public String toString()
    {
        return "PhraseLocale{" +
            "id='" + id + '\'' +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
