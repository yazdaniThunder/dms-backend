package com.sima.dms.repository;

import com.sima.dms.domain.entity.baseinformation.FileType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.FetchType;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface FileTypeRepository extends PagingAndSortingRepository<FileType,Long> {

    @Modifying
    @Transactional
    @Query("update FileType set active=:active where id in :ids")
    void updateActive(@Param("ids") List<Long> ids, @Param("active") boolean active);

    List<FileType> getAllByActiveIsTrue();

    @Query(value = "select fs.fileType from FileStatus fs where fs.id = :fileStatusId ")
    FileType getFileTypeByFileStatusId(Long fileStatusId);

    @Query(value = " select ft from FileType ft where ft.title like concat('%',:title,'%') and ft.active=true ")
    FileType getByTitle(@Param("title") String title);
}
