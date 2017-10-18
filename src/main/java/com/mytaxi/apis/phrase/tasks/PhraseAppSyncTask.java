package com.mytaxi.apis.phrase.tasks;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mytaxi.apis.phrase.api.format.Format;
import com.mytaxi.apis.phrase.api.locale.DefaultPhraseLocaleAPI;
import com.mytaxi.apis.phrase.api.locale.PhraseLocaleAPI;
import com.mytaxi.apis.phrase.api.localedownload.DefaultPhraseLocaleDownloadAPI;
import com.mytaxi.apis.phrase.api.localedownload.PhraseLocaleDownloadAPI;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseLocale;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseProjectLocale;
import com.mytaxi.apis.phrase.service.FileService;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mytaxi.apis.phrase.api.localedownload.DefaultPhraseLocaleDownloadAPI.DEFAULT_FILE_FORMAT;

/**
 * Created by m.winkelmann on 04.11.15.
 */
public class PhraseAppSyncTask implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(PhraseAppSyncTask.class);

    // init
    private final List<String> projectIds;
    private final PhraseLocaleAPI localeAPI;
    private final PhraseLocaleDownloadAPI localeDownloadAPI;
    private final FileService fileService;

    // data
    private List<PhraseProjectLocale> phraseLocales;

    // logging
    private final String projectIdString;

    //
    private Format format = DEFAULT_FILE_FORMAT;


    public PhraseAppSyncTask(final String authToken, final String projectId)
    {
        // TODO - support for more projectIds but we need to think about how we want to save the message files
        projectIds = Collections.singletonList(projectId);
        localeAPI = new DefaultPhraseLocaleAPI(authToken);
        localeDownloadAPI = new DefaultPhraseLocaleDownloadAPI(authToken);
        projectIdString = Joiner.on(",").join(projectIds);
        fileService = new FileService();
        LOG.debug("Initialized PhraseAppSyncTask with following projectIds: " + projectIdString);
    }


    public PhraseAppSyncTask(final String authToken, final String projectId, PhraseLocaleAPI localeApi, PhraseLocaleDownloadAPI localeDownloadAPI, FileService fileService)
    {
        Preconditions.checkNotNull(authToken);
        Preconditions.checkNotNull(projectId);
        this.projectIds = Collections.singletonList(projectId);
        this.localeAPI = localeApi;
        this.localeDownloadAPI = localeDownloadAPI;
        this.projectIdString = Joiner.on(",").join(projectIds);
        this.fileService = fileService;
        LOG.debug("Initialized PhraseAppSyncTask with following projectIds: " + projectIdString);
    }


    @Override
    public void run()
    {
        try
        {
            LOG.info("START Update Messages in messagesPath: " + fileService.getGeneratedResourcesFoldername());

            checkAndGetPhraseLocales();

            for (final String projectId : projectIds)
            {
                final List<PhraseLocale> locales = getLocales(projectId);
                if (locales != null)
                {
                    for (final PhraseLocale locale : locales)
                    {
                        updateLocales(projectId, locale);
                    }
                }
            }

            LOG.info("FINISHED Update Messages");
        }
        catch (final Exception e)
        {
            LOG.error("Error due running the PhraseAppSyncTask", e);
        }
    }


    private void updateLocales(String projectId, PhraseLocale locale)
    {
        try
        {
            byte[] translationByteArray = localeDownloadAPI.downloadLocale(projectId, locale.getId(), format);
            if (translationByteArray == null || translationByteArray.length == 0)
            {
                LOG.warn("Could not receive any data from PhraseAppApi for locale: {}. Please check configuration in PhraseApp!", locale);
                translationByteArray = "no.data.received=true".getBytes();
            }
            fileService.saveToFile(projectId, translationByteArray, locale.getCode().replace('-', '_'));
        }
        catch (Exception e)
        {
            LOG.error("Error updating locale " + locale.getName(), e);
        }
    }


    List<PhraseProjectLocale> getPhraseLocales()
    {
        return phraseLocales;
    }


    private List<PhraseLocale> getLocales(final String projectId)
    {
        final Collection<PhraseProjectLocale> phrasesProjectLocales = Collections2.filter(phraseLocales, new Predicate<PhraseProjectLocale>()
        {
            @Override
            public boolean apply(final PhraseProjectLocale projectLocale)
            {
                return projectLocale.getProjectId().equals(projectId);
            }
        });
        if (phrasesProjectLocales.isEmpty())
        {
            LOG.warn("No locales found for projectId: " + projectId);
            return null;
        }
        final PhraseProjectLocale projectLocale = phrasesProjectLocales.iterator().next();
        return projectLocale.getLocales();
    }


    private void checkAndGetPhraseLocales()
    {
        if (phraseLocales == null)
        {
            initLocales();
        }
    }


    private void initLocales()
    {
        try
        {
            LOG.debug("Start: Initialize all locales for projectIds: " + projectIdString);
            phraseLocales = localeAPI.listLocales(projectIds);
            LOG.trace("Locales are successfully retreived: " + Joiner.on(",").join(phraseLocales));
            LOG.debug("End: Initialize all locales for projectIds: " + projectIdString);
        }
        catch (final Exception e)
        {
            LOG.error("Error due calling the locales from phrase app", e);
        }
    }


    void setMessagesFoldername(final String messagesFoldername)
    {
        fileService.setMessagesFoldername(messagesFoldername);
    }


    void setFormat(Format format)
    {
        this.format = format;
    }
}
