package ru.maxryazan.bankterminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maxryazan.bankterminal.model.Pay;

@Repository
public interface PayRepository extends JpaRepository<Pay, Long> {
}
