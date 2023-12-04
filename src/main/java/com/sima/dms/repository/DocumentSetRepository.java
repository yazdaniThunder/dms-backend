package com.sima.dms.repository;

import com.sima.dms.domain.entity.documentSet.DocumentSet;
import com.sima.dms.domain.enums.DocumentSetStateEnum;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DocumentSetRepository extends PagingAndSortingRepository<DocumentSet, Long> {

    Long countByState_NameInAndBranch_idIn(List<DocumentSetStateEnum> states, List<Long> branchIds);

    List<DocumentSet> findAll();

    Page<DocumentSet> findAllByBranch_idIn(List<Long> ids, Pageable pageable);

    List<DocumentSet> findAllByIdIn(List<Long> ids);

    @Query("select distinct d.branch.id from DocumentSet d where d.id in (:documentSetIds)")
    List<Long> getCreatorBranchIds(@Param("documentSetIds") List<Long> documentSetIds);

    boolean existsByRowsNumberAndSequence(String rowsNumber, String sequence);

    @Query(" select d from DocumentSet d where d.state.name in (:states) and  d.branch.id in (:branchIds) ")
    Page<DocumentSet> getDocumentSetsByStatesAndBranchIds(@Param("states") List<DocumentSetStateEnum> states, @Param("branchIds") List<Long> branchIds, Pageable pageable);

    @Query(" select d from DocumentSet d where d.state.name in (:states)  ")
    Page<DocumentSet> getDocumentSetsByStates(@Param("states") List<DocumentSetStateEnum> states, Pageable pageable);

    Page<DocumentSet> findAllByState_NameInAndBranch_idIn(List<DocumentSetStateEnum> states, List<Long> branchIds, Pageable pageable);

    Page<DocumentSet> findAllByState_NameIn(List<DocumentSetStateEnum> states, Pageable pageable);

    List<DocumentSet> findByState_Name(DocumentSetStateEnum documentSetStateEnum);

    @Query(value = "select distinct ds from DocumentSet ds  inner join DocumentSetState dss on dss.documentSet.id = ds.id " +
            " left join FileStatus fs on ds.fileStatus.id = fs.id " +
            " left join FileType ft on fs.fileType.id = ft.id " +
            "where (ds.state.name in (:states) or COALESCE(:states) is null  ) and ( DATE( ds.fromDate)  >= :fromDate or :fromDate is null)" +
            " and ( DATE(ds.toDate) <= :toDate or :toDate is null) and ( DATE(ds.registerDate) >= :registerFromDate or :registerFromDate is null ) and ( DATE(ds.registerDate) <= :registerToDate or :registerToDate is null) " +
            " and (ds.branch.id in (:branchIds)) and (ds.type = :type or  :type is null) " +
            " and ( ds.createdBy.user.id = :registrarId or :registrarId is null ) " +
            " and (ds.customerNumber like concat('%',:customerNumber,'%')  or :customerNumber is null ) and (ds.fileNumber like concat('%',:fileNumber,'%') or :fileNumber is null)" +
            " and (ft.id  = :fileTypeId or :fileTypeId is null)  " +
            " and (fs.id = :fileStatusId or :fileStatusId is null ) " +
            " and ( ( DATE(dss.registerDate) >= :sentFromDate and dss.name = com.sima.dms.domain.enums.DocumentSetStateEnum.BRANCH_CONFIRMED ) or :sentFromDate is null )" +
            " and ( ( DATE(dss.registerDate) <= :sentToDate and dss.name = com.sima.dms.domain.enums.DocumentSetStateEnum.BRANCH_CONFIRMED ) or :sentToDate is null )" +
            " and ( ( dss.createdBy.user.id = :confirmerId and dss.name = com.sima.dms.domain.enums.DocumentSetStateEnum.BRANCH_CONFIRMED ) or :confirmerId is null )" +
            " and ( concat(ds.rowsNumber,ds.sequence) like concat('%',:rowNumber,'%') or :rowNumber is null)" +
            " and ( ( dss.createdBy.user.id = :scannerId and dss.name = com.sima.dms.domain.enums.DocumentSetStateEnum.SCANNED ) or :scannerId is null )" +
            " and (exists (select dr.id from ConflictReason dr where dr.reason like concat('%',:reason,'%') and dr.id in ( select cr.id from DocumentSetConflict ssd inner join ssd.conflictReasons cr where ssd.documentSet.id = ds.id ) ) or :reason is null )")
    Page<DocumentSet> advanceSearch(
            @Param("type") DocumentSetTypeEnum type,
            @Param("states") List<DocumentSetStateEnum> states,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("registerFromDate") Date registerFromDate,
            @Param("registerToDate") Date registerToDate,
            @Param("registrarId") Long registrarId,
            @Param("sentFromDate") Date sentFromDate,
            @Param("sentToDate") Date sentToDate,
            @Param("confirmerId") Long confirmerId,
            @Param("scannerId") Long scannerId,
            @Param("rowNumber") String rowNumber,
            @Param("branchIds") List<Long> branchIds,
            @Param("reason") String reason,
            @Param("customerNumber") String customerNumber,
            @Param("fileNumber") String fileNumber,
            @Param("fileStatusId") Long fileStatusId,
            @Param("fileTypeId") Long fileTypeId,
            Pageable pageable);


    @Query(value = "select distinct ds from DocumentSet ds  inner join DocumentSetState dss on dss.documentSet.id = ds.id " +
            " left join FileStatus fs on ds.fileStatus.id = fs.id " +
            " left join FileType ft on fs.fileType.id = ft.id " +
            "where (ds.state.name in (:states) or COALESCE(:states) is null  ) and ( DATE( ds.fromDate)  >= :fromDate or :fromDate is null)" +
            " and ( DATE(ds.toDate) <= :toDate or :toDate is null) and ( DATE(ds.registerDate) >= :registerFromDate or :registerFromDate is null ) and ( DATE(ds.registerDate) <= :registerToDate or :registerToDate is null) " +
            " and (ds.branch.id in (:branchIds) or COALESCE(:branchIds) is null) and (ds.type = :type or  :type is null) " +
            " and ( ds.createdBy.user.id = :registrarId or :registrarId is null ) " +
            " and (ds.customerNumber like concat('%',:customerNumber,'%')  or :customerNumber is null ) and (ds.fileNumber like concat('%',:fileNumber,'%') or :fileNumber is null)" +
            " and (ft.id  = :fileTypeId or :fileTypeId is null)  " +
            " and (fs.id = :fileStatusId or :fileStatusId is null ) " +
            " and ( ( DATE(dss.registerDate) >= :sentFromDate and dss.name = com.sima.dms.domain.enums.DocumentSetStateEnum.BRANCH_CONFIRMED ) or :sentFromDate is null )" +
            " and ( ( DATE(dss.registerDate) <= :sentToDate and dss.name = com.sima.dms.domain.enums.DocumentSetStateEnum.BRANCH_CONFIRMED ) or :sentToDate is null )" +
            " and ( ( dss.createdBy.user.id = :confirmerId and dss.name = com.sima.dms.domain.enums.DocumentSetStateEnum.BRANCH_CONFIRMED ) or :confirmerId is null )" +
            " and ( concat(ds.rowsNumber,ds.sequence) like concat('%',:rowNumber,'%') or :rowNumber is null)" +
            " and ( ( dss.createdBy.user.id = :scannerId and dss.name = com.sima.dms.domain.enums.DocumentSetStateEnum.SCANNED ) or :scannerId is null )" +
            " and (exists (select dr.id from ConflictReason dr where dr.reason like concat('%',:reason,'%') and dr.id in ( select cr.id from DocumentSetConflict ssd inner join ssd.conflictReasons cr where ssd.documentSet.id = ds.id ) ) or :reason is null )")
    Page<DocumentSet> advanceSearchAdminAndDoa(
            @Param("type") DocumentSetTypeEnum type,
            @Param("states") List<DocumentSetStateEnum> states,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("registerFromDate") Date registerFromDate,
            @Param("registerToDate") Date registerToDate,
            @Param("registrarId") Long registrarId,
            @Param("sentFromDate") Date sentFromDate,
            @Param("sentToDate") Date sentToDate,
            @Param("confirmerId") Long confirmerId,
            @Param("scannerId") Long scannerId,
            @Param("rowNumber") String rowNumber,
            @Param("branchIds") List<Long> branchIds,
            @Param("reason") String reason,
            @Param("customerNumber") String customerNumber,
            @Param("fileNumber") String fileNumber,
            @Param("fileStatusId") Long fileStatusId,
            @Param("fileTypeId") Long fileTypeId,
            Pageable pageable);

//    @Query(value = "select new com.sima.dms.domain.dto.documentSet.OcrDocumentReportDto( ds.id , ds.fileSize , ds.uploadStart , ds.uploadEnd , ds.ocrFinishedTime , ds.registerDate ," +
//            " count (nd.mimeType) , nd.mimeType , d.biStatus , ( select count (dd.id) from Document dd where dd.documentSet.id = ds.id ) as countAllDoc , ds.branch.branchCode , ds.branch.branchName ) " +
//            " from DocumentSet ds inner join Document d on ds.id =d.documentSet.id " +
//            " inner join DocumentSetState dss on ds.id = dss.documentSet.id " +
//            " inner join NodeDocument nd on d.file.uuid =nd.uuid " +
//            " where dss.name = com.sima.dms.domain.enums.DocumentSetStateEnum.PROCESSED  and ( DATE(ds.registerDate) >= :registerFromDate or :registerFromDate is null ) and ( DATE(ds.registerDate) <= :registerToDate or :registerToDate is null) " +
//            " group by ds.id, d.biStatus")
//    List<OcrDocumentReportDto> report(@Param("registerFromDate") Date registerFromDate,
//                                      @Param("registerToDate") Date registerToDate);


    @Query(value = "select max(cast( ds.sequence as int)) from DocumentSet ds where ds.branch.id = :branchId ")
    Integer getMaxSequenceByBranchId(@Param("branchId") Long branchId);
}
