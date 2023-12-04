package com.sima.dms.domain.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class FolderOcrDto {

    private int documentCount;
    private int matchesCount = 0;
    private int nonMatchesCount = 0;
    private List<String> MatchedDocuments = new ArrayList<>();
    private List<String> nonMatchedDocuments = new ArrayList<>();

    public FolderOcrDto(int documentCount) {
        this.documentCount = documentCount;
    }

    public void increaseMatchesCount() {
        this.matchesCount++;
    }

    public void increaseNonMatchesCount() {
        this.nonMatchesCount++;
    }

    public void addMatchedDocuments(String uuid) {
        this.MatchedDocuments.add(uuid);
    }

    public void addNonMatchedDocuments(String uuid) {
        this.nonMatchedDocuments.add(uuid);
    }
}
