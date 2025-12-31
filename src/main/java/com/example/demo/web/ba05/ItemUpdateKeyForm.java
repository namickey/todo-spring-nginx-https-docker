package com.example.demo.web.ba05;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemUpdateKeyForm {
    /**
     * ID
     * 必須チェック
     */
    @NotNull
    private Integer id;
}
