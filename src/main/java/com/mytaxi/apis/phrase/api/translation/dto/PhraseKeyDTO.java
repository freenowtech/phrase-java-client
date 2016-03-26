package com.mytaxi.apis.phrase.api.translation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by m.winkelmann on 05.11.15.
 */
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
