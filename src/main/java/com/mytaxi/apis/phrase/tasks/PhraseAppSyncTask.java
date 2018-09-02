package com.mytaxi.apis.phrase.tasks;

import com.google.common.collect.Collections2;
import com.mytaxi.apis.phrase.api.format.Format;
import com.mytaxi.apis.phrase.api.locale.PhraseLocaleAPI;
import com.mytaxi.apis.phrase.api.localedownload.PhraseLocaleDownloadAPI;
import com.mytaxi.apis.phrase.config.DefaultPhraseAppConfig;
import com.mytaxi.apis.phrase.config.PhraseAppConfig;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseLocale;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseProjectLocale;
import com.mytaxi.apis.phrase.exception.PhraseAppApiException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mytaxi.apis.phrase.api.localedownload.PhraseLocaleDownloadAPI.DEFAULT_FILE_FORMAT;
import static java.util.Collections.singletonList;

public class PhraseAppSyncTask implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(PhraseAppSyncTask.class);

    // init
    private final PhraseAppConfig phraseAppConfig;
    private final PhraseLocaleAPI localeAPI;
    private final PhraseLocaleDownloadAPI localeDownloadAPI;
    private final FileService fileService;

    // data
    private List<PhraseProjectLocale> phraseLocales;

    private Format format = DEFAULT_FILE_FORMAT;


    public PhraseAppSyncTask(final String authToken, final String projectId)
    {
        this(new DefaultPhraseAppConfig(authToken, projectId));

    }


    public PhraseAppSyncTask(final String authToken, final String projectId, final String scheme, final String host)
    {
        this(new DefaultPhraseAppConfig(authToken, projectId));
    }


    public PhraseAppSyncTask(final String authToken, final String projectId, PhraseLocaleAPI localeApi, PhraseLocaleDownloadAPI localeDownloadAPI, FileService fileService)
    {
        this(new DefaultPhraseAppConfig(authToken, projectId));
    }


    public PhraseAppSyncTask(PhraseAppConfig phraseAppConfig)
    {
        this.phraseAppConfig = phraseAppConfig;
        this.localeAPI = new PhraseLocaleAPI(phraseAppConfig);
        this.localeDownloadAPI = new PhraseLocaleDownloadAPI(phraseAppConfig);
        this.fileService = new FileService();
    }


    @Override
    public void run()
    {
        try
        {
            LOG.info("START Update Messages in messagesPath: " + fileService.getGeneratedResourcesFoldername());

            checkAndGetPhraseLocales();

            String projectId = phraseAppConfig.getProjectId();
            final List<PhraseLocale> locales = getLocales(projectId);
            if (locales != null)
            {
                for (final PhraseLocale locale : locales)
                {
                    updateLocale(projectId, locale);
                }
            }

            LOG.info("FINISHED Update Messages");
        }
        catch (final PhraseAppApiException e)
        {
            LOG.error("Error due execution Phrase API ", e);
            throw new RuntimeException(e);
        }
        catch (final Exception e)
        {
            LOG.error("Error due running the PhraseAppSyncTask", e);
            throw new RuntimeException(e);
        }
    }


    private void updateLocale(String projectId, PhraseLocale locale)
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
            LOG.error("Error updating locale {}", locale, e);
        }
    }


    List<PhraseProjectLocale> getPhraseLocales()
    {
        return phraseLocales;
    }


    private List<PhraseLocale> getLocales(final String projectId)
    {
        final Collection<PhraseProjectLocale> phrasesProjectLocales = Collections2.filter(
            getPhraseLocales(),
            projectLocale -> Objects.requireNonNull(projectLocale).getProjectId().equals(projectId)
        );
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
        phraseLocales = localeAPI.listLocales(singletonList(phraseAppConfig.getProjectId()));
    }


    public void setGeneratedResourcesFoldername(final String generatedResourcesFoldername)
    {
        fileService.setGeneratedResourcesFoldername(generatedResourcesFoldername);
    }


    public void setMessagesFoldername(final String messagesFoldername)
    {
        fileService.setMessagesFoldername(messagesFoldername);
    }


    public void setMessageFilePostfix(final String messageFilePostfix)
    {
        fileService.setMessageFilePostfix(messageFilePostfix);
    }


    public void setMessageFilePrefix(final String messageFilePrefix)
    {
        fileService.setMessageFilePrefix(messageFilePrefix);
    }


    public void setFormat(Format format)
    {
        this.format = format;
    }
}
