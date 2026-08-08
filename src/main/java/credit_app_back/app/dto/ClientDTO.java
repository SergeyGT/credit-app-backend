
package credit_app_back.app.dto;

import java.time.LocalDate;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientDto {
    @NotBlank
    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[A-Za-z][a-z]*(?:[\\s'-][A-Za-z][a-z]*)*$", message = "Invalid first name format")
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[A-Za-z][a-z]*(?:[\\s'-][A-Za-z][a-z]*)*$", message = "Invalid last name format")
    private String lastName;

    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[A-Za-z][a-z]*(?:[\\s'-][A-Za-z][a-z]*)*$", message = "Invalid middle name format")
    private String middleName;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid passport format")
    @Size(min = 10, max = 10)
    public String passport;

    @NotNull
    private Gender gender;

    @NotNull
    public FamilyStatus familyStatus;

    @NotBlank
    @Size(max = 255)
    private String residenceAddress;

    @NotBlank
    @Size(max = 255)
    private String registrationAddress;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{10,12}$", message = "Invalid phone number format")
    @Size(min = 11, max = 12)
    public String phoneNumber;

    private LocalDate employmentStartDate;
    private LocalDate employmentEndDate;

    @Size(max = 255)
    private String employmentPosition;

    @Size(max = 255)
    private String organizationName;

    @NotBlank
    @Size(max = 255)
    private String loanPurpose;
}