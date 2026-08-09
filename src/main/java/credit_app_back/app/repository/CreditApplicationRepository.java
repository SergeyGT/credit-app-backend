package credit_app_back.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import credit_app_back.app.entity.CreditApplication;
import credit_app_back.app.entity.CreditApplicationStatus;

@Repository
public interface CreditApplicationRepository extends JpaRepository<CreditApplication, Long> {
    Optional<CreditApplication> findById(Long id);
    List<CreditApplication> findAll();
    List<CreditApplication> findByStatus(CreditApplicationStatus status);
    
    default List<CreditApplication> findApproved() {
        return findByStatus(CreditApplicationStatus.APPROVED);
    }
    List<CreditApplication> findByClientIdAndStatus(Long clientId, CreditApplicationStatus status);
}
