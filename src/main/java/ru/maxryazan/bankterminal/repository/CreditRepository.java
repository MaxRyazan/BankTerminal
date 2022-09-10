package ru.maxryazan.bankterminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maxryazan.bankterminal.model.Credit;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {
    boolean existsByNumberOfCreditContract(String numberOfCreditContract);

    Credit findByNumberOfCreditContract(String numberOfCreditContract);
}
