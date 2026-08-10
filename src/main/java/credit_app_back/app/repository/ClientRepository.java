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
       "(:firstName IS NULL OR c.firstName LIKE CONCAT('%', :firstName, '%')) AND " +
       "(:lastName IS NULL OR c.lastName LIKE CONCAT('%', :lastName, '%')) AND " +
       "(:middleName IS NULL OR c.middleName LIKE CONCAT('%', :middleName, '%')) AND " +
       "(:passport IS NULL OR c.passport = :passport) AND " +
       "(:phone IS NULL OR c.phone = :phone)")
        Page<Client> findClientsByFilters(
                @Param("firstName") String firstName,
                @Param("lastName") String lastName,
                @Param("middleName") String middleName,
                @Param("passport") String passport,
                @Param("phone") String phone,
                Pageable pageable
        );
}
