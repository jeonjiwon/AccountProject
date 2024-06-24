package com.example.account.dto;

import com.example.account.type.TranactionType;
import com.example.account.type.TransactionResultType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryTransactionResponse {
        private String accountNumber;
        private TranactionType tranactionType;
        private TransactionResultType transactionResult;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactionAt;


        public static QueryTransactionResponse from (TransactionDto transactionDto) {
            return QueryTransactionResponse.builder()
                    .accountNumber(transactionDto.getAccountNumber())
                    .tranactionType(transactionDto.getTranactionType())
                    .transactionResult(transactionDto.getTransactionResultType())
                    .transactionId(transactionDto.getTransactionId())
                    .amount(transactionDto.getAmount())
                    .transactionAt(transactionDto.getTransactedAt())
                    .build();
        }
}
