package credit_app_back.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import credit_app_back.app.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> { 
    Optional<Client> findByPassport(String passport);
    Optional<Client> findById(Long id);
    Optional<Client> findByPhone(String phone);
    Optional<Client> findByFirstNameAndLastNameAndMiddleName(
            String firstName, String lastName, String middleName
    );
    
    @Query("SELECT c FROM Client c WHERE " +
       "(:firstNameLike IS NULL OR LOWER(c.firstName) LIKE :firstNameLike) AND " +
       "(:lastNameLike IS NULL OR LOWER(c.lastName) LIKE :lastNameLike) AND " +
       "(:middleNameLike IS NULL OR LOWER(c.middleName) LIKE :middleNameLike) AND " +
       "(:passport IS NULL OR c.passport = :passport) AND " +
       "(:phone IS NULL OR c.phone = :phone OR c.phone = :phonePlus)")
    Page<Client> findClientsByFilters(
            @Param("firstNameLike") String firstNameLike,
            @Param("lastNameLike") String lastNameLike,
            @Param("middleNameLike") String middleNameLike,
            @Param("passport") String passport,
            @Param("phone") String phone,
            @Param("phonePlus") String phonePlus,
            Pageable pageable
    );
}
