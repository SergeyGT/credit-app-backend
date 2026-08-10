package credit_app_back.app.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Page<T> {
    private int page;
    private int pageSize;
    private long total;
    private List<T> content;
    
    public static <T> Page<T> of(int page, int pageSize, long total, List<T> content) {
        return new Page<>(page, pageSize, total, content);
    }
    
    public static <T> Page<T> empty() {
        return new Page<>(0, 0, 0, List.of());
    }
}
