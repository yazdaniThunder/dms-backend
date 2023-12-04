package com.sima.dms.repository;

import com.sima.dms.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@SuppressWarnings("unused")
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select id from User where personelUserName=:personelUserName")
    Long getUserId(@Param("personelUserName") String personelUserName);

    boolean existsByPersonelUserName(String personelUserName);

    Optional<User> findOneByPersonelUserName(String personelUserName);

    @Modifying
    @Transactional
    @Query(value = "update User u set u.active=:active where u.id=:userId ")
    void activeAndDeActiveProfile(@Param("userId") Long userId,@Param("active") Boolean active);

    Page<User> findAll(Pageable pageable);

//    List<User> findAllByBranch_Id(Long branchId);
//
//    @Query("select u.branch.id from User u where u.id=:userId")
//    Optional<Long> getBranchId(@Param("userId") Long userId);
//
//    @Query("select u.branch.branchCode from User u where u.id=:userId")
//    Optional<String> getBranchCode(@Param("userId") Long userId);
//
//    @Query("select u.branch from User u where u.id=:userId")
//    Optional<Branch> getBranch(@Param("userId") Long userId);


//    Optional<User> findOneByEmail(String email);
//
//    Optional<User> findOneByEmailIgnoreCase(String email);
//
//    Boolean existsByEmail(String email);
//
//    Optional<User> findOneWithRoleByEmail(String email);
}
