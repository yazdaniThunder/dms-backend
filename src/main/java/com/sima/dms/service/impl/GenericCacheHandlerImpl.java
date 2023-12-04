package com.sima.dms.service.impl;

import com.sima.dms.domain.dto.DocumentTypeDto;
import com.sima.dms.domain.dto.response.BranchListDto;
import com.sima.dms.domain.entity.DocumentType;
import com.sima.dms.repository.BranchRepository;
import com.sima.dms.repository.DocumentOcrRepository;
import com.sima.dms.repository.DocumentTypeRepository;
import com.sima.dms.service.GenericCacheHandler;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@EnableScheduling
@AllArgsConstructor
public class GenericCacheHandlerImpl implements GenericCacheHandler {

    private final BranchRepository branchRepository;
    private final DocumentOcrRepository documentOcrRepository;
    private final DocumentTypeRepository documentTypeRepository;

    private List<Long> branchCodes;
    private List<String> similarTitles;
    private List<BranchListDto> branchListDto;
    private List<DocumentType> documentTypeList;

    @PostConstruct
    private void intializeBudgetState() {
        this.branchCodes = branchRepository.getBranchCodes();
        this.similarTitles = documentOcrRepository.getDocumentSimilarTitles();
        this.branchListDto = branchRepository.findAllByActiveIsTrue();
        this.documentTypeList= documentTypeRepository.findAll();
    }


    @Override
    public List<Long> branchCodes() {
        return this.branchCodes;
    }

    @Override
    public List<String> getSimilarTitles() {
        return this.similarTitles;
    }

    @Override
    public List<BranchListDto> getBranchListDto() {
        return this.branchListDto;
    }

    @Override
    public List<DocumentType> getDocumentTypeList() {
        return this.documentTypeList;
    }
}