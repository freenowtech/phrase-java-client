package com.freenow.apis.phrase.api.tag.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalePreviewDTO {

    private String id;

    private String name;

    private String code;

    private LocalePreviewWithStatsStatisticsDTO statistics;

    public LocalePreviewDTO() {
    }

    public LocalePreviewDTO(
            final String id,
            final String name,
            final String code,
            final LocalePreviewWithStatsStatisticsDTO statistics) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.statistics = statistics;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public LocalePreviewWithStatsStatisticsDTO getStatistics() {
        return statistics;
    }

    public void setStatistics(LocalePreviewWithStatsStatisticsDTO statistics) {
        this.statistics = statistics;
    }

    @Override
    public String toString() {
        return String.format("LocalePreviewDTO{id='%s', name='%s', code='%s'}", id, name, code);
    }
}
