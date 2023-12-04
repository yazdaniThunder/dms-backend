package com.sima.dms.repository;

import com.sima.dms.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    Role findByInitials(String initials);

    Optional<List<Role>> findAllByIdIn(List<Long> roleIds);

    Optional<List<Role>> findByNameIn(List<String> names);

    Boolean existsByName(String name);

    Boolean existsByInitials(String initials);

}
