package com.example.demo.web.ba04;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemDeleteForm {
    /**
     * ID
     * 必須チェック
     */
    @NotNull
    private Integer id;

    /**
     * バージョン番号
     * 必須チェック
     */
    @NotNull
    private Integer versionNo;
}
