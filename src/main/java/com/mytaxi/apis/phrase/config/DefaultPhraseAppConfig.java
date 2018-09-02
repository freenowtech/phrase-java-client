package com.mytaxi.apis.phrase.config;

public class DefaultPhraseAppConfig implements PhraseAppConfig
{
    private final String authToken;
    private final String projectId;
    private final String baseUrl;


    public DefaultPhraseAppConfig(String authToken, String projectId)
    {
        this(authToken, projectId, "https://api.phraseapp.com");
    }


    public DefaultPhraseAppConfig(String authToken, String projectId, String scheme, String host)
    {
        this(authToken, projectId, scheme + "://" + host);
    }


    public DefaultPhraseAppConfig(String authToken, String projectId, String baseUrl)
    {
        this.authToken = authToken;
        this.projectId = projectId;
        this.baseUrl = baseUrl;
    }


    @Override
    public String getProjectId()
    {
        return projectId;
    }


    @Override
    public String getAuthToken()
    {
        return authToken;
    }


    @Override
    public String getBaseUrl()
    {
        return baseUrl;
    }
}
