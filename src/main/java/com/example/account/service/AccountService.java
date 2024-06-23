package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import com.example.account.dto.AccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;

    /**
     * 사용자가 있는지 조회
       계좌번호를 생성하고
       계좌를 저장하고, 그 정보를 넘긴다
     * @param userId
     * @param InitialBalance
     */
    @Transactional
    public AccountDto createAccount(Long userId, Long InitialBalance){
       AccountUser accountUser = accountUserRepository.findById(userId)
               .orElseThrow(()-> new AccountException(ErrorCode.USER_NOT_FOUND));

       validateCreateAccount(accountUser);

       String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
               .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
               .orElse("1000000000");

       return AccountDto.fromEntity(
               accountRepository.save(Account.builder()
                       .accountUser(accountUser)
                       .accountStatus(AccountStatus.IN_USE)
                       .accountNumber(newAccountNumber)
                       .balance(InitialBalance)
                       .registeredAt(LocalDateTime.now())
                       .build())
               );
    }

    private void validateCreateAccount(AccountUser accountUser) {
       if(accountRepository.countByAccountUser(accountUser) == 10) {
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);
       }
    }

    @Transactional
    public Account getAccount(Long id) {
        if(id < 0 ){
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();
    }


    @Transactional
    public AccountDto DeleteAccount(Long userId, String accountNumber) {

       AccountUser accountUser = accountUserRepository.findById(userId)
               .orElseThrow(()-> new AccountException(ErrorCode.USER_NOT_FOUND));
       Account account = accountRepository.findByAccountNumber(accountNumber)
               .orElseThrow(()-> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

       validateDeleteAccount(accountUser, account);

       account.setAccountStatus(AccountStatus.UNREGISTERED);
       account.setUnregisteredAt(LocalDateTime.now());

       // 없어도 동작하긴 함
        accountRepository.save(account);


       return AccountDto.fromEntity(account);
    }

    private void validateDeleteAccount(AccountUser accountUser, Account account) {
        if(!Objects.equals(accountUser.getId(),account.getAccountUser().getId())) {
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);
        }

        if(account.getAccountStatus() == AccountStatus.UNREGISTERED) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTED);
        }

        if(account.getBalance() > 0) {
            throw new AccountException(ErrorCode.BALANCE_NOT_EMPTY);
        }

    }


    @Transactional
    public List<AccountDto> getAccountsByUserId(Long userId) {

        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
        List<Account> accounts = accountRepository.findByAccountUser(accountUser);

        return accounts.stream().map(AccountDto::fromEntity).collect(Collectors.toList());
    }
}
