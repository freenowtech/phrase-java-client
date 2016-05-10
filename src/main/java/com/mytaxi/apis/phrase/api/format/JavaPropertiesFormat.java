package com.mytaxi.apis.phrase.api.format;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * See https://phraseapp.com/docs/guides/formats/java-properties/
 */
public class JavaPropertiesFormat implements Format
{
    private final List<NameValuePair> options;


    private JavaPropertiesFormat(List<NameValuePair> options)
    {
        this.options = options;
    }


    @Override
    public String getName()
    {
        return "properties";
    }


    @Override
    public List<NameValuePair> getOptions()
    {
        return options;
    }


    public static Builder newBuilder()
    {
        return new Builder();
    }


    public static class Builder
    {
        private final List<NameValuePair> options = new ArrayList<>();


        public Builder setEscapeSingleQuotes(boolean escapeSingleQuotes)
        {
            options.add(new BasicNameValuePair("format_options[escape_single_quotes]", "" + escapeSingleQuotes));
            return this;
        }


        public JavaPropertiesFormat build()
        {
            return new JavaPropertiesFormat(options);
        }
    }
}
