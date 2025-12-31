package com.example.demo.web.ba03;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemDetailForm {
    /**
     * ID
     * 必須チェック
     */
    @NotNull
    private Integer id;
}
