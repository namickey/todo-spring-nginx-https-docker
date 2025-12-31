package com.example.demo.web.ba05;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.common.code.ItemGroup;
import com.example.demo.entity.Item;
import com.example.demo.web.ba03.ItemDetailService;

import lombok.RequiredArgsConstructor;

/**
 * item更新画面コントローラ
 */
@Controller
@RequiredArgsConstructor
public class BA0501Controller {

    private final ItemDetailService itemDetailService;
    private final ItemUpdateService itemUpdateService;

    /**
     * 更新画面を初期表示する
     * @param form ItemUpdateForm
     * @param result BindingResult
     * @return 更新画面
     */
    @GetMapping("/WBA0501/index")
    public String index(@Validated ItemUpdateKeyForm form,BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "error";
        }

        // itemカラム全量を取得して、formに設定
        Item item = itemDetailService.find(form.getId());

        ItemUpdateForm itemUpdateForm = new ItemUpdateForm();
        itemUpdateForm.setItem(item);
        
        model.addAttribute("itemUpdateForm", itemUpdateForm);
        return "BA0501/update.html";
    }

    /**
     * 確認
     * @param form ItemUpdateForm
     * @param result BindingResult
     * @return 確認画面
     */
    @PostMapping("/WBA0501/confirm")
    public String confirm(@Validated ItemUpdateForm form,BindingResult result) {
        if (result.hasErrors()) {
            return "/BA0501/update";
        }
        return "BA0501/confirm";
    }

    /**
     * 更新
     * @param form ItemUpdateForm
     * @param result BindingResult
     * @return 完了画面
     */
    @PostMapping(path="/WBA0501/update", params="update")
    public String update(@Validated ItemUpdateForm form,BindingResult result) {
        if (result.hasErrors()) {
            return "/BA0501/update";
        }

        try {
            itemUpdateService.update(form.toItem());
        } catch (OptimisticLockingFailureException e) {
            result.reject(e.getMessage());
            return "BA0501/update";
        }
        
        return "redirect:/WBA0501/complete";
    }

    /**
     * 戻る
     * @param form ItemUpdateForm
     * @return 更新画面
     */
    @PostMapping(path="/WBA0501/update", params="back")
    public String back(ItemUpdateForm form) {
        return "BA0501/update";
    }

    /**
     * 完了
     * @return 完了画面
     */
    @GetMapping("/WBA0501/complete")
    public String complete() {
        return "BA0501/complete";
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
