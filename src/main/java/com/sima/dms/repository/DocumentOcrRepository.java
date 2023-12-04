package com.sima.dms.repository;


import com.sima.dms.domain.entity.DocumentOcr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface DocumentOcrRepository extends JpaRepository<DocumentOcr, Long> {

    @Query(value = "select similar_title from DMS_DOCUMENT_TYPE where score > 0 order by score desc ", nativeQuery = true)
    List<String> getDocumentSimilarTitles();

    @Query(value = "select title from DMS_DOCUMENT_TYPE where similar_title = :similarTitle ", nativeQuery = true)
    String getDocumentTitle(String similarTitle);

    @Modifying
    @Transactional
    @Query(value = "update DMS_DOCUMENT_TYPE set score = score + 1 where title=:title", nativeQuery = true)
    void increaseScore(@Param("title") String title);

//    @Query(value = " select similar_title from DMS_DOCUMENT_TYPE where  :line like concat('%' , similar_title ,'%')  order by score desc ", nativeQuery = true)
//    List<String> getDocumentSimilarTitles(@Param("line") String line);
}
