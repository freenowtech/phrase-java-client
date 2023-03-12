package com.freenow.apis.phrase.api.tag.dto;

public class TagWithStatsStatisticsDTO {

    private LocalePreviewDTO locale;

    public TagWithStatsStatisticsDTO() {
    }

    public TagWithStatsStatisticsDTO(final LocalePreviewDTO locale) {
        this.locale = locale;
    }

    public LocalePreviewDTO getLocale() {
        return locale;
    }

    public void setLocale(final LocalePreviewDTO locale) {
        this.locale = locale;
    }

    @Override
    public String toString() {
        return String.format("TagWithStatsStatisticsDTO{locale='%s'}", locale);
    }
}
