package com.freenow.apis.phrase.api.tag.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalePreviewWithStatsStatisticsDTO {

    @JsonProperty("keys_total_count")
    private Integer keysTotalCount;

    @JsonProperty("translations_completed_count")
    private Integer translationsCompletedCount;

    @JsonProperty("translations_unverified_count")
    private Integer translationsUnverifiedCount;

    @JsonProperty("keys_untranslated_count")
    private Integer keysUntranslatedCount;

    public LocalePreviewWithStatsStatisticsDTO() { }

    public LocalePreviewWithStatsStatisticsDTO(
            final Integer keysTotalCount,
            final Integer translationsCompletedCount,
            final Integer translationsUnverifiedCount,
            final Integer keysUntranslatedCount
    ) {
        this.keysTotalCount = keysTotalCount;
        this.translationsCompletedCount = translationsCompletedCount;
        this.translationsUnverifiedCount = translationsUnverifiedCount;
        this.keysUntranslatedCount = keysUntranslatedCount;
    }

    public Integer getKeysTotalCount() {
        return keysTotalCount;
    }

    public void setKeysTotalCount(Integer keysTotalCount) {
        this.keysTotalCount = keysTotalCount;
    }

    public Integer getTranslationsCompletedCount() {
        return translationsCompletedCount;
    }

    public void setTranslationsCompletedCount(Integer translationsCompletedCount) {
        this.translationsCompletedCount = translationsCompletedCount;
    }

    public Integer getTranslationsUnverifiedCount() {
        return translationsUnverifiedCount;
    }

    public void setTranslationsUnverifiedCount(Integer translationsUnverifiedCount) {
        this.translationsUnverifiedCount = translationsUnverifiedCount;
    }

    public Integer getKeysUntranslatedCount() {
        return keysUntranslatedCount;
    }

    public void setKeysUntranslatedCount(Integer keysUntranslatedCount) {
        this.keysUntranslatedCount = keysUntranslatedCount;
    }
}
