package com.mtb.app.repository;

import com.mtb.app.entity.BankCdaEntity;
import com.mtb.app.entity.BankCdaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankCdaRepository extends JpaRepository<BankCdaEntity, BankCdaId> {

    Optional<BankCdaEntity> findByIdBankCdaIdAndIdCariCdaId(String bankCdaId, String cariCdaId);
}
