package com.sima.dms.repository;

import com.sima.dms.domain.dto.response.AssignedBranchDto;
import com.sima.dms.domain.dto.response.BranchListDto;
import com.sima.dms.domain.dto.response.ProfileSearchResponseDto;
import com.sima.dms.domain.entity.Branch;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.User;
import com.sima.dms.domain.enums.BranchTypeEnum;
import com.sima.dms.domain.enums.RoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    List<Profile> findAllByRoleInAndActiveIsTrue(List<RoleEnum> roles);

    @Query("select p.user from Profile p where p.role =:role and p.active = true ")
    List<User> getUsersByRole(RoleEnum role);

    @Query("select p.user from Profile p where p.role in (:roles) and p.active = true ")
    List<User> getUsersByRoles(List<RoleEnum> roles);

    Profile findByUser_PersonelUserNameIgnoreCaseAndActiveIsTrue(String personelUserName);

    Profile findByUser_PersonelUserNameAndActiveIsTrue(String personelUserName);

    boolean existsByUser_PersonelUserNameIgnoreCaseAndActiveIsTrue(String personelUserName);

    List<Profile> findAllByUser_PersonelUserNameIn(List<String> personelUserName);

    Profile findByUserIdAndActiveIsTrue(Long userId);

//    @Query("select p.assignedBranches from Profile p where p.id =:profileId ")
//    List<Branch> getAssignedBranches(@Param("profileId") Long profileId);

    @Query(value = "select distinct new com.sima.dms.domain.dto.response.AssignedBranchDto( b.id , b.branchName ,b.branchCode , b.type ) from Branch b join b.assignedProfiles p where p.id = :profileId ")
    List<AssignedBranchDto> getAssignedBranches(@Param("profileId") Long profileId);

    @Query("select id from Profile where user.personelUserName=:personelUserName and active = true ")
    Long getId(@Param("personelUserName") String personelUserName);

    @Query("select role from Profile where id=:profileId and active = true")
    RoleEnum getRole(@Param("profileId") Long profileId);

    @Query("select branch.id from Profile where id =:profileId and active = true")
    Long getBranchId(@Param("profileId") Long profileId);

    @Query("select branch.branchCode from Profile where user.personelUserName=:personelUserName and active = true")
    Optional<String> getBranchCode(@Param("personelUserName") String personelUserName);

    @Query("select p.branch from Profile p  where p.user.personelUserName=:personelUserName and p.active = true")
    Optional<Branch> getBranch(@Param("personelUserName") String personelUserName);

    @Query("select p.user.fullName from Profile p where p.id = :profileId ")
    String getFullName(@Param("profileId") Long profileId);

    @Query(value = "select new com.sima.dms.domain.dto.response.ProfileSearchResponseDto (p.id, u.id,u.personCode,u.nationalKey , u.firstName,u.lastName ," +
            " u.fullName ,u.personelUserName , p.branch.branchCode,p.branch.branchName , p.branch.type , p.role,u.active ) from User u " +
            "join Profile p on u.id =p.user.id" +
            " where ( p.active = true ) and ( p.user.fullName like concat('%',:fullName,'%') or :fullName is null) " +
            " and ( p.user.nationalKey like concat('%',:nationalKey,'%') or :nationalKey is null )" +
            " and ( p.user.personCode = :personCode or :personCode is null ) " +
            " and ( lower(p.user.personelUserName) like lower(concat('%',:personelUserName,'%')) or :personelUserName is null )" +
            " and ( p.job like concat('%',:job,'%') or :job is null ) " +
            " and ( p.branch.id = :branchId or :branchId is null )" +
            " and ( p.role = :role or :role is null )")
    Page<ProfileSearchResponseDto> advanceSearch(@Param("fullName") String fullName,
                                                 @Param("nationalKey") String nationalKey,
                                                 @Param("personCode") Long personCode,
                                                 @Param("personelUserName") String personelUserName,
                                                 @Param("job") String job,
                                                 @Param("branchId") Long branchId,
                                                 @Param("role") RoleEnum role,
                                                 Pageable pageable);

    @Query("select p.user from Profile p where p.active = true and p.branch.id =:branchId ")
    List<User> getByBranchId(@Param("branchId") Long branchId);

    @Query("select p.user from Profile p where p.active = true and p.role =:role and p.branch.id in (:branchIds)")
    List<User> getByBranchIdAndRole(@Param("role") RoleEnum role, @Param("branchIds") List<Long> branchIds);

    @Query("select distinct p.user from Profile p join Branch db on db.id = p.branch.id" +
            " left join Branch parent on parent.id = db.parent.id" +
            " where ( p.active = true and p.role in (:roles) ) and ( db.id =:branchId or parent.id=:branchId )")
    List<User> getByBranchIdAndRole(@Param("roles") List<RoleEnum> roles, @Param("branchId") Long branchId);

    @Query("select p.user from Profile p where p.role in (:roles) and p.active = true and (p.branch.id in (:branchId) ) ")
    List<User> getByBranchIdsAndRoles(@Param("roles") List<RoleEnum> roles, @Param("branchId") List<Long> branchId);


    @Query("select p.user from Profile p where p.role in (:roles) and p.active = true and (p.branch.id in (:branchId) or COALESCE(:branchId) is null ) ")
    List<User> getByBranchIdsAndRolesForAdmin(@Param("roles") List<RoleEnum> roles, @Param("branchId") List<Long> branchId);

    @Query(value = "select p.user.personelUserName from Profile p inner join p.assignedBranches ab where ab.branchCode=:branchCode and p.active = true ")
    List<String> getUserNameAssignedBranches(@Param("branchCode") Long branchCode);

    @Query("select branch.type from Profile where id =:profileId")
    BranchTypeEnum getBranchType(@Param("profileId") Long profileId);


    @Query("select p.user.personelUserName from Profile p where p.id = :profileId ")
    String getPersonelUserName(@Param("profileId") Long profileId);



}
