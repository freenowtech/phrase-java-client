package com.freenow.apis.phrase.tasks;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableList;
import com.freenow.apis.phrase.api.format.Format;
import com.freenow.apis.phrase.api.format.JavaPropertiesFormat;
import com.freenow.apis.phrase.config.TestConfig;
import com.freenow.apis.phrase.domainobject.locale.PhraseBranch;
import com.freenow.apis.phrase.domainobject.locale.PhraseLocale;
import com.freenow.apis.phrase.domainobject.locale.PhraseProject;
import com.freenow.apis.phrase.exception.PhraseAppSyncTaskException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhraseAppSyncTaskTest
{
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(9999));
    private TestConfig cfg;


    @Before
    public void beforeTest()
    {
        cfg = ConfigFactory.create(TestConfig.class, System.getProperties(), System.getenv());
    }


    @Test
    @Ignore
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
    @Ignore
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

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testInvalidBaseURL()
    {
        exceptionRule.expect(PhraseAppSyncTaskException.class);
        exceptionRule.expectMessage("Parsing base URL failed");
        new PhraseAppSyncTask(cfg.authToken(), cfg.projectId(), "https://phrase.local/api[]");
    }

    @Test
    public void testInvalidBaseURLScheme()
    {
        exceptionRule.expect(PhraseAppSyncTaskException.class);
        exceptionRule.expectMessage("Expect scheme in base URL to be 'http' or 'https', was 'htttps'");
        new PhraseAppSyncTask(cfg.authToken(), cfg.projectId(), "htttps://phrase.local/api");
    }


    private void testRun(Format format, List<ExpectedEntry> expectedEntries) throws Exception
    {

        // use unique folder name to start from scratch in each run
        String uuid = UUID.randomUUID().toString();

        //
        PhraseAppSyncTask phraseAppSyncTask = new PhraseAppSyncTask(cfg.authToken(), cfg.projectId(), cfg.scheme(), cfg.host());
        phraseAppSyncTask.setMessagesFoldername(uuid);
        phraseAppSyncTask.setFormat(format);
        phraseAppSyncTask.run();

        // assert we found some locales
        List<PhraseProject> phraseProjects = phraseAppSyncTask.getPhraseProjects();
        assertEquals(1, phraseProjects.size());

        List<PhraseBranch> phraseBranches = phraseProjects.get(0).getBranches();
        assertEquals(1, phraseBranches.size());

        List<PhraseLocale> phraseLocales = phraseBranches.get(0).getLocales();
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
