package credit_app_back.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import credit_app_back.app.entity.CreditAgreeStatus;
import credit_app_back.app.entity.CreditAgreement;

@Repository
public interface CreditAgreementRepository extends JpaRepository<CreditAgreement, Long> {
    Optional<CreditAgreement> findById(Long id);
    List<CreditAgreement> findAll();
    List<CreditAgreement> findBySignStatus(CreditAgreeStatus signStatus);
        
    // Или если хотите искать по статусу SIGNED
    default List<CreditAgreement> findSigned() {
        return findBySignStatus(CreditAgreeStatus.SIGNED);
    }
}
