package com.sima.dms.repository;

import com.sima.dms.domain.entity.document.OtherDocument;
import com.sima.dms.domain.enums.OtherDocumentStateEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OtherDocumentRepository extends PagingAndSortingRepository<OtherDocument, Long> {

    Long countByLastState_StateInAndBranch_IdIn(List<OtherDocumentStateEnum> state, List<Long> branchIds);

    List<OtherDocument> findAllByIdIn(List<Long> ids);

    Boolean existsByIdAndLastState_State(Long id, OtherDocumentStateEnum state);

    @Query("select distinct d.branch.id from OtherDocument d where d.id in (:otherDocumentIds)")
    List<Long> getCreatorBranchIds(@Param("otherDocumentIds") List<Long> otherDocumentIds);

    @Query(value = "select distinct od from  OtherDocument od  " +
            " left join OtherDocumentFile otf on od.id = otf.otherDocument.id" +
            " left join OtherDocumentType ott on otf.otherDocumentType.id = ott.id" +
            " left join FileStatus fs on otf.fileStatus.id = fs.id" +
            " where ( DATE(od.registerDate) >= :registerFromDate or :registerFromDate is null ) and ( DATE(od.registerDate) <= :registerToDate or :registerToDate is null) " +
            " and (od.customerNumber like concat('%',:customerNumber,'%')  or :customerNumber is null ) and (od.fileNumber like concat('%',:fileNumber,'%') or :fileNumber is null)" +
            " and ( od.fileType.id = :fileTypeId  or :fileTypeId is null ) and (ott.id  = :otherDocumentTypeId or :otherDocumentTypeId is null) " +
            " and (fs.id = :fileStatusId or :fileStatusId is null ) and (od.createdBy.user.id = :registrarId or :registrarId is null) " +
            " and (od.branch.id in (:branchIds) or COALESCE(:branchIds) is null) and ( od.lastState.state =:state or :state is null )")
    Page<OtherDocument> advanceSearch(@Param("registerFromDate") Date registerFromDate, @Param("registerToDate") Date registerToDate, @Param("customerNumber") String customerNumber,
                                      @Param("fileNumber") String fileNumber, @Param("fileTypeId") Long fileTypeId, @Param("otherDocumentTypeId") Long otherDocumentTypeId, @Param("fileStatusId") Long fileStatusId,
                                      @Param("registrarId") Long registrarId, @Param("branchIds") List<Long> branchIds, @Param("state") OtherDocumentStateEnum state, Pageable pageable);

    @Query(value = " select od from OtherDocument od where od.fileType.id = :id and od.lastState.state in (:states) ")
    List<OtherDocument> getAllByFileTypeId(Long id, List<OtherDocumentStateEnum> states);
}
