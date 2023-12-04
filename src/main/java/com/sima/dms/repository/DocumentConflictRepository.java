package com.sima.dms.repository;


import com.sima.dms.domain.entity.document.DocumentConflict;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentConflictRepository extends PagingAndSortingRepository<DocumentConflict,Long> {

    DocumentConflict findByDocument_Id(Long documentId);
}
