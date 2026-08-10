package credit_app_back.app.dto;

import credit_app_back.app.entity.FamilyStatus;
import credit_app_back.app.entity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDto {

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[\\p{L}\\s'-]+$", message = "Invalid first name format")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[\\p{L}\\s'-]+$", message = "Invalid last name format")
    private String lastName;

    @Size(max = 64)
    @Pattern(regexp = "^[\\p{L}\\s'-]+$", message = "Invalid middle name format")
    private String middleName;

    @NotBlank(message = "Passport is required")
    @Size(min = 10, max = 10)
    @Pattern(regexp = "^\\d{10}$", message = "Invalid passport format")
    private String passport;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Family status is required")
    private FamilyStatus familyStatus;

    @NotBlank(message = "Residence address is required")
    @Size(max = 255)
    private String residenceAddress;

    @NotBlank(message = "Registration address is required")
    @Size(max = 255)
    private String registrationAddress;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?\\d{10,12}$", message = "Invalid phone format")
    private String phone;

    private LocalDate employmentStartDate;
    private LocalDate employmentEndDate;

    @Size(max = 255)
    private String employmentPosition;

    @Size(max = 255)
    private String organizationName;

    @NotBlank(message = "Loan purpose is required")
    @Size(max = 255)
    private String loanPurpose;
}