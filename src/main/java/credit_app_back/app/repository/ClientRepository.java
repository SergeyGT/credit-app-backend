package credit_app_back.app.repository;

import credit_app_back.app.entity.Client;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class ClientRepository extends CRUDRepository<Client, Long> {

    public ClientRepository(SessionFactory sessionFactory) {
        super(Client.class, sessionFactory);
    }

    @Transactional
    public Optional<Client> findByPassport(String passport) {
        return findAllBy((builder, root) ->
                new Predicate[]{builder.equal(root.get("passport"), passport)}
        ).stream().findFirst();
    }

    @Transactional
    public Optional<Client> findByPhone(String phone) {
        String normalizedPhone = normalizePhone(phone);

        return findAllBy((builder, root) -> new Predicate[]{
                builder.or(
                        builder.equal(root.get("phone"), normalizedPhone),
                        builder.equal(root.get("phone"), "+" + normalizedPhone)
                )
        }).stream().findFirst();
    }

    private String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.startsWith("+") ? phone.substring(1) : phone;
    }

    @Transactional
    public Optional<Client> findById(Long id) {
        return super.findById(id);
    }

    @Transactional
    public Page<Client> findClientsByFilters(
            String firstNameLike,
            String lastNameLike,
            String middleNameLike,
            String passport,
            String phone,
            Pageable pageable
    ) {
        return findAllBy(pageable, (builder, root) -> {
            Predicate[] predicates = new Predicate[5];
            int index = 0;

            if (firstNameLike != null) {
                predicates[index++] = builder.like(
                        builder.lower(root.get("firstName")),
                        "%" + firstNameLike.toLowerCase() + "%"
                );
            }
            if (lastNameLike != null) {
                predicates[index++] = builder.like(
                        builder.lower(root.get("lastName")),
                        "%" + lastNameLike.toLowerCase() + "%"
                );
            }
            if (middleNameLike != null) {
                predicates[index++] = builder.like(
                        builder.lower(root.get("middleName")),
                        "%" + middleNameLike.toLowerCase() + "%"
                );
            }
            if (passport != null) {
                predicates[index++] = builder.equal(root.get("passport"), passport);
            }
            if (phone != null) {
                predicates[index++] = builder.or(
                        builder.equal(root.get("phone"), phone),
                        builder.equal(root.get("phone"), "+" + phone)
                );
            }

            // Убираем null из массива
            Predicate[] finalPredicates = new Predicate[index];
            System.arraycopy(predicates, 0, finalPredicates, 0, index);
            return finalPredicates;
        });
    }
}