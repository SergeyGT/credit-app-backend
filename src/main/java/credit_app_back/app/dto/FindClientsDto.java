package credit_app_back.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindClientsDto implements Filterable {

    private String firstName;
    private String lastName;
    private String middleName;
    private String passport;
    private String phone;
}