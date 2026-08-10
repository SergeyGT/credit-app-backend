package credit_app_back.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import credit_app_back.app.entity.CreditAgreeStatus;
import credit_app_back.app.entity.CreditAgreement;

@Repository
public interface CreditAgreementRepository extends JpaRepository<CreditAgreement, Long> {
    Optional<CreditAgreement> findById(Long id);
    List<CreditAgreement> findAll();
    List<CreditAgreement> findBySignStatus(CreditAgreeStatus signStatus);
        
    default List<CreditAgreement> findSigned() {
        return findBySignStatus(CreditAgreeStatus.SIGNED);
    }

    Optional<CreditAgreement> findByCreditApplicationId(Long applicationId);

    Page<CreditAgreement> findBySignStatus(CreditAgreeStatus status, Pageable pageable);

    @Query("SELECT a FROM CreditAgreement a WHERE " +
           "(:status IS NULL OR a.signStatus = :status) AND " +
           "(:clientId IS NULL OR a.creditApplication.client.id = :clientId)")
    Page<CreditAgreement> findAgreementsByFilters(
            @Param("status") CreditAgreeStatus status,
            @Param("clientId") Long clientId,
            Pageable pageable
    );
}
