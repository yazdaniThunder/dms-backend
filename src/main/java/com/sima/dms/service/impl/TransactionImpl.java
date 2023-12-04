package com.sima.dms.service.impl;

import com.sima.dms.domain.dto.TransactionDto;
import com.sima.dms.domain.entity.Transaction;
import com.sima.dms.repository.TransactionRepository;
import com.sima.dms.service.TransactionService;
import com.sima.dms.service.mapper.TransactionMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
//@Transactional
@AllArgsConstructor
public class TransactionImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final Logger log= LoggerFactory.getLogger(TransactionImpl.class);

    @Override
    public Page<TransactionDto> paging(Pageable pageable) {
        log.debug("Request to get all transactions");
        return transactionRepository.findAll(pageable)
                .map(transactionMapper::toDto);
    }



    @Override
    public Transaction getByTransactCode(String trxCode) {
        log.debug("Request to get transactions by transaction code");
     return transactionRepository.findByTransactionCode(trxCode);

    }

    @Override
    public List<TransactionDto> getAll() {
        log.debug("Request to get all transactions ");
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDto> getByDocumentClass(String documentClass) {
        log.debug("Request to get  transactions by document class ");
        return transactionRepository.findByDocumentClass(documentClass).stream()
                .map(transactionMapper::toDto).collect(Collectors.toList());
    }


}
