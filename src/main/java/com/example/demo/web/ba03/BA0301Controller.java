package com.example.demo.web.ba03;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.Item;

import lombok.RequiredArgsConstructor;

/**
 * item詳細画面コントローラ
 */
@Controller
@RequiredArgsConstructor
public class BA0301Controller {

    private final ItemDetailService itemDetailService;

    /**
     * item詳細画面表示する。
     * @param model Model
     * @return item照会画面
     */
    @GetMapping("/WBA0301/index")
    public String index(@Validated ItemDetailForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // 詳細フォームに単項目エラーがあった場合には、予期しないエラーのため、システムエラー画面へ遷移する
            return "error";
        }

        Item item = itemDetailService.find(form.getId());
        model.addAttribute("item", item);

        return "BA0301/index";
    }
}
