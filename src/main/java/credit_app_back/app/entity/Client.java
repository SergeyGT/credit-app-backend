package credit_app_back.app.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clients")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name", nullable = false, length = 64)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 64)
    private String lastName;

    @Column(name = "middle_name", length = 64)
    private String middleName;

    @Column(name = "passport", nullable = false, unique = true, length = 10)
    private String passport;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 20)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "family_status", nullable = false, length = 20)
    private FamilyStatus familyStatus;

    @Column(name = "residence_address", nullable = false, length = 255)
    private String residenceAddress;

    @Column(name = "registration_address", nullable = false, length = 255)
    private String registrationAddress;

    @Column(name = "phone", nullable = false, length = 16)
    private String phone;

    @Column(name = "employment_start_date")
    private LocalDate employmentStartDate;

    @Column(name = "employment_end_date")
    private LocalDate employmentEndDate;

    @Column(name = "employment_position", length = 255)
    private String employmentPosition;

    @Column(name = "organization_name", length = 255)
    private String organizationName;

    @Column(name = "loan_purpose", nullable = false, length = 255)
    private String loanPurpose;

    @OneToMany(mappedBy = "client")
    private List<CreditApplication> creditApplications;
}
