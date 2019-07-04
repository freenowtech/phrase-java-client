package com.mytaxi.apis.phrase.config;

import java.util.List;
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

    @DefaultValue("${ENV_PHRASE_BRANCHES}")
    List<String> branches();

    @DefaultValue("${ENV_PHRASE_LOCALEID_DE}")
    String localeIdDe();

    @DefaultValue("${ENV_PHRASE_HOST}")
    String host();

    @DefaultValue("${ENV_PHRASE_SCHEME}")
    String scheme();
}
