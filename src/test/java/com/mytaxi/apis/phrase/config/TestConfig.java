package com.mytaxi.apis.phrase.config;

import org.aeonbits.owner.Config;

/**
 * Created by j.ramm on 25.03.16.
 */
public interface TestConfig extends Config
{
    @DefaultValue("${authToken}")
    String authToken();
    @DefaultValue("${projectId}")
    String projectId();
    @DefaultValue("${localeIdDe}")
    String localeIdDe();

}
