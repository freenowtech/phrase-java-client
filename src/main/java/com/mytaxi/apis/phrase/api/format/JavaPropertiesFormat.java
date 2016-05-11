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

    public static final String NAME = "properties";
    private final List<NameValuePair> options;


    private JavaPropertiesFormat(List<NameValuePair> options)
    {
        this.options = options;
    }


    @Override
    public String getName()
    {
        return NAME;
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
            return setOption("escape_single_quotes", "" + escapeSingleQuotes);
        }


        public Builder setOption(String name, String value)
        {
            String fullName = String.format("format_options[%s]", name);
            options.add(new BasicNameValuePair(fullName, value));
            return this;
        }


        public JavaPropertiesFormat build()
        {
            return new JavaPropertiesFormat(options);
        }
    }
}
