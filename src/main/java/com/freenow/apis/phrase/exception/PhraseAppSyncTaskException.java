package com.freenow.apis.phrase.exception;

public class PhraseAppSyncTaskException extends RuntimeException
{
    public PhraseAppSyncTaskException(String message)
    {
        super(message);
    }

    public PhraseAppSyncTaskException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}
