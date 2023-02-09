package com.freenow.apis.phrase.api.translation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PhraseKeyDTO
{
    private String id;
    private String name;


    public String getId()
    {
        return id;
    }


    public String getName()
    {
        return name;
    }


    @Override
    public String toString()
    {
        return "PhraseKeyDTO{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
