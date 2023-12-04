package com.sima.dms.controller;

import com.sima.dms.domain.dto.TransactionDto;
import com.sima.dms.domain.entity.Transaction;
import com.sima.dms.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "Transaction")
@RequestMapping("/dms/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final Logger log = LoggerFactory.getLogger(TransactionController.class);

    @CrossOrigin
    @GetMapping
    @Operation(summary = "Get all transactions")
    public ResponseEntity<Page<TransactionDto>> getAll(Pageable pageable) {
        log.debug("REST request to find all transactions : ");
        Page<TransactionDto> transactions = transactionService.paging(pageable);
        return ResponseEntity.ok().body(transactions);
    }

    @CrossOrigin
    @GetMapping("/{code}")
    @Operation(summary = "Get transaction by code")
    public ResponseEntity<Transaction> getByTransactionCode(@PathVariable String code) {
        log.debug("REST request to find transaction by code : ");
        Transaction transaction = transactionService.getByTransactCode(code);
        return ResponseEntity.ok().body(transaction);
    }

    @CrossOrigin
    @GetMapping("/getAllTransactions")
    @Operation(summary = "Get all Transactions")
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        log.debug("REST request to get transaction list : ");
        return ResponseEntity.ok().body(transactionService.getAll());
    }

    @CrossOrigin
    @GetMapping("/documentClass")
    @Operation(summary = "Get all transactions by document class")
    public ResponseEntity<List<TransactionDto>> getAllByDocumentClass(String documentClass) {
        log.debug("REST request to get transaction by document class : ");
        return ResponseEntity.ok().body(transactionService.getByDocumentClass(documentClass));
    }
}