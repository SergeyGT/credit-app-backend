package credit_app_back.app.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "credit_agreements")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreditAgreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "credit_application_id", nullable = false)
    private CreditApplication creditApplication;

    @Column(name = "sign_date")
    private LocalDate signDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "sign_status", nullable = false)
    private CreditAgreeStatus signStatus;
}
