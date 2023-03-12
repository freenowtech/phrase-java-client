package com.freenow.apis.phrase.api.tag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class PhraseTagWithStatsDTO {

    private String name;

    @JsonProperty("keys_count")
    private Integer keysCount;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("updated_at")
    private Date updatedAt;

    private List<TagWithStatsStatisticsDTO> statistics;

    public PhraseTagWithStatsDTO() {
    }

    public PhraseTagWithStatsDTO(
            final String name,
            final Integer keysCount,
            final Date createdAt,
            final Date updatedAt,
            final List<TagWithStatsStatisticsDTO> statistics
    ) {
        this.name = name;
        this.keysCount = keysCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.statistics = statistics;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getKeysCount() {
        return keysCount;
    }

    public void setKeysCount(final Integer keysCount) {
        this.keysCount = keysCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<TagWithStatsStatisticsDTO> getStatistics() {
        return statistics;
    }

    public void setStatistics(final List<TagWithStatsStatisticsDTO> statistics) {
        this.statistics = statistics;
    }
}
