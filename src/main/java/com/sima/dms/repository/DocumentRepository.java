package com.sima.dms.repository;

import com.sima.dms.domain.dto.response.DocumentOcrProcessDto;
import com.sima.dms.domain.entity.document.Document;
import com.sima.dms.domain.enums.BIStatusEnum;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import com.sima.dms.domain.enums.DocumentStateEnum;
import com.sima.dms.domain.enums.ProcessStateEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface DocumentRepository extends PagingAndSortingRepository<Document, Long> {


    Long countByState_NameAndDocumentSet_Branch_IdIn(DocumentStateEnum state, List<Long> branchIds);

    void deleteAllByDocumentSet_id(Long id);

    List<Document> findByDocumentSetId(Long documentSetId);

    List<Document> findAllByIdIn(List<Long> ids);

    @Query("select id from Document where documentSet.id =:documentSetId")
    List<Long> findAllDocumentSetId(@Param("documentSetId") Long documentSetId);

    @Query("select count(d) from Document d where d.documentSet.id =:documentSetId and  d.processStateEnum is null or d.processStateEnum <> 'PENDING'")
    int countAllByProcessState(@Param("documentSetId") Long documentSetId);

    @Query("select count(d) from Document d where d.documentSet.id =:documentSetId and d.file.textExtracted is true")
    int textExtractedDocumentsByDocumentSetIdCount(@Param("documentSetId") Long documentSetId);

    @Query("select distinct d.documentSet.branch.id from Document d where d.id in (:documentIds )")
    List<Long> getCreatorBranchIds(@Param("documentIds") List<Long> documentIds);

    @Query(value = "select state.name from Document where id=:id")
    DocumentStateEnum findStateName(Long id);

    @Query(value = "select file.uuid from Document where id=:id")
    String findFileUUuid(Long id);

    @Query(value = "select id from Document where state.name=:state and file.textExtracted=:textExtracted")
    List<Long> findAllByStateNameFileTextExtracted(DocumentStateEnum state, boolean textExtracted);

    @Transactional
    @Modifying
    @Query(value = "update Document set file.uuid=:uuid where id=:id")
    void setFile(Long id, String uuid);

    @Transactional
    @Modifying
    @Query(value = "update Document set maintenanceCode=:maintenanceCode where id=:id")
    void updateMaintenanceCode(Long id, String maintenanceCode);

    @Transactional
    @Modifying
    @Query(value = "update Document set processStateEnum=:processStateEnum where file.uuid =:uuid")
    void updateProcessState(String uuid, ProcessStateEnum processStateEnum);

    @Transactional
    @Modifying
    @Query(value = "update Document set processStateEnum=:processStateEnum where file.uuid in (:uuids)")
    void updateProcessState(List<String> uuids, ProcessStateEnum processStateEnum);

    @Modifying
    @Transactional
    @Query("update Document set processStateEnum =:processStateEnum , description =:description, ocrProcessTime=:ocrProcessTime, biProcessTime=:biProcessTime where file.uuid =:uuid")
    void updateProcessState(@Param("uuid") String uuid, @Param("description") String description, @Param("processStateEnum") ProcessStateEnum processStateEnum, @Param("ocrProcessTime") long ocrProcessTime, @Param("biProcessTime") long biProcessTime);

    @Query(value = "select file.uuid from Document where documentSet.id=:documentSetId")
    List<String> getDocumentSetFileUuids(@Param("documentSetId") Long documentSetId);

    Page<Document> findAllByState_NameInAndDocumentSet_Branch_IdIn(List<DocumentStateEnum> states, List<Long> branchIds, Pageable pageable);

    Page<Document> findAllByState_NameIn(List<DocumentStateEnum> states, Pageable pageable);

    Page<Document> findAllByState_Name(DocumentStateEnum states, Pageable pageable);

    Page<Document> findAllByState_NameAndDocumentSet_Branch_IdIn(DocumentStateEnum state, List<Long> branchIds, Pageable pageable);

    @Query(value = "select d.file.uuid from Document d join NodeDocument nd on d.file.uuid = nd.uuid where d.processStateEnum is null and nd.textExtracted is false and (d.file.uuid not in (:uuids) or coalesce(:uuids) is null ) order by  nd.created asc")
    List<String> ocrCandidate(@Param("uuids") List<String> uuids);

    @Query(value = "select d from Document d where d.documentSet.id = :documentSetId and (d.state.name = :state or :state is null )")
    List<Document> getAllByDocumentState(@Param("documentSetId") Long documentSetId, @Param("state") DocumentStateEnum state);

//    @Query(value = "select d.file.uuid from Document d join NodeDocument nd  on d.file.uuid = nd.uuid where nd.textExtracted is false and d.convertFlag is false and d.file.uuid not in (:uuids)")
//    List<String> ocrCandidate(Set<String> uuids, Pageable pageable);

//    @Query(value = "select d.file_NBS_UUID from DMS_DOCUMENT d join OKM_NODE_DOCUMENT nd on d.file_NBS_UUID = nd.NBS_UUID " +
//            " join OKM_NODE_BASE nb on nd.NBS_UUID = nb.NBS_UUID  where (d.CONVERT_FLAG='F' or d.CONVERT_FLAG is null) and nd.NDC_TEXT_EXTRACTED ='F' AND ( nd.NDC_MIME_TYPE LIKE CONCAT('%','pdf','%') or nd.NDC_MIME_TYPE LIKE CONCAT('%','image','%')  ) order by nb.NBS_CREATED asc Limit :limit ", nativeQuery = true)
//    List<String> getNotOcrDocuments(@Param("limit") int limit);

    @Query(value = "select tb1.*  " +
            " from (select d.* from DMS_DOCUMENT d inner join DMS_DOCUMENT_STATE ds on ds.id = d.state_id " +
            " join DMS_DOCUMENT_SET d_set on d_set.id = d.DOCUMENT_SET_ID" +
            " join DMS_PROFILE p on p.id = d_set.created_by " +
            " join DMS_BRANCH b on b.id = p.branch_id " +
            " where ( ds.STATUS = 4 or ds.STATUS= 7 or ( ds.STATUS = 0 and exists(select 1 from DMS_DOCUMENT_STATE dds where dds.document_id = d.id and dds.STATUS  = 7))) " +
            " and ( b.id in (:branchIds))) as tb1 " +
            " join DMS_DOCUMENT_SET d_set on d_set.id = tb1.DOCUMENT_SET_ID  " +
            " left join DMS_FILE_STATUS fs on fs.id = d_set.FILE_STATUS_ID  " +
            " left join DMS_FILE_TYPE ft on ft.id = fs.FILE_TYPE_ID  " +
            " join DMS_PROFILE p on p.id = d_set.created_by " +
            " join DMS_BRANCH b on b.id = p.branch_id " +
            " join DMS_DOCUMENT_STATE d_state on d_state.id = tb1.state_id  " +
            " join OKM_NODE_BASE nb on nb.nbs_uuid = tb1.file_NBS_UUID  " +
            " where ( tb1.MAINTENANCE_CODE like concat('%',:maintenanceCode,'%') or :maintenanceCode is null ) and (DATE( d_set.FROM_DATE ) >= :fromDate or :fromDate is null ) " +
            " and ( DATE( d_set.TO_DATE ) <= :toDate or :toDate is null ) and (DATE (tb1.register_date) >= :registerFromDate or :registerFromDate is null ) " +
            " and (DATE (tb1.register_date) <= :registerToDate or :registerToDate is null ) and (d_state.STATUS in (:states) or coalesce(:states) is null )" +
            " and ( nb.NBS_NAME like CONCAT ('%',:filename,'%')  or :filename is null ) and (d_set.type =:type or :type is null)" +
            " and (d_set.CUSTOMER_NUMBER like concat('%',:customerNumber,'%')  or :customerNumber is null ) and (d_set.FILE_NUMBER like concat('%',:fileNumber,'%') or :fileNumber is null)" +
            " and (ft.id  = :fileTypeId or :fileTypeId is null)  " +
            " and (fs.id = :fileStatusId or :fileStatusId is null ) " +
            " and (exists (select ddc.id from DMS_DOCUMENT_CONFLICT ddc where ddc.DOCUMENT_INFO_ID = tb1.id and DATE (ddc.register_date) = :conflictRegisterDate) or :conflictRegisterDate is null)" +
            " and (exists (select dr.id from DMS_CONFLICT_REASON dr where dr.REASON like concat('%',:reason,'%') and dr.id in ( select dcr.REASON_ID from DMS_DC_REASON dcr where dcr.DC_ID in (select ddc1.id from DMS_DOCUMENT_CONFLICT  ddc1 where ddc1.DOCUMENT_INFO_ID = tb1.id)  ) ) or :reason is null )",
            countQuery = "select count(tb1.id) " +
                    " from (select d.* from DMS_DOCUMENT d inner join DMS_DOCUMENT_STATE ds on ds.id = d.state_id " +
                    " join DMS_DOCUMENT_SET d_set on d_set.id = d.DOCUMENT_SET_ID" +
                    " join DMS_PROFILE p on p.id = d_set.created_by " +
                    " join DMS_BRANCH b on b.id = p.branch_id " +
                    " where ( ds.STATUS = 4 or ds.STATUS= 7 or ( ds.STATUS = 0 and exists(select 1 from DMS_DOCUMENT_STATE dds where dds.document_id = d.id and dds.STATUS  = 7))) " +
                    " and ( b.id in (:branchIds))) as tb1 " +
                    " join DMS_DOCUMENT_SET d_set on d_set.id = tb1.DOCUMENT_SET_ID  " +
                    " left join DMS_FILE_STATUS fs on fs.id = d_set.FILE_STATUS_ID  " +
                    " left join DMS_FILE_TYPE ft on ft.id = fs.FILE_TYPE_ID  " +
                    " join DMS_PROFILE p on p.id = d_set.created_by " +
                    " join DMS_BRANCH b on b.id = p.branch_id " +
                    " join DMS_DOCUMENT_STATE d_state on d_state.id = tb1.state_id  " +
                    " join OKM_NODE_BASE nb on nb.nbs_uuid = tb1.file_NBS_UUID  " +
                    " where ( tb1.MAINTENANCE_CODE like concat('%',:maintenanceCode,'%') or :maintenanceCode is null ) and (DATE( d_set.FROM_DATE ) >= :fromDate or :fromDate is null ) " +
                    " and ( DATE( d_set.TO_DATE ) <= :toDate or :toDate is null ) and (DATE (tb1.register_date) >= :registerFromDate or :registerFromDate is null ) " +
                    " and (DATE (tb1.register_date) <= :registerToDate or :registerToDate is null ) and (d_state.STATUS in (:states) or coalesce(:states) is null )" +
                    " and ( nb.NBS_NAME like CONCAT ('%',:filename,'%')  or :filename is null ) and (d_set.type =:type or :type is null)" +
                    " and (d_set.CUSTOMER_NUMBER like concat('%',:customerNumber,'%')  or :customerNumber is null ) and (d_set.FILE_NUMBER like concat('%',:fileNumber,'%') or :fileNumber is null)" +
                    " and (ft.id  = :fileTypeId or :fileTypeId is null)  " +
                    " and (fs.id = :fileStatusId or :fileStatusId is null ) " +
                    " and (exists (select ddc.id from DMS_DOCUMENT_CONFLICT ddc where ddc.DOCUMENT_INFO_ID = tb1.id and DATE (ddc.register_date) = :conflictRegisterDate) or :conflictRegisterDate is null)" +
                    " and (exists (select dr.id from DMS_CONFLICT_REASON dr where dr.REASON like concat('%',:reason,'%') and dr.id in ( select dcr.REASON_ID from DMS_DC_REASON dcr where dcr.DC_ID in (select ddc1.id from DMS_DOCUMENT_CONFLICT ddc1 where ddc1.DOCUMENT_INFO_ID = tb1.id)  ) ) or :reason is null )"
            , nativeQuery = true)
    Page<Document> findAllConflictingManagement(@Param("branchIds") List<Long> branchIds,
                                                @Param("maintenanceCode") String maintenanceCode,
                                                @Param("states") List<Integer> states,
                                                @Param("fromDate") Date fromDate,
                                                @Param("toDate") Date toDate,
                                                @Param("registerFromDate") Date registerFromDate,
                                                @Param("registerToDate") Date registerToDate,
                                                @Param("filename") String filename,
                                                @Param("type") Integer type,
                                                @Param("conflictRegisterDate") Date conflictRegisterDate,
                                                @Param("reason") String reason,
                                                @Param("customerNumber") String customerNumber,
                                                @Param("fileNumber") String fileNumber,
                                                @Param("fileStatusId") Long fileStatusId,
                                                @Param("fileTypeId") Long fileTypeId,
                                                Pageable pageable);


    @Query(value = "select tb1.*  from (select d.* from DMS_DOCUMENT d inner join DMS_DOCUMENT_STATE ds on ds.id = d.state_id " +
            " join DMS_DOCUMENT_SET d_set on d_set.id = d.DOCUMENT_SET_ID" +
            " join DMS_PROFILE p on p.id = d_set.created_by " +
            " join DMS_BRANCH b on b.id = p.branch_id " +
            " where ( ds.STATUS = 4 or ds.STATUS= 7 or ( ds.STATUS = 0 and exists(select 1 from DMS_DOCUMENT_STATE dds where dds.document_id = d.id and dds.STATUS  = 7))) " +
            " and ( b.id in (:branchIds) or " +
            " coalesce(:branchIds) is null )) as tb1 " +
            " join DMS_DOCUMENT_SET d_set on d_set.id = tb1.DOCUMENT_SET_ID  " +
            " left join DMS_FILE_STATUS fs on fs.id = d_set.FILE_STATUS_ID  " +
            " left join DMS_FILE_TYPE ft on ft.id = fs.FILE_TYPE_ID  " +
            " join DMS_PROFILE p on p.id = d_set.created_by " +
            " join DMS_BRANCH b on b.id = p.branch_id " +
            " join DMS_DOCUMENT_STATE d_state on d_state.id = tb1.state_id  " +
            " join OKM_NODE_BASE nb on nb.nbs_uuid = tb1.file_NBS_UUID  " +
            " where ( tb1.MAINTENANCE_CODE like concat('%',:maintenanceCode,'%') or :maintenanceCode is null ) and (DATE( d_set.FROM_DATE ) >= :fromDate or :fromDate is null ) " +
            " and ( DATE( d_set.TO_DATE ) <= :toDate or :toDate is null ) and (DATE (tb1.register_date) >= :registerFromDate or :registerFromDate is null ) " +
            " and (DATE (tb1.register_date) <= :registerToDate or :registerToDate is null ) and (d_state.STATUS in (:states) or coalesce(:states) is null )" +
            " and ( nb.NBS_NAME like CONCAT ('%',:filename,'%')  or :filename is null ) and (d_set.type =:type or :type is null) " +
            " and (d_set.CUSTOMER_NUMBER like concat('%',:customerNumber,'%')  or :customerNumber is null ) and (d_set.FILE_NUMBER like concat('%',:fileNumber,'%') or :fileNumber is null)" +
            " and (ft.id  = :fileTypeId or :fileTypeId is null)  " +
            " and (fs.id = :fileStatusId or :fileStatusId is null ) " +
            " and (exists (select ddc.id from DMS_DOCUMENT_CONFLICT ddc where ddc.DOCUMENT_INFO_ID = tb1.id and DATE (ddc.register_date) = :conflictRegisterDate) or :conflictRegisterDate is null)" +
            " and (exists (select dr.id from DMS_CONFLICT_REASON dr where dr.REASON like concat('%',:reason,'%') and dr.id in ( select dcr.REASON_ID from DMS_DC_REASON dcr where dcr.DC_ID in (select ddc1.id from DMS_DOCUMENT_CONFLICT  ddc1 where ddc1.DOCUMENT_INFO_ID = tb1.id)  ) ) or :reason is null )",
            countQuery = "select count(tb1.id)  from (select d.* from DMS_DOCUMENT d inner join DMS_DOCUMENT_STATE ds on ds.id = d.state_id " +
                    " join DMS_DOCUMENT_SET d_set on d_set.id = d.DOCUMENT_SET_ID" +
                    " join DMS_PROFILE p on p.id = d_set.created_by " +
                    " join DMS_BRANCH b on b.id = p.branch_id " +
                    " where ( ds.STATUS = 4 or ds.STATUS= 7 or ( ds.STATUS = 0 and exists(select 1 from DMS_DOCUMENT_STATE dds where dds.document_id = d.id and dds.STATUS  = 7))) " +
                    " and ( b.id in (:branchIds) or " +
                    " coalesce(:branchIds) is null )) as tb1 " +
                    " join DMS_DOCUMENT_SET d_set on d_set.id = tb1.DOCUMENT_SET_ID  " +
                    " left join DMS_FILE_STATUS fs on fs.id = d_set.FILE_STATUS_ID  " +
                    " left join DMS_FILE_TYPE ft on ft.id = fs.FILE_TYPE_ID  " +
                    " join DMS_PROFILE p on p.id = d_set.created_by " +
                    " join DMS_BRANCH b on b.id = p.branch_id " +
                    " join DMS_DOCUMENT_STATE d_state on d_state.id = tb1.state_id  " +
                    " join OKM_NODE_BASE nb on nb.nbs_uuid = tb1.file_NBS_UUID  " +
                    " where ( tb1.MAINTENANCE_CODE like concat('%',:maintenanceCode,'%') or :maintenanceCode is null ) and (DATE( d_set.FROM_DATE ) >= :fromDate or :fromDate is null ) " +
                    " and ( DATE( d_set.TO_DATE ) <= :toDate or :toDate is null ) and (DATE (tb1.register_date) >= :registerFromDate or :registerFromDate is null ) " +
                    " and (DATE (tb1.register_date) <= :registerToDate or :registerToDate is null ) and (d_state.STATUS in (:states) or coalesce(:states) is null )" +
                    " and ( nb.NBS_NAME like CONCAT ('%',:filename,'%')  or :filename is null ) and (d_set.type =:type or :type is null) " +
                    " and (d_set.CUSTOMER_NUMBER like concat('%',:customerNumber,'%')  or :customerNumber is null ) and (d_set.FILE_NUMBER like concat('%',:fileNumber,'%') or :fileNumber is null)" +
                    " and (ft.id  = :fileTypeId or :fileTypeId is null)  " +
                    " and (fs.id = :fileStatusId or :fileStatusId is null ) " +
                    " and (exists (select ddc.id from DMS_DOCUMENT_CONFLICT ddc where ddc.DOCUMENT_INFO_ID = tb1.id and DATE (ddc.register_date) = :conflictRegisterDate) or :conflictRegisterDate is null)" +
                    " and (exists (select dr.id from DMS_CONFLICT_REASON dr where dr.REASON like concat('%',:reason,'%') and dr.id in ( select dcr.REASON_ID from DMS_DC_REASON dcr where dcr.DC_ID in (select ddc1.id from DMS_DOCUMENT_CONFLICT  ddc1 where ddc1.DOCUMENT_INFO_ID = tb1.id)  ) ) or :reason is null )",
            nativeQuery = true)
    Page<Document> findAllConflictingManagementAdminAndDoa(@Param("branchIds") List<Long> branchIds,
                                                           @Param("maintenanceCode") String maintenanceCode,
                                                           @Param("states") List<Integer> states,
                                                           @Param("fromDate") Date fromDate,
                                                           @Param("toDate") Date toDate,
                                                           @Param("registerFromDate") Date registerFromDate,
                                                           @Param("registerToDate") Date registerToDate,
                                                           @Param("filename") String filename,
                                                           @Param("type") Integer type,
                                                           @Param("conflictRegisterDate") Date conflictRegisterDate,
                                                           @Param("reason") String reason,
                                                           @Param("customerNumber") String customerNumber,
                                                           @Param("fileNumber") String fileNumber,
                                                           @Param("fileStatusId") Long fileStatusId,
                                                           @Param("fileTypeId") Long fileTypeId,
                                                           Pageable pageable);


    @Query(value = "select distinct d from Document d " +
            " left join NodeProperty onp on onp.node.uuid = d.file.uuid " +
            " left join FileStatus fs on d.documentSet.fileStatus.id = fs.id " +
            " left join FileType ft on fs.fileType.id = ft.id " +
            " where (d.documentSet.state.name = com.sima.dms.domain.enums.DocumentSetStateEnum.PROCESSED or d.documentSet.state.name = com.sima.dms.domain.enums.DocumentSetStateEnum.COMPLETED )  " +
            " and (d.documentSet.type =:type or :type is null ) " +
            " and ( d.maintenanceCode like concat('%',:maintenanceCode,'%') or :maintenanceCode is null ) and (d.state.name in (:states) or COALESCE(:states) is null  ) and ( DATE( d.documentSet.fromDate)  >= :fromDate or :fromDate is null)" +
            " and ( DATE(d.documentSet.toDate) <= :toDate or :toDate is null) and ( DATE(d.registerDate) >= :registerFromDate or :registerFromDate is null ) and ( DATE(d.registerDate) <= :registerToDate or :registerToDate is null) " +
            " and (d.name like concat('%',:filename,'%')  or :filename is null ) and (d.documentSet.branch.id in (:branchIds) )" +
            " and (d.documentSet.customerNumber like concat('%',:customerNumber,'%')  or :customerNumber is null ) and (d.documentSet.fileNumber like concat('%',:fileNumber,'%') or :fileNumber is null)" +
            " and (ft.id  = :fileTypeId or :fileTypeId is null)  " +
            " and (fs.id = :fileStatusId or :fileStatusId is null ) " +
            " and ( concat(d.documentSet.rowsNumber,d.documentSet.sequence) like concat('%',:rowNumber,'%') or :rowNumber is null)" +
            " and (( onp.name = 'okp:Accounting.docNo' and onp.value like concat('%',:documentNumber,'%') ) or :documentNumber is null) " +
            " and ( onp.node.uuid  in ( select onpp.node.uuid from Document dd left join NodeProperty onpp on onpp.node.uuid = dd.file.uuid where ( onpp.name = 'okp:Accounting.date' and onpp.value like concat('%',:documentDate,'%'))) or :documentDate is null )" +
            " and (exists (select dr.id from ConflictReason dr where dr.reason like concat('%',:reason,'%') and dr.id in ( select cr.id from DocumentConflict ddc1 inner join ddc1.conflictReasons cr where ddc1.document.id = d.id ) ) or :reason is null )")
    Page<Document> advanceSearch(
            @Param("branchIds") List<Long> branchIds,
            @Param("maintenanceCode") String maintenanceCode,
            @Param("states") List<DocumentStateEnum> states,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("registerFromDate") Date registerFromDate,
            @Param("registerToDate") Date registerToDate,
            @Param("filename") String filename,
            @Param("documentNumber") String documentNumber,
            @Param("documentDate") String documentDate,
            @Param("reason") String reason,
            @Param("rowNumber") String rowNumber,
            @Param("type") DocumentSetTypeEnum type,
            @Param("customerNumber") String customerNumber,
            @Param("fileNumber") String fileNumber,
            @Param("fileStatusId") Long fileStatusId,
            @Param("fileTypeId") Long fileTypeId,
            Pageable pageable);

    @Query(value = "select distinct d from Document d " +
            " left join NodeProperty onp on onp.node.uuid = d.file.uuid " +
            " left join FileStatus fs on d.documentSet.fileStatus.id = fs.id " +
            " left join FileType ft on fs.fileType.id = ft.id " +
            " where (d.documentSet.state.name = com.sima.dms.domain.enums.DocumentSetStateEnum.PROCESSED or d.documentSet.state.name = com.sima.dms.domain.enums.DocumentSetStateEnum.COMPLETED )  " +
            " and (d.documentSet.type =:type or :type is null ) " +
            " and ( d.maintenanceCode like concat('%',:maintenanceCode,'%') or :maintenanceCode is null ) and (d.state.name in (:states) or COALESCE(:states) is null  ) and ( DATE( d.documentSet.fromDate)  >= :fromDate or :fromDate is null)" +
            " and ( DATE(d.documentSet.toDate) <= :toDate or :toDate is null) and ( DATE(d.registerDate) >= :registerFromDate or :registerFromDate is null ) and ( DATE(d.registerDate) <= :registerToDate or :registerToDate is null) " +
            " and (d.name like concat('%',:filename,'%')  or :filename is null ) and (d.documentSet.branch.id in (:branchIds) or COALESCE(:branchIds) is null )" +
            " and ( concat(d.documentSet.rowsNumber,d.documentSet.sequence) like concat('%',:rowNumber,'%') or :rowNumber is null)" +
            " and (d.documentSet.customerNumber like concat('%',:customerNumber,'%')  or :customerNumber is null ) and (d.documentSet.fileNumber like concat('%',:fileNumber,'%') or :fileNumber is null)" +
            " and (ft.id  = :fileTypeId or :fileTypeId is null)  " +
            " and (fs.id = :fileStatusId or :fileStatusId is null ) " +
            " and (( onp.name = 'okp:Accounting.docNo' and onp.value like concat('%',:documentNumber,'%') ) or :documentNumber is null) " +
            " and ( onp.node.uuid  in ( select onpp.node.uuid from Document dd left join NodeProperty onpp on onpp.node.uuid = dd.file.uuid where ( onpp.name = 'okp:Accounting.date' and onpp.value like concat('%',:documentDate,'%'))) or :documentDate is null )" +
            " and (exists (select dr.id from ConflictReason dr where dr.reason like concat('%',:reason,'%') and dr.id in ( select cr.id from DocumentConflict ddc1 inner join ddc1.conflictReasons cr where ddc1.document.id = d.id ) ) or :reason is null )")
    Page<Document> advanceSearchAdminAndDoa(
            @Param("branchIds") List<Long> branchIds,
            @Param("maintenanceCode") String maintenanceCode,
            @Param("states") List<DocumentStateEnum> states,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("registerFromDate") Date registerFromDate,
            @Param("registerToDate") Date registerToDate,
            @Param("filename") String filename,
            @Param("documentNumber") String documentNumber,
            @Param("documentDate") String documentDate,
            @Param("reason") String reason,
            @Param("rowNumber") String rowNumber,
            @Param("type") DocumentSetTypeEnum type,
            @Param("customerNumber") String customerNumber,
            @Param("fileNumber") String fileNumber,
            @Param("fileStatusId") Long fileStatusId,
            @Param("fileTypeId") Long fileTypeId,
            Pageable pageable);


    @Modifying
    @Transactional
    @Query(value = "insert into OKM_NODE_ROLE_PERMISSION (NRP_NODE,NRP_PERMISSION,NRP_ROLE) values (:uuid,:permission,:role)", nativeQuery = true)
    void setRolePermission(@Param("uuid") String uuid, @Param("permission") Long permission, @Param("role") String role);

    @Query(value = "select new com.sima.dms.domain.dto.response.DocumentOcrProcessDto(documentSet.branch.branchCode,documentSet.fromDate,documentSet.toDate) from Document where file.uuid =:uuid")
    DocumentOcrProcessDto getOcrDocuments(@Param("uuid") String uuid);

    @Query(value = "select file.uuid from Document where biStatus in(:biStatusEnums) and Date(registerDate) =:registerDate  ")
    List<String> findUnsuccessfulّBIFiles(@Param("biStatusEnums") List<BIStatusEnum> biStatusEnums, Date registerDate);

    @Query(value = " select  min(d.maintenanceCode) from Document  d where d.documentSet.id = :documentSetId ")
    String getMaintenanceCode(@Param("documentSetId") Long documentSetId);

    @Transactional
    @Modifying
    @Query(value = "update Document d set d.file.uuid = :newUuid , d.name = :fileName where d.file.uuid = :oldUuid")
    void updateUuidAndFileName(String oldUuid, String newUuid, String fileName);

    @Query(value = "select d.name from Document d where d.file.uuid = :Uuid")
    String getFileNameByUuid(@Param("Uuid") String Uuid);
}


