package com.sima.dms.repository;

import com.sima.dms.domain.entity.documentSet.DocumentSetState;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentSetStateRepository extends PagingAndSortingRepository<DocumentSetState, Long> {

    List<DocumentSetState> findAllByDocumentSet_Id(Long documentSetId);
}
