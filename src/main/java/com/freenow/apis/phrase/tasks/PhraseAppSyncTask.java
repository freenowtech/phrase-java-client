package com.freenow.apis.phrase.tasks;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.freenow.apis.phrase.api.format.Format;
import com.freenow.apis.phrase.api.locale.PhraseLocaleAPI;
import com.freenow.apis.phrase.api.localedownload.PhraseLocaleDownloadAPI;
import com.freenow.apis.phrase.domainobject.locale.PhraseBranch;
import com.freenow.apis.phrase.domainobject.locale.PhraseLocale;
import com.freenow.apis.phrase.domainobject.locale.PhraseProject;
import com.freenow.apis.phrase.exception.PhraseAppApiException;
import com.freenow.apis.phrase.exception.PhraseAppSyncTaskException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.singletonList;
import static com.freenow.apis.phrase.api.localedownload.PhraseLocaleDownloadAPI.DEFAULT_FILE_FORMAT;
import static com.freenow.apis.phrase.api.localedownload.PhraseLocaleDownloadAPI.DEFAULT_BRANCH;

public class PhraseAppSyncTask implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(PhraseAppSyncTask.class);

    // init
    private final List<String> projectIds;
    private final String tags;
    private final List<String> branches;
    private final PhraseLocaleAPI localeAPI;
    private final PhraseLocaleDownloadAPI localeDownloadAPI;
    private final FileService fileService;

    // data
    private List<PhraseProject> phraseProjects;

    // logging
    private final String projectIdString;
    private final String branchesString;

    //
    private Format format = DEFAULT_FILE_FORMAT;



    public PhraseAppSyncTask(final String authToken, final String projectId)
    {
        // TODO - support for more projectIds but we need to think about how we want to save the message files
        projectIds = Collections.singletonList(projectId);
        this.tags = null;
        branches = singletonList(DEFAULT_BRANCH);
        localeAPI = new PhraseLocaleAPI(authToken);
        localeDownloadAPI = new PhraseLocaleDownloadAPI(authToken);
        projectIdString = Joiner.on(",").join(projectIds);
        branchesString = Joiner.on(",").join(branches);
        fileService = new FileService();
        LOG.debug("Initialized PhraseAppSyncTask with following projectIds: " + projectIdString + " and branches: " + branchesString);
    }

    public PhraseAppSyncTask(final String authToken, final String projectId, PhraseLocaleAPI localeApi, PhraseLocaleDownloadAPI localeDownloadAPI, FileService fileService)
    {
        this(authToken, projectId, singletonList(DEFAULT_BRANCH), localeApi, localeDownloadAPI, fileService);
    }

    public PhraseAppSyncTask(final String authToken, final String projectId, final List<String> branches, PhraseLocaleAPI localeApi, PhraseLocaleDownloadAPI localeDownloadAPI,
        FileService fileService)
    {
        Preconditions.checkNotNull(authToken);
        Preconditions.checkNotNull(projectId);
        this.branches = branches;
        this.projectIds = Collections.singletonList(projectId);
        this.tags = null;
        this.localeAPI = localeApi;
        this.localeDownloadAPI = localeDownloadAPI;
        this.projectIdString = Joiner.on(",").join(projectIds);
        branchesString = Joiner.on(",").join(branches);
        this.fileService = fileService;
        LOG.debug("Initialized PhraseAppSyncTask with following projectIds: " + projectIdString + " and branches: " + branchesString);
    }


    /**
     * Constructs a new PhraseAppSyncTask by parsing a base URL of the Phrase API and setting Auth Token, Project and
     * Branches.
     *
     * @param authToken Phrase auth token
     * @param projectId ID of the project in Phrase
     * @param tags      List of comma separated tag names of the project in Phrase
     * @param branches  List of branches in Phrase to query
     * @param baseURL   Base URL of the Phrase API, e.g. {@code https://api.phraseapp.com}
     */
    public PhraseAppSyncTask(final String authToken, final String projectId, final String tags, final List<String> branches, final String baseURL)
    {
        URI uri;
        try
        {
            uri = new URI(baseURL);
        }
        catch (NullPointerException | URISyntaxException e)
        {
            throw new PhraseAppSyncTaskException("Parsing base URL failed", e);
        }

        if (!uri.getScheme().equals("http") && !uri.getScheme().equals("https"))
        {
            throw new PhraseAppSyncTaskException("Expect scheme in base URL to be 'http' or 'https', was '" + uri.getScheme() + "'");
        }

        String host = baseURL.replace(uri.getScheme() + "://", "");
        projectIds = Collections.singletonList(projectId);
        this.tags = tags;
        this.branches = branches;
        localeAPI = new PhraseLocaleAPI(authToken, uri.getScheme(), host);
        localeDownloadAPI = new PhraseLocaleDownloadAPI(authToken, uri.getScheme(), host);
        projectIdString = Joiner.on(",").join(projectIds);
        branchesString = Joiner.on(",").join(branches);
        fileService = new FileService();
        LOG.debug("Initialized PhraseAppSyncTask with following projectIds: " + projectIdString + " and branches: " + branchesString);
    }


    /**
     * Constructs a new PhraseAppSyncTask by parsing a base URL of the Phrase API and setting Auth Token, Project and
     * Branches.
     *
     * @param authToken Phrase auth token
     * @param projectId ID of the project in Phrase
     * @param branches  List of branches in Phrase to query
     * @param baseURL   Base URL of the Phrase API, e.g. {@code https://api.phraseapp.com}
     */
    public PhraseAppSyncTask(final String authToken, final String projectId, final List<String> branches, final String baseURL)
    {
        this(authToken, projectId, null, branches, baseURL);
    }


    /**
     * Constructs a new PhraseAppSyncTask by parsing a base URL of the Phrase API and setting Auth Token and Project.
     * Uses the default branch in Phrase.
     *
     * @param authToken Phrase auth token
     * @param projectId ID of the project in Phrase
     * @param baseURL   Base URL of the Phrase API, e.g. {@code https://api.phraseapp.com}
     */
    public PhraseAppSyncTask(final String authToken, final String projectId, final String baseURL)
    {
        this(authToken, projectId, null, singletonList(DEFAULT_BRANCH), baseURL);
    }


    /**
     * Constructs a new PhraseAppSyncTask by parsing a base URL of the Phrase API and setting Auth Token and Project.
     * Uses the default branch in Phrase.
     *
     * @param authToken Phrase auth token
     * @param tags      List of comma separated tag names of the project in Phrase
     * @param projectId ID of the project in Phrase
     * @param baseURL   Base URL of the Phrase API, e.g. {@code https://api.phraseapp.com}
     */
    public PhraseAppSyncTask(final String authToken, final String projectId, final String tags, final String baseURL)
    {
        this(authToken, projectId, tags, singletonList(DEFAULT_BRANCH), baseURL);
    }

    List<PhraseProject> getPhraseProjects()
    {
        return phraseProjects;
    }

    @Override
    public void run()
    {
        try
        {
            LOG.info("START Update Messages in messagesPath: " + fileService.getGeneratedResourcesFoldername());

            checkAndGetPhraseLocales();

            projectIds
                .forEach(projectId -> branches
                    .forEach(branch -> updateBranchLocales(projectId, branch, tags)));

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

    private void updateBranchLocales(final String projectId, final String branch, final String tags)
    {
        final List<PhraseLocale> locales = getLocales(projectId, branch);

        if (locales != null)
        {
            for (final PhraseLocale locale : locales)
            {
                updateLocale(projectId, branch, locale, tags);
            }
        }
    }

    private void updateLocale(final String projectId, final String branch, final PhraseLocale locale, final String tags)
    {
        try
        {
            byte[] translationByteArray = localeDownloadAPI.downloadLocale(projectId, branch, locale.getId(), format, tags);
            if (translationByteArray == null || translationByteArray.length == 0)
            {
                LOG.warn("Could not receive any data from PhraseAppApi for locale: {}. Please check configuration in PhraseApp!", locale);
                translationByteArray = "no.data.received=true".getBytes();
            }
            fileService.saveToFile(projectId, branch, translationByteArray, locale.getCode().replace('-', '_'));
        }
        catch (Exception e)
        {
            LOG.error("Error updating locale {}", locale, e);
        }
    }

    private List<PhraseLocale> getLocales(final String projectId, final String branch)
    {
        final Collection<PhraseProject> phraseProjects =
            Collections2.filter(getPhraseProjects(), phraseProject -> Objects.requireNonNull(phraseProject).getProjectId().equals(projectId));

        if (phraseProjects.isEmpty())
        {
            LOG.warn("No locales found for projectId: " + projectId);
            return null;
        }
        final PhraseProject phraseProject = phraseProjects.iterator().next();

        final Collection<PhraseBranch> phraseBranches =
            Collections2.filter(phraseProject.getBranches(), phraseBranch -> Objects.requireNonNull(phraseBranch).getBranchName().equals(branch));

        if (phraseBranches.isEmpty())
        {
            LOG.warn("No branches found for projectId: " + projectId);
            return null;
        }

        final PhraseBranch phraseBranch = phraseBranches.iterator().next();
        return phraseBranch.getLocales();
    }


    private void checkAndGetPhraseLocales()
    {
        if (phraseProjects == null)
        {
            initLocales();
        }
    }

    private void initLocales()
    {
        LOG.debug("Start: Initialize all locales for projectIds: " + projectIdString + " and branches: " + branchesString);
        phraseProjects = localeAPI.listLocales(projectIds, branches);
        LOG.trace("Locales are successfully retreived: " + Joiner.on(",").join(phraseProjects));
        LOG.debug("End: Initialize all locales for projectIds: " + projectIdString + " and branches: " + branchesString);
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
