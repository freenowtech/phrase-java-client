# Phrase-Java-Client 
[![Build Status](https://travis-ci.org/mytaxi/phrase-java-client.svg?branch=master)](https://travis-ci.org/mytaxi/phrase-java-client)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.mytaxi.apis/phrase-java-client/badge.svg?x=1)](https://maven-badges.herokuapp.com/maven-central/com.mytaxi.apis/phrase-java-client)
## What is this?
This projects contains of services to handle the translations from [PhraseApp API v2](http://docs.phraseapp.com/api/v2/).
It's supposed to expose Phrase translations as POJO or as File within the java world.

## This project consists of 3 main phraseApp services:
- `PhraseLocaleAPI:` Downloads the locales from phraseApp as POJOs.
- `PhraseLocaleDownloadAPI:` Downloads the translations from phraseApp as file(byte[]).
- `PhraseTranslationAPI:` Downloads the translations from phraseApp as POJOs.


## How to use this project
Currently this project is not released in the maven central repository.
Please install this dependency to your local repository and include the following dependency:
```
<dependency>
    <groupId>com.mytaxi.apis</groupId>
    <artifactId>phrase-java-client</artifactId>
    <version>1.0.1</version>
</dependency>
```


## What you have to do to start it in an spring application?

### Create the bean PhraseAppSyncTask to run this job scheduled lately.

    @Bean
    public PhraseAppSyncTask phraseAppSyncTask(final PhraseConfig config)
    {
        return new PhraseAppSyncTask(config.getAuthToken(), config.getProjectId());
    }

### Create a scheduling to run this job frequently.

    @Scheduled(fixedRate = 120000)
    public void updatePhraseAppStringsTask()
    {
        try
        {
            final PhraseAppSyncTask phraseAppSyncTask = applicationContext.getBean("phraseAppSyncTask",
                PhraseAppSyncTask.class);
            phraseAppSyncTask.run();
            ResourceBundle.clearCache();
        }
        catch (final Exception e)
        {
            LOG.error("Error downloading PhraseApp messages due auto sync task!", e);
        }
    }

## Developers
In order to make the test work create a file `/src/test/resources/com/mytaxi/phraseapi/config/TestConfig.properties` with the content
```properties
authToken=<authToken>
projectId=<projectId>
localeIdDe=<localeIdDe>
```

### Deploy to OSS Sonatype
```
export authToken=<authToken>
export projectId=<projectId>
export localeIdDe=<localeIdDe>
mvn release:prepare -P release
mvn release:perform -P release
```

## TODOs
- add tests for main functionality of the services localedownload, translation
