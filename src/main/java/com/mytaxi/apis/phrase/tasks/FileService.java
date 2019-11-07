package com.mytaxi.apis.phrase.tasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import static com.google.common.base.MoreObjects.firstNonNull;

class FileService
{
    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);

    private static final String MESSAGE_FILE_PREFIX = "messages_";
    private static final String MESSAGE_FILE_POSTFIX = ".properties";
    private static final String GENERATED_RESOURCES_FOLDERNAME = "generated-resources/";
    private static final String MESSAGES_FOLDERNAME_MASTER = "messages/";
    private static final String MESSAGES_FOLDERNAME_BRANCH = "messages_{branch}/";
    private static final String MASTER_BRANCH = "master";

    private String messageFilePrefix = null;
    private String messageFilePostfix = null;
    private String generatedResourcesFoldername = null;
    private String messagesFoldername = null;
    private Path messagesDirectory;

    private static final String PROJECT_ID_PLACEHOLDER = "{projectid}";
    private static final String BRANCH_PLACEHOLDER = "{branch}";

    public void saveToFile(final String projectId, final byte[] translationByteArray, final String locale) throws IOException
    {
        saveToFile(projectId, MASTER_BRANCH, translationByteArray, locale);
    }


    public void saveToFile(final String projectId, String branch, final byte[] translationByteArray, final String locale) throws IOException
    {
        LOG.debug("Start: saving translations to file for projectId: {}", projectId);
        if (messagesDirectory == null)
        {
            initMessageDirectory();
        }

        final String fileName = createFileName(locale);

        final String messagesFoldername = createMessagesFoldername(projectId, branch);
        final Path messageFolderPath = getOrCreateFolder(messagesFoldername);

        try
        {
            Path path = getOrCreateFile(messageFolderPath, fileName);
            Files.write(path, translationByteArray);

        }
        catch (final Exception ex)
        {
            String exMessage = ex.getMessage();
            String errorMessage = exMessage != null ? exMessage : ex.getClass().getCanonicalName();
            LOG.error("Error due handling messages-files - " + errorMessage, ex);
        }

        LOG.debug("End: saving translations to file for projectId: " + projectId);
    }


    private void initMessageDirectory() throws IOException
    {
        messagesDirectory = Paths.get(firstNonNull(getGeneratedResourcesFoldername(), GENERATED_RESOURCES_FOLDERNAME));
        Files.createDirectories(messagesDirectory);
    }


    private Path getOrCreateFolder(String messagesFoldername) throws IOException
    {
        Path messageFolderPath = messagesDirectory.resolve(messagesFoldername);
        Files.createDirectories(messageFolderPath);
        return messageFolderPath;
    }


    private Path getOrCreateFile(final Path messageFolderPath, final String fileName) throws IOException
    {
        Path path = messageFolderPath.resolve(fileName);
        if (!Files.exists(path))
        {
            Files.createFile(path);
        }
        return path;
    }


    private String createMessagesFoldername(final String projectId, final String branch)
    {
        if (MASTER_BRANCH.equals(branch))
        {
            return firstNonNull(messagesFoldername, MESSAGES_FOLDERNAME_MASTER)
                .replace(PROJECT_ID_PLACEHOLDER, projectId);
        }

        return firstNonNull(messagesFoldername, MESSAGES_FOLDERNAME_BRANCH)
            .replace(PROJECT_ID_PLACEHOLDER, projectId)
            .replace(BRANCH_PLACEHOLDER, branch);
    }


    private String createFileName(final String code)
    {
        return firstNonNull(messageFilePrefix, MESSAGE_FILE_PREFIX) + code + firstNonNull(messageFilePostfix, MESSAGE_FILE_POSTFIX);
    }


    public void setGeneratedResourcesFoldername(final String generatedResourcesFoldername)
    {
        this.generatedResourcesFoldername = generatedResourcesFoldername;
    }


    public void setMessagesFoldername(final String messagesFoldername)
    {
        this.messagesFoldername = messagesFoldername;
    }


    public void setMessageFilePostfix(final String messageFilePostfix)
    {
        this.messageFilePostfix = messageFilePostfix;
    }


    public void setMessageFilePrefix(final String messageFilePrefix)
    {
        this.messageFilePrefix = messageFilePrefix;
    }


    public String getGeneratedResourcesFoldername()
    {
        if (generatedResourcesFoldername == null)
        {
            try
            {
                generatedResourcesFoldername = new ClassPathResource("/").getFile().getPath();
            }
            catch (final Exception e)
            {
                LOG.error("could not get default ClassPathResource. use /generated-resources/ instead");
            }
        }

        return generatedResourcesFoldername;
    }

}
