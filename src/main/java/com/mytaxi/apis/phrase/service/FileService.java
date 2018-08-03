package com.mytaxi.apis.phrase.service;

import com.mytaxi.apis.phrase.domainobject.translation.PhraseTranslation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import static com.google.common.base.MoreObjects.firstNonNull;

public class FileService
{
    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);

    private static final String MESSAGE_FILE_PREFIX = "messages_";
    private static final String MESSAGE_FILE_POSTFIX = ".properties";
    private static final String GENERATED_RESOURCES_FOLDERNAME = "generated-resources/";
    private static final String MESSAGES_FOLDERNAME = "messages/";

    private String messageFilePrefix = null;
    private String messageFilePostfix = null;
    private String generatedResourcesFoldername = null;
    private String messagesFoldername = null;
    private File messagesDirectory;

    private static final String PROJECT_ID_PLACEHOLDER = "{projectid}";


    public FileService()
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




    public void saveToFile(final String projectId, final byte[] translationByteArray, final String locale)
    {
        LOG.debug("Start: saving translations to file for projectId: " + projectId);
        if (messagesDirectory == null)
        {
            initMessageDirectory();
        }

        final String fileName = createFileName(locale);

        final String messagesFoldername = createMessagesFoldername(projectId);
        final File messageFolderPath = getOrCreateFolder(messagesFoldername);
        FileOutputStream out = null;
        try
        {
            final File file = getOrCreateFile(fileName, messageFolderPath);
            out = new FileOutputStream(file);
            out.write(translationByteArray);
        }
        catch (final Exception ex)
        {
            String exMessage = ex.getMessage();
            String errorMessage = exMessage != null ? exMessage : ex.getClass().getCanonicalName();
            LOG.error("Error due handling messages-files - " + errorMessage, ex);
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (final IOException ex)
                {
                    LOG.error("Error due closing FileOutputStream", ex);
                }
            }
        }

        LOG.debug("End: saving translations to file for projectId: " + projectId);
    }


    private void initMessageDirectory()
    {
        messagesDirectory = new File(firstNonNull(generatedResourcesFoldername, GENERATED_RESOURCES_FOLDERNAME));
        if (!messagesDirectory.exists())
        {
            messagesDirectory.mkdirs();
        }
    }


    private File getOrCreateFolder(final String messagesFoldername)
    {
        final File messageFolderPath = new File(messagesDirectory.getAbsolutePath() + File.separator + messagesFoldername);
        if (!messageFolderPath.exists())
        {
            messageFolderPath.mkdirs();
        }
        return messageFolderPath;
    }


    private File getOrCreateFile(final String fileName, final File messageFolderPath) throws IOException
    {
        final File file = new File(messageFolderPath.getAbsolutePath() + File.separator + fileName);
        if (!file.exists())
        {
            file.createNewFile();
        }
        return file;
    }


    private Properties createProperties(final List<PhraseTranslation> translations)
    {
        final Properties prop = new Properties();

        for (final PhraseTranslation translation : translations)
        {
            prop.setProperty(translation.getKey(), translation.getTranslation());
        }
        return prop;
    }


    private String createMessagesFoldername(final String projectId)
    {
        return firstNonNull(messagesFoldername, MESSAGES_FOLDERNAME).replace(PROJECT_ID_PLACEHOLDER, projectId);
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
        return generatedResourcesFoldername;
    }


    public String getMessageFilePostfix()
    {
        return messageFilePostfix;
    }


    public String getMessageFilePrefix()
    {
        return messageFilePrefix;
    }


    public String getMessagesFoldername()
    {
        return messagesFoldername;
    }
}
