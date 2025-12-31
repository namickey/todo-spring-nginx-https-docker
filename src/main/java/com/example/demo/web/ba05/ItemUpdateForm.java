package com.example.demo.web.ba05;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.example.demo.common.code.ItemGroup;
import com.example.demo.entity.Item;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemUpdateForm {
    /**
     * ID
     * 必須チェック
     */
    @NotNull
    private Integer id;

    /**
     * itemName
     * 10桁チェック
     */
    @Length(max=10)
    private String itemName;

    /**
     * price
     * 最大値5,000チェック
     */
    @Range(max=5000)
    private Integer price;

    /**
     * 分類ID
     * 6桁チェック
     */
    @Length(max=6)
    private String groupid;

    /**
     * バージョン番号
     */
    @NotNull
    private Integer versionNo;

    /**
     * 登録日
     * 日付フォーマットチェック
     */
    @DateTimeFormat(pattern = "uuuu-MM-dd")
    private LocalDate registDate;

    /**
     * 相関チェック：分類が文具かつ価格が2000円未満であること
     * @return 結果
     */
    @AssertTrue(message = "{ME005}")
    public boolean isBunguPrice() {
        if (ItemGroup.BUNGU.getCode().equals(groupid)) {
            if (price != null && price >= 2000) {
                return false;
            }
        }
        return true;
    }

    /**
     * Item生成
     * @return Item
     */
    public Item toItem() {
        Item item = new Item();
        BeanUtils.copyProperties(this, item);
        return item;
    }

    /**
     * Itemを設定
     * @param item Item
     */
    public void setItem(Item item) {
        BeanUtils.copyProperties(item, this);
    }
}
