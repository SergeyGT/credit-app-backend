package credit_app_back.app.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private CreditApplicationStatus status;

    @Column(name = "requested_money", nullable = false, precision = 12, scale = 2)
    private BigDecimal requestedMoney;

    @Column(name = "approved_term")
    private Integer approvedTerm;

    @Column(name = "approved_money", precision = 12, scale = 2)
    private BigDecimal approvedMoney;

    @Column(name = "loan_purpose", length = 255)
    private String loanPurpose; 

    @Column(name = "decision_date")
    private LocalDateTime decisionDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "creditApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CreditAgreement creditAgreement;
}
