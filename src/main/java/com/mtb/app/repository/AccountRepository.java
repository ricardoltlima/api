package com.mtb.app.repository;

import com.mtb.app.entity.AccountEntity;
import com.mtb.app.entity.BankCariId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, BankCariId> {

    Optional<AccountEntity> findByIdBankCdaIdAndIdBankCustomerId(String bankCdaId, String bankCustomerId);
}
