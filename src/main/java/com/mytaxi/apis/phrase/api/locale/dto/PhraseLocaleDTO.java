package com.mytaxi.apis.phrase.api.locale.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by m.winkelmann on 05.11.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhraseLocaleDTO
{
    private String id;
    private String code;
    private String name;


    public String getId()
    {
        return id;
    }


    public void setId(final String id)
    {
        this.id = id;
    }


    public String getCode()
    {
        return code;
    }


    public void setCode(final String code)
    {
        this.code = code;
    }


    public String getName()
    {
        return name;
    }


    public void setName(final String name)
    {
        this.name = name;
    }


    @Override
    public String toString()
    {
        return "PhraseLocaleDTO{" +
            "id='" + id + '\'' +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
