package com.sima.dms.repository;

import com.sima.dms.domain.entity.document.DocumentState;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentStateRepository extends PagingAndSortingRepository<DocumentState, Long> {

    List<DocumentState> findAllByDocumentId(Long documentId);
}
