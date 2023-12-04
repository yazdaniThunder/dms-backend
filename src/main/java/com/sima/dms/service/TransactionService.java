package com.sima.dms.service;

import com.sima.dms.domain.dto.TransactionDto;
import com.sima.dms.domain.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {
    Page<TransactionDto> paging(Pageable  pageable);
    Transaction getByTransactCode(String trxCode);
    List<TransactionDto> getAll();
    List<TransactionDto> getByDocumentClass(String documentClass);
}
