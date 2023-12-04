package com.sima.dms.repository;

import com.sima.dms.domain.entity.baseinformation.DocumentRequestReason;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface DocumentRequestReasonRepository extends PagingAndSortingRepository<DocumentRequestReason , Long> {
    List<DocumentRequestReason> findAllByActiveIsTrue();

    @Modifying
    @Transactional
    @Query("update DocumentRequestReason set active=:active where id in :ids")
    void updateActive(@Param("ids") List<Long> ids, @Param("active") boolean active);
}
