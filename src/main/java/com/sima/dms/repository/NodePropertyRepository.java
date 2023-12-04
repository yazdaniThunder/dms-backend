package com.sima.dms.repository;

import com.sima.dms.domain.entity.NodeProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodePropertyRepository extends JpaRepository<NodeProperty, String> {

    NodeProperty findByNode_UuidAndName(String uuid,String name);

    List<NodeProperty> findAllByNode_UuidAndValueIsNot(String uuid,String value);
}
