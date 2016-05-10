package com.mytaxi.apis.phrase.api.format;

import java.util.List;
import org.apache.http.NameValuePair;

public interface Format
{

    String getName();

    List<NameValuePair> getOptions();
}
