package com.sima.dms.repository;

import com.sima.dms.domain.entity.document.OtherDocumentFile;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OtherDocumentFileRepository extends PagingAndSortingRepository<OtherDocumentFile, Long> {

    @Modifying
    @Transactional
    @Query(value = "update OtherDocumentFile set file.uuid=:uuid where id=:id")
    void setFile(Long id, String uuid);

    @Transactional
    @Modifying
    @Query(value = "update OtherDocumentFile d set d.file.uuid = :newUuid where d.file.uuid = :oldUuid")
    void updateUuid(String oldUuid, String newUuid );
}
