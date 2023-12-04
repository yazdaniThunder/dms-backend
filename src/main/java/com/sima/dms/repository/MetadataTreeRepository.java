package com.sima.dms.repository;

import com.sima.dms.domain.entity.MetadataTree;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetadataTreeRepository extends JpaRepository<MetadataTree, Long> {

    List<MetadataTree> findAllByParentId(Long parentId);
}