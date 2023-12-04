package com.sima.dms.service;

import com.sima.dms.domain.dto.response.BranchListDto;
import com.sima.dms.domain.entity.DocumentType;

import java.util.List;

public interface GenericCacheHandler {

    List<Long> branchCodes();

    List<String> getSimilarTitles();

    List<BranchListDto> getBranchListDto();

    List<DocumentType> getDocumentTypeList();
}