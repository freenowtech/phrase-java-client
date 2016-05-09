package com.mytaxi.apis.phrase.tasks;

import com.mytaxi.apis.phrase.config.TestConfig;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseLocale;
import com.mytaxi.apis.phrase.domainobject.locale.PhraseProjectLocale;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhraseAppSyncTaskTest
{
    private TestConfig cfg;


    @Before
    public void beforeTest()
    {
        cfg = ConfigFactory.create(TestConfig.class, System.getProperties(), System.getenv());
    }


    @Test
    public void testRun() throws Exception
    {

        //
        String uuid = UUID.randomUUID().toString();

        //
        PhraseAppSyncTask phraseAppSyncTask = new PhraseAppSyncTask(cfg.authToken(), cfg.projectId());
        phraseAppSyncTask.setMessagesFoldername(uuid);
        phraseAppSyncTask.run();

        // assert we found some locales
        List<PhraseProjectLocale> phraseProjectLocales = phraseAppSyncTask.getPhraseLocales();
        assertEquals(1, phraseProjectLocales.size());

        PhraseProjectLocale phraseProjectLocale = phraseProjectLocales.get(0);
        List<PhraseLocale> phraseLocales = phraseProjectLocale.getLocales();
        assertTrue(phraseLocales.size() >= 11);

        //
        File folder = new File("./target/test-classes/" + uuid);
        assertTrue(folder.canRead());

        for (PhraseLocale phraseLocale : phraseLocales)
        {
            System.out.println(phraseLocale);

            File messagesFile = new File(folder, "messages_" + phraseLocale.getCode() + ".properties");
            assertTrue(messagesFile.canRead());

            try (InputStream in = new FileInputStream(messagesFile))
            {
                Properties properties = new Properties();
                properties.load(in);

                assertFalse(properties.isEmpty());

                checkSome(phraseLocale, properties);
            }

        }

    }


    private void checkSome(PhraseLocale phraseLocale, Properties properties)
    {

        // some arbitrary test
        if (phraseLocale.getCode().equals("ca"))
        {
            String translation = properties.getProperty("incentiveservice.incentive.pricing.description");
            assertEquals("Crédito bonus fin de semana - sábado y domingo- número de identificación: {0}", translation);
        }

        // check Double apostrophe
        if (phraseLocale.getCode().equals("en"))
        {
            String translation = properties.getProperty("SERVER_ABORT");
            assertEquals("Unfortunately, we haven't found a taxi for you.", translation);
        }

    }

}