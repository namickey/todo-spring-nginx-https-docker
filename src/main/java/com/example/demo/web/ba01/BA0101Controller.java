package com.example.demo.web.ba01;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.common.code.ItemGroup;
import com.example.demo.core.exception.AppException;

import lombok.RequiredArgsConstructor;

/*
 * item登録画面コントローラ
 * 
 * 以下のような画面遷移を行う
 * 登録画面⇒確認画面⇒完了画面
 */
@Controller
@RequiredArgsConstructor
public class BA0101Controller {

    /** item登録サービス */
    private final ItemRegistService itemRegistService;

    /**
     * 表示
     * @param itemForm ItemForm
     * @return 登録画面
     */
    @GetMapping("/WBA0101/index")
    public String index(ItemForm itemForm) {

        // 開発用。ダミーデータを初期表示する
        itemForm.setId(13);
        itemForm.setItemName("ペン");
        itemForm.setPrice(1000);
        itemForm.setGroupid("CD-A01");
        itemForm.setRegistDate(LocalDate.now());

        return "BA0101/regist";
    }

    /**
     * 確認
     * @param itemForm ItemForm
     * @param result BindingResult
     * @return 正常なら確認画面 or 入力エラーなら登録画面
     */
    @PostMapping("/WBA0101/confirm")
    public String confirm(@Validated ItemForm itemForm, BindingResult result) {
        
        if (result.hasErrors()) {
            // フォームクラスによる単項目チェックエラーがある場合、登録画面を表示
            return "/BA0101/regist";
        }
        // 確認画面を表示
        return "BA0101/confirm";
    }

    /**
     * 登録
     * @param itemForm ItemForm
     * @param result BindingResult
     * @return　完了画面 or 登録画面
     */
    @PostMapping(path="/WBA0101/regist", params="regist")
    public String regist(@Validated ItemForm itemForm, BindingResult result) {

        if (result.hasErrors()) {
            // フォームクラスによる単項目チェックエラーがある場合、登録画面を表示
            return "/BA0101/regist";
        }

        try {
            // 登録処理
            itemRegistService.registItem(itemForm.toItem());
        } catch(AppException e) {
            if (e.getField() != null) {
                result.rejectValue(e.getField(), e.getMessageId());
            } else {
                result.reject(e.getMessageId());
            }
            return "BA0101/regist";
        }
        
        return "redirect:/WBA0101/complete";
    }
    
    /**
     * 戻る
     * @param itemForm ItemForm
     * @return 登録画面
     */
    @PostMapping(path="/WBA0101/regist", params="back")
    public String back(ItemForm itemForm) {
        return "BA0101/regist";
    }

    /**
     * 完了
     * @return 完了画面
     */
    @GetMapping("/WBA0101/complete")
    public String complete() {
        return "BA0101/complete";
    }

    /**
     * 分類コードのプルダウンリスト
     * @return プルダウンリスト
     */
    @ModelAttribute("groups")
    public List<ItemGroup> getGroupid() {
        return Arrays.stream(ItemGroup.values())
        .collect(Collectors.toList());
    }
}
