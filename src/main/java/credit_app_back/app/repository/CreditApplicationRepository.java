package credit_app_back.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<CreditApplication> findByStatus(CreditApplicationStatus status, Pageable pageable);

    @Query("SELECT a FROM CreditApplication a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:clientId IS NULL OR a.client.id = :clientId)")
    Page<CreditApplication> findApplicationsByFilters(
            @Param("status") CreditApplicationStatus status,
            @Param("clientId") Long clientId,
            Pageable pageable
    );
}
