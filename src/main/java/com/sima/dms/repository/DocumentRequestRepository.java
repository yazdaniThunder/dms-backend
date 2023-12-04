package com.sima.dms.repository;


import com.sima.dms.domain.dto.response.DocumentReceiveResponseDto;
import com.sima.dms.domain.entity.document.DocumentRequest;
import com.sima.dms.domain.enums.DocumentRequestStateEnum;
import com.sima.dms.domain.enums.DocumentRequestTypeEnum;
import com.sima.dms.domain.enums.DocumentSetStateEnum;
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
public interface DocumentRequestRepository extends PagingAndSortingRepository<DocumentRequest, Long> {

    Long countByLastState_StateInAndBranch_idIn(List<DocumentRequestStateEnum> states, List<Long> branchIds);

    List<DocumentRequest> findAllByIdIn(List<Long> ids);

    @Query("select distinct d.requestBranch.id from DocumentRequest d where d.id in (:documentRequestIds)")
    List<Long> getCreatorBranchIds(@Param("documentRequestIds")List<Long> documentRequestIds);

    @Query(value = "select dr from DocumentRequest dr  where dr.lastState.state in (:states) ")
    Page<DocumentRequest> findAllByStateIn(@Param("states") List<DocumentRequestStateEnum> states, Pageable pageable);

    @Query(value = "select dr from DocumentRequest dr  where dr.lastState.state not in (:states) ")
    Page<DocumentRequest> findAllByStateNotIn(@Param("states") List<DocumentRequestStateEnum> states, Pageable pageable);

    @Query("select d from DocumentRequest d where d.requestBranch.id in (:branchIds) and d.lastState.state <> com.sima.dms.domain.enums.DocumentRequestStateEnum.BRANCH_REJECTED ")
    Page<DocumentRequest> getBranchRequests(@Param("branchIds") List<Long> branchIds, Pageable pageable);

    @Query("select d from DocumentRequest d where d.lastState.state <> com.sima.dms.domain.enums.DocumentRequestStateEnum.BRANCH_REJECTED ")
    Page<DocumentRequest> getAllBranchRequests(Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "update DocumentRequest set branchFile.uuid=:uuid where id=:id")
    void setBranchFile(Long id, String uuid);

    @Modifying
    @Transactional
    @Query(value = "insert into DMS_DOCUMENT_REQUEST_OFFICE_FILE (DOCUMENT_REQUEST_ID,UUID) values (:documentRequestId , :uuid)" , nativeQuery = true)
    void setDocumentOfficeFile(@Param("documentRequestId") Long documentRequestId,@Param("uuid") String uuid);

//    @Query("select new com.sima.dms.domain.dto.response.DocumentReceiveResponseDto ( officeFile.uuid , expiryDate  , receiveDescription ,officeFile.name ) from DocumentRequest " +
//            " where id = :id")
//    DocumentReceiveResponseDto receiveDocument(@Param("id") Long id);

