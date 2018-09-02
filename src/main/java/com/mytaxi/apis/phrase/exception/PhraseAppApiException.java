package com.mytaxi.apis.phrase.exception;

public class PhraseAppApiException extends RuntimeException
{
    static final long serialVersionUID = -7034807190745766939L;


    public PhraseAppApiException(String message)
    {
        super(message);
    }


    public PhraseAppApiException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}
