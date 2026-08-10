package credit_app_back.app.repository;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pageable {
    @Positive
    private int page;
    
    @Positive
    private int size;
    
    public static Pageable of(int page, int size) {
        return new Pageable(page, size);
    }
}