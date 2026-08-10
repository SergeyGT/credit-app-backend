package credit_app_back.app.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import credit_app_back.app.repository.Page;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDto<T> {
    private int page;
    private int pageSize;
    private long total;
    private List<T> data;
    
    public static <T> PageResponseDto<T> fromPage(Page<T> page) {
        return PageResponseDto.<T>builder()
                .page(page.getPage())
                .pageSize(page.getPageSize())
                .total(page.getTotal())
                .data(page.getContent())
                .build();
    }
}