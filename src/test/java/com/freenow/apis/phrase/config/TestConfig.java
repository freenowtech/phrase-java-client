package com.freenow.apis.phrase.config;

import java.util.List;
import org.aeonbits.owner.Config;

/**
 * Created by j.ramm on 25.03.16.
 */
public interface TestConfig extends Config
{
    @DefaultValue("token-1234")
    String authToken();

    @DefaultValue("proj-1234")
    String projectId();

    @DefaultValue("master")
    List<String> branches();

    @DefaultValue("loc-1234")
    String localeIdDe();

    @DefaultValue("localhost:9999")
    String host();

    @DefaultValue("http")
    String scheme();

    @DefaultValue("tag-1234")
    String tags();
}
