package com.sima.dms.repository;

import com.sima.dms.domain.entity.documentSet.DocumentSetConflict;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentSetConflictRepository extends PagingAndSortingRepository<DocumentSetConflict,Long> {

//    List<DocumentSetConflict>findAllByDocumentSet(DocumentSet documentSet);
//    List<DocumentSetConflict>findAllByUserResolverConflictId(User user);
//    List<DocumentSetConflict>findAllByUserSenderConflictId(User user);
//    List<DocumentSetConflict>findAllByUserSubmittedConflictId(User user);
//    List<DocumentSetConflict>findAllByReason(DocumentSetConflictReason reason);

}
