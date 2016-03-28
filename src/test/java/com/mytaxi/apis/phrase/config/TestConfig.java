package com.mytaxi.apis.phrase.config;

import org.aeonbits.owner.Config;

/**
 * Created by j.ramm on 25.03.16.
 */
public interface TestConfig extends Config
{
    @DefaultValue("${ENV_PHRASE_AUTHTOKEN}")
    String authToken();

    @DefaultValue("${ENV_PHRASE_PROJECTID}")
    String projectId();

    @DefaultValue("${ENV_PHRASE_LOCALEID_DE}")
    String localeIdDe();

}
