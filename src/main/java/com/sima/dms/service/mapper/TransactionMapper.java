package com.sima.dms.service.mapper;


import com.sima.dms.domain.dto.TransactionDto;
import com.sima.dms.domain.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper extends EntityMapper<TransactionDto, Transaction>{

    TransactionDto toDto(Transaction transaction);

    Transaction toEntity(TransactionDto transactionDto);

    default Transaction fromId(Long id) {
        if (id == null) {
            return null;
        }
        Transaction transaction = new Transaction();
        transaction.setId(id);
        return transaction;

    }
}
