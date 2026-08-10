package credit_app_back.app.repository;

import credit_app_back.app.entity.CreditAgreeStatus;
import credit_app_back.app.entity.CreditAgreement;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class CreditAgreementRepository extends CRUDRepository<CreditAgreement, Long> {

    public CreditAgreementRepository(SessionFactory sessionFactory) {
        super(CreditAgreement.class, sessionFactory);
    }

    @Transactional
    public Optional<CreditAgreement> findById(Long id) {
        return super.findById(id);
    }

    @Transactional
    public Optional<CreditAgreement> findByCreditApplicationId(Long applicationId) {
        return findAllBy((builder, root) ->
                new Predicate[]{builder.equal(root.get("creditApplication").get("id"), applicationId)}
        ).stream().findFirst();
    }

    @Transactional
    public Page<CreditAgreement> findBySignStatus(CreditAgreeStatus status, Pageable pageable) {
        return findAllBy(pageable, (builder, root) ->
                new Predicate[]{builder.equal(root.get("signStatus"), status)}
        );
    }

    @Transactional
    public Page<CreditAgreement> findAgreementsByFilters(
            CreditAgreeStatus status,
            Long clientId,
            Pageable pageable
    ) {
        return findAllBy(pageable, (builder, root) -> {
            Predicate[] predicates = new Predicate[2];
            int index = 0;

            if (status != null) {
                predicates[index++] = builder.equal(root.get("signStatus"), status);
            }
            if (clientId != null) {
                predicates[index++] = builder.equal(root.get("creditApplication").get("client").get("id"), clientId);
            }

            Predicate[] finalPredicates = new Predicate[index];
            System.arraycopy(predicates, 0, finalPredicates, 0, index);
            return finalPredicates;
        });
    }
}