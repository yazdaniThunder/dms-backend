package com.sima.dms.repository;

import com.sima.dms.domain.entity.baseinformation.ConflictReason;
import com.sima.dms.domain.enums.ConflictTypeEnum;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface ConflictReasonRepository extends PagingAndSortingRepository<ConflictReason, Long> {

    List<ConflictReason> getAllByTypeAndDocumentSetType(ConflictTypeEnum type, DocumentSetTypeEnum documentSetType);

    List<ConflictReason> getAllByType(ConflictTypeEnum type);

    @Modifying
    @Transactional
    @Query("update ConflictReason set active=:active where id in :ids")
    void updateActive(@Param("ids") List<Long> ids, @Param("active") boolean active);

    @Query("select dscr from ConflictReason dscr where (dscr.reason like CONCAT ('%',:reason,'%') or :reason is null ) and ( dscr.documentSetType= :documentSetType or :documentSetType is null ) " +
            " and  ( dscr.type = :type or :type is null) and ( dscr.createdBy.id= :userId or :userId is null) and  ( DATE(dscr.registerDate) >= :regDateFrom or :regDateFrom is null ) " +
            " and ( DATE(dscr.registerDate) <= :regDateTo or :regDateTo is null) ")
    Page<ConflictReason> search(
            @Param("userId") Long userId,
            @Param("reason") String reason,
            @Param("documentSetType") DocumentSetTypeEnum documentSetType,
            @Param("type") ConflictTypeEnum type,
            @Param("regDateFrom") Date regDateFrom,
            @Param("regDateTo") Date regDateTo, Pageable pageable);

}
