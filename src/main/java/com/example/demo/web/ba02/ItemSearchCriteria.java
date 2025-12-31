package com.example.demo.web.ba02;

import org.springframework.data.domain.Pageable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 検索条件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchCriteria {
    private String itemName;
    private Integer price;
    private Pageable pageable;
}
