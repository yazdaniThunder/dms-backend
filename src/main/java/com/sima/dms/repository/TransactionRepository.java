package com.sima.dms.repository;

import com.sima.dms.domain.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction,Long> {

    Page<Transaction>findAll(Pageable pageable);

    Transaction findByTransactionCode(String transactCode);

    List<Transaction>findAll();

    List<Transaction>findByDocumentClass(String documentClass);



}
