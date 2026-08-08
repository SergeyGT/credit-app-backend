package credit_app_back.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import credit_app_back.app.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByPassport(String passport);
    Optional<Client> findById(Long id);
    Optional<Client> findByPhone(String phone);
    Optional<Client> findByFirstNameAndLastNameAndMiddleName(String firstName, String lastName, String middleName);
    List<Client> findAll();
}