    @Query(" select dr from DocumentRequest dr " +
            " join dr.createdBy creator " +
            " where ( :registerDateFrom <= DATE(dr.registerDate) or :registerDateFrom is null ) and (:registerDateTo >= DATE(dr.registerDate) or :registerDateTo is null ) and (:documentNumber = dr.documentNumber or :documentNumber is null) " +
            " and (:documentDateFrom <= DATE(dr.documentDateFrom) or :documentDateFrom is null )  and (:documentDateTo >= DATE(dr.documentDateFrom) or :documentDateTo is null) and (:customerNumber = dr.customerNumber or :customerNumber is null) " +
            " and (:documentType = dr.documentType or :documentType is null ) and (:creatorId = creator.user.id or :creatorId is null) " +
            " and exists ( select 1 from DocumentRequestState state where state.documentRequest.id = dr.id and state.createdBy.user.id = :confirmerId or :confirmerId is null ) and ( dr.branch.id in (:documentBranchIds) or COALESCE(:documentBranchIds) is null) and (dr.requestBranch.id in (:requestBranchIds)) " +
            " and ( :sentDateFrom <= DATE(dr.sentDate) or :sentDateFrom is null ) and (:sentDateTo >= DATE(dr.sentDate) or :sentDateTo is null )  " +
            " and ( dr.lastState.state in (:states) or COALESCE(:states) is null ) ")
    Page<DocumentRequest> requestSearchByDou(@Param("registerDateFrom") Date registerDateFrom,
                                        @Param("registerDateTo") Date registerDateTo,
                                        @Param("documentNumber") String documentNumber,
                                        @Param("documentDateFrom") Date documentDateFrom,
                                        @Param("documentDateTo") Date documentDateTo,
                                        @Param("customerNumber") String customerNumber,
                                        @Param("documentType") DocumentRequestTypeEnum documentType,
                                        @Param("states") List<DocumentRequestStateEnum> states,
                                        @Param("creatorId") Long creatorId,
                                        @Param("confirmerId") Long confirmerId,
                                        @Param("documentBranchIds") List<Long> documentBranchIds,
                                        @Param("requestBranchIds") List<Long> requestBranchIds,
                                        @Param("sentDateFrom") Date sentDateFrom,
                                        @Param("sentDateTo") Date sentDateTo,
                                        Pageable pageable);

    @Query(" select dr from DocumentRequest dr " +
            " join dr.createdBy creator " +
            " where ( :registerDateFrom <= DATE(dr.registerDate) or :registerDateFrom is null ) and (:registerDateTo >= DATE(dr.registerDate) or :registerDateTo is null ) and (:documentNumber = dr.documentNumber or :documentNumber is null) " +
            " and (:documentDateFrom <= DATE(dr.documentDateFrom) or :documentDateFrom is null )  and (:documentDateTo >= DATE(dr.documentDateFrom) or :documentDateTo is null) and (:customerNumber = dr.customerNumber or :customerNumber is null) " +
            " and (:documentType = dr.documentType or :documentType is null ) and (:creatorId = creator.user.id or :creatorId is null) " +
            " and exists ( select 1 from DocumentRequestState state where state.documentRequest.id = dr.id and state.createdBy.user.id = :confirmerId or :confirmerId is null ) and ( dr.branch.id in (:documentBranchIds) or COALESCE(:documentBranchIds) is null) and (dr.requestBranch.id in (:requestBranchIds)  or COALESCE(:requestBranchIds) is null) " +
            " and ( :sentDateFrom <= DATE(dr.sentDate) or :sentDateFrom is null ) and (:sentDateTo >= DATE(dr.sentDate) or :sentDateTo is null )  " +
            " and ( dr.lastState.state in (:states) or COALESCE(:states) is null ) ")
    Page<DocumentRequest> requestSearch(@Param("registerDateFrom") Date registerDateFrom,
                                        @Param("registerDateTo") Date registerDateTo,
                                        @Param("documentNumber") String documentNumber,
                                        @Param("documentDateFrom") Date documentDateFrom,
                                        @Param("documentDateTo") Date documentDateTo,
                                        @Param("customerNumber") String customerNumber,
                                        @Param("documentType") DocumentRequestTypeEnum documentType,
                                        @Param("states") List<DocumentRequestStateEnum> states,
                                        @Param("creatorId") Long creatorId,
                                        @Param("confirmerId") Long confirmerId,
                                        @Param("documentBranchIds") List<Long> documentBranchIds,
                                        @Param("requestBranchIds") List<Long> requestBranchIds,
                                        @Param("sentDateFrom") Date sentDateFrom,
                                        @Param("sentDateTo") Date sentDateTo,
                                        Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = " update DMS_DOCUMENT_REQUEST_OFFICE_FILE d set d.UUID = :newUuid where d.UUID = :oldUuid" , nativeQuery = true)
    void updateUuid(String oldUuid, String newUuid);
}
