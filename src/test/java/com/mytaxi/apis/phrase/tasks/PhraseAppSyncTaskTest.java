package com.mytaxi.apis.phrase.tasks;

import com.google.common.collect.ImmutableList;
import com.mytaxi.apis.phrase.api.format.Format;
import com.mytaxi.apis.phrase.api.format.JavaPropertiesFormat;
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
    public void testJavaPropertiesFormat_escapeSingleQuotesTrue() throws Exception
    {
        JavaPropertiesFormat format = JavaPropertiesFormat.newBuilder()
            .setEscapeSingleQuotes(true)
            .build();

        List<ExpectedEntry> expectedEntries = ImmutableList.of(
            new ExpectedEntry("de", "special_chars_test", "Crédito bonus fin de semana - sábado y domingo- número de identificación: {0}"),
            new ExpectedEntry("de", "special_chars_apostrophe_test", "You can''t rate cancelled trips.") // check double apostrophe
        );

        testRun(format, expectedEntries);
    }


    @Test
    public void testJavaPropertiesFormat_escapeSingleQuotesFalse() throws Exception
    {
        JavaPropertiesFormat format = JavaPropertiesFormat.newBuilder()
            .setEscapeSingleQuotes(false)
            .build();

        List<ExpectedEntry> expectedEntries = ImmutableList.of(
            new ExpectedEntry("de", "special_chars_test", "Crédito bonus fin de semana - sábado y domingo- número de identificación: {0}"),
            new ExpectedEntry("de", "special_chars_apostrophe_test", "You can't rate cancelled trips.") // check single apostrophe
        );

        testRun(format, expectedEntries);
    }


    private void testRun(Format format, List<ExpectedEntry> expectedEntries) throws Exception
    {

        // use unique folder name to start from scratch in each run
        String uuid = UUID.randomUUID().toString();

        //
        PhraseAppSyncTask phraseAppSyncTask = new PhraseAppSyncTask(cfg.authToken(), cfg.projectId());
        phraseAppSyncTask.setMessagesFoldername(uuid);
        phraseAppSyncTask.setFormat(format);
        phraseAppSyncTask.run();

        // assert we found some locales
        List<PhraseProjectLocale> phraseProjectLocales = phraseAppSyncTask.getPhraseLocales();
        assertEquals(1, phraseProjectLocales.size());

        PhraseProjectLocale phraseProjectLocale = phraseProjectLocales.get(0);
        List<PhraseLocale> phraseLocales = phraseProjectLocale.getLocales();
        assertTrue(phraseLocales.size() >= 2);

        // check some files
        File folder = new File("./target/test-classes/" + uuid);
        assertTrue(folder.canRead());

        for (PhraseLocale phraseLocale : phraseLocales)
        {
            System.out.println(phraseLocale);

            File messagesFile = new File(folder, "messages_" + phraseLocale.getCode().replace('-', '_') + ".properties");
            assertTrue("file " + messagesFile.getAbsolutePath() + " could not be read", messagesFile.canRead());

            try (InputStream in = new FileInputStream(messagesFile))
            {
                Properties properties = new Properties();
                properties.load(in);

                assertFalse(properties.isEmpty());

                checkSome(phraseLocale, properties, expectedEntries);
            }
        }

    }


    private void checkSome(PhraseLocale phraseLocale, Properties properties, List<ExpectedEntry> expectedEntries)
    {
        for (ExpectedEntry expectedEntry : expectedEntries)
        {
            if (phraseLocale.getCode().equals(expectedEntry.phraseLocaleCode))
            {
                String translation = properties.getProperty(expectedEntry.key);
                assertEquals(expectedEntry.expectedTranslation, translation);
            }
        }
    }


    private static class ExpectedEntry
    {
        final String phraseLocaleCode;
        final String key;
        final String expectedTranslation;


        private ExpectedEntry(String phraseLocaleCode, String key, String expectedTranslation)
        {
            this.phraseLocaleCode = phraseLocaleCode;
            this.key = key;
            this.expectedTranslation = expectedTranslation;
        }
    }

}
