package com.sima.dms.repository;

import com.sima.dms.domain.entity.document.DocumentRequestState;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRequestStateRepository extends PagingAndSortingRepository<DocumentRequestState,Long> {
    List<DocumentRequestState> findAllByDocumentRequestIdOrderByRegisterDate(Long documentRequestId);
}
