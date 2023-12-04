package com.sima.dms.repository;

import com.sima.dms.domain.dto.response.BranchListDto;
import com.sima.dms.domain.entity.Branch;
import com.sima.dms.domain.enums.BranchTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    List<Branch> findAllByPathIsNotNull();

    @Query("select branchCode from Branch where id=:branchId")
    String getBranchCode(@Param("branchId") Long branchId);

    @Modifying
    @Transactional
    @Query(value = "delete from DMS_BRANCH_PROFILE where BRANCH_ID=:branchId and USER_ID in(:userIds)", nativeQuery = true)
    void deleteUsers(@Param("branchId") Long branchId, @Param("userIds") List<Long> userIds);

    @Query(value = "select id from Branch where branchCode =:branchCode")
    Long findByBranchCode(Long branchCode);

//    Branch findByBranchCode(Long branchCode);

    Page<Branch> findAllByActiveIsTrue(Pageable pageable);

    @Query(value = "select new com.sima.dms.domain.dto.response.BranchListDto (b.id ,b.branchName,b.branchCode,b.type,p.branchCode,p.branchName) from Branch b  left join Branch p on b.parent.id = p.id where b.active = true ")
    List<BranchListDto> findAllByActiveIsTrue();

    @Query(value = "select BRANCH_ID from DMS_BRANCH_PROFILE where PROFILE_ID=:profileId", nativeQuery = true)
    List<Long> getAssignBranches(@Param("profileId") Long profileId);

    @Query("select b.sequence from Branch b where  b.id =:branchId ")
    Long getSequence(Long branchId);

    @Query("select b.branchCode from Branch b where b.active = true")
    List<Long> getBranchCodes();

    @Query(value = "select new com.sima.dms.domain.dto.response.BranchListDto (b.id ,b.branchName,b.branchCode,b.type,p.branchCode,p.branchName) from Branch b  left join Branch p on b.parent.id = p.id where b.active = true and b.type =:type ")
    List<BranchListDto> getAllByType (@Param("type")BranchTypeEnum type);

    @Modifying
    @Transactional
    @Query(value = "update Branch b set b.path = :path where b.branchCode = :code ")
    void setPath(String path, Long code);

    @Query("select id from Branch where parent.id =:branchId")
    Long getParentId(@Param("branchId")Long branchId);

    @Query("select id from Branch where parent.id =:branchId or id =:branchId")
    List<Long> getAllByParentId(@Param("branchId")Long branchId);

    @Query("select branchCode from Branch where parent.id =:branchId or id =:branchId")
    List<Long> getAllBranchCodeByParentId(@Param("branchId")Long branchId);

    @Query("select type from Branch where id =:branchId")
    BranchTypeEnum getBranchType(@Param("branchId")Long branchId);

    @Query(value = "select distinct b from Branch b left join b.assignedProfiles p where b.active= true and (p.id = :profileId or :profileId is null) and ( b.id = :branchId or :branchId is null )")
    Page<Branch> getBranchWithProfile(@Param("branchId") Long branchId,
                                      @Param("profileId") Long profileId,
                                      Pageable pageable);
}