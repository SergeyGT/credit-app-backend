package credit_app_back.app.repository;

import credit_app_back.app.entity.CreditApplication;
import credit_app_back.app.entity.CreditApplicationStatus;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class CreditApplicationRepository extends CRUDRepository<CreditApplication, Long> {

    public CreditApplicationRepository(SessionFactory sessionFactory) {
        super(CreditApplication.class, sessionFactory);
    }

    @Transactional
    public Optional<CreditApplication> findById(Long id) {
        return super.findById(id);
    }

    @Transactional
    public Page<CreditApplication> findByStatus(CreditApplicationStatus status, Pageable pageable) {
        return findAllBy(pageable, (builder, root) ->
                new Predicate[]{builder.equal(root.get("status"), status)}
        );
    }

    @Transactional
    public Page<CreditApplication> findApplicationsByFilters(
            CreditApplicationStatus status,
            Long clientId,
            Pageable pageable
    ) {
        return findAllBy(pageable, (builder, root) -> {
            Predicate[] predicates = new Predicate[2];
            int index = 0;

            if (status != null) {
                predicates[index++] = builder.equal(root.get("status"), status);
            }
            if (clientId != null) {
                predicates[index++] = builder.equal(root.get("client").get("id"), clientId);
            }

            Predicate[] finalPredicates = new Predicate[index];
            System.arraycopy(predicates, 0, finalPredicates, 0, index);
            return finalPredicates;
        });
    }
}