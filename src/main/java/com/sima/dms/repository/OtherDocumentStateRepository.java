package com.sima.dms.repository;

import com.sima.dms.domain.entity.document.OtherDocumentState;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OtherDocumentStateRepository extends PagingAndSortingRepository<OtherDocumentState,Long> {

    List<OtherDocumentState> findAllByOtherDocumentIdOrderByRegisterDate(Long id);
}
