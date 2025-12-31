package com.example.demo.entity;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * itemエンティティ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Integer id;
    private String itemName;
    private Integer price;
    private String groupid;
    private LocalDate registDate;
    private Integer versionNo;
}
