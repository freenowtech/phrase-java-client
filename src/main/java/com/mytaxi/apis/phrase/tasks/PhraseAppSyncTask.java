package com.mytaxi.apis.phrase.tasks;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.isadounikau.phrase.api.client.PhraseApiClient;
import com.isadounikau.phrase.api.client.PhraseApiClientImpl;
import com.isadounikau.phrase.api.client.model.PhraseLocales;
import com.mytaxi.apis.phrase.api.format.Format;
import com.mytaxi.apis.phrase.api.locale.PhraseLocaleAPI;
import com.mytaxi.apis.phrase.api.localedownload.PhraseLocaleDownloadAPI;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseBranch;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseLocale;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseProject;
import com.mytaxi.apis.phrase.exception.PhraseAppApiException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mytaxi.apis.phrase.api.localedownload.PhraseLocaleDownloadAPI.DEFAULT_FILE_FORMAT;
import static com.mytaxi.apis.phrase.api.localedownload.PhraseLocaleDownloadAPI.DEFAULT_BRANCH;

public class PhraseAppSyncTask implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(PhraseAppSyncTask.class);

    // init
    private final List<String> projectIds;
    private final List<String> branches;
    private final PhraseApiClient client;
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
        branches = Collections.singletonList(DEFAULT_BRANCH);
        client = new PhraseApiClientImpl("https://api.phraseapp.com", authToken);
        projectIdString = Joiner.on(",").join(projectIds);
        branchesString = Joiner.on(",").join(branches);
        fileService = new FileService();
        LOG.debug("Initialized PhraseAppSyncTask with following projectIds: " + projectIdString + " and branches: " + branchesString);
    }


    /*
      authToken -
      projectId -
      scheme - http or https
      host - host of api
    */
    public PhraseAppSyncTask(final String authToken, final String projectId, final String scheme, final String host)
    {
        this(authToken, projectId, Collections.singletonList(DEFAULT_BRANCH), scheme, host);
    }


    public PhraseAppSyncTask(final String authToken, final String projectId, final List<String> branches, final String scheme, final String host)
    {
        projectIds = Collections.singletonList(projectId);
        this.branches = branches;
        URIBuilder builder = new URIBuilder();
        builder.setScheme(scheme);
        builder.setHost(host);
        try
        {
            client = new PhraseApiClientImpl(builder.build().toURL().toString(), authToken);
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException("Unsupported arguments schema:[" + scheme + "] host:[" + host + "]", ex);
        }
        projectIdString = Joiner.on(",").join(projectIds);
        branchesString = Joiner.on(",").join(branches);
        fileService = new FileService();
        LOG.debug("Initialized PhraseAppSyncTask with following projectIds: " + projectIdString + " and branches: " + branchesString);
    }


    public PhraseAppSyncTask(final String authToken, final String projectId, PhraseLocaleAPI localeApi, PhraseLocaleDownloadAPI localeDownloadAPI, FileService fileService)
    {
        this(authToken, projectId, Arrays.asList(DEFAULT_BRANCH), localeApi, localeDownloadAPI, fileService);
    }


    @Deprecated
    public PhraseAppSyncTask(
        final String authToken, final String projectId, final List<String> branches, PhraseLocaleAPI localeApi, PhraseLocaleDownloadAPI localeDownloadAPI,
        FileService fileService)
    {
        Preconditions.checkNotNull(authToken);
        Preconditions.checkNotNull(projectId);
        this.branches = branches;
        this.projectIds = Collections.singletonList(projectId);
        this.client = new PhraseApiClientImpl("https://api.phraseapp.com", authToken);
        this.projectIdString = Joiner.on(",").join(projectIds);
        branchesString = Joiner.on(",").join(branches);
        this.fileService = fileService;
        LOG.debug("Initialized PhraseAppSyncTask with following projectIds: " + projectIdString + " and branches: " + branchesString);
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
                    .forEach(branch -> updateBranchLocales(projectId, branch)));

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


    private void updateBranchLocales(final String projectId, final String branch)
    {
        final List<PhraseLocale> locales = getLocales(projectId, branch);

        if (locales != null)
        {
            for (final PhraseLocale locale : locales)
            {
                updateLocale(projectId, branch, locale);
            }
        }
    }


    private void updateLocale(String projectId, String branch, PhraseLocale locale)
    {
        try
        {
            Optional<NameValuePair> v = DEFAULT_FILE_FORMAT.getOptions().stream().filter(it -> "escape_single_quotes".equals(it.getName())).findFirst();
            boolean escapeSingleQuotes = false;
            if (v.isPresent())
            {
                escapeSingleQuotes = Boolean.parseBoolean(v.get().getValue());
            }
            byte[] translationByteArray = client.downloadLocaleAsProperties(projectId, locale.getId(), escapeSingleQuotes, branch);
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
        String projectId = projectIds.get(0);

        List<PhraseBranch> list = new ArrayList<>();
        for (String branch : branches)
        {
            PhraseLocales locales = client.locales(projectId, branch);
            List<PhraseLocale> oldLocales = Objects.requireNonNull(locales)
                .stream()
                .map(it -> PhraseLocale.newBuilder().withId(it.getId()).withCode(it.getCode()).withName(it.getName()).build())
                .collect(Collectors.toList());
            PhraseBranch branchObject = PhraseBranch.newBuilder().withBranchName(branch).withLocales(oldLocales).build();
            list.add(branchObject);
        }
        PhraseProject project = PhraseProject.newBuilder().withProjectId(projectId).withBranches(list).build();

        phraseProjects = Collections.singletonList(project);
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
