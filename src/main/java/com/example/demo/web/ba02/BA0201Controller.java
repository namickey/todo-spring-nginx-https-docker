package com.example.demo.web.ba02;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.example.demo.core.exception.AppException;
import com.example.demo.entity.Item;

import lombok.RequiredArgsConstructor;

/**
 * item照会画面コントローラ
 * 
 * 照会画面処理表示時には検索条件をリセットして、検索結果を初期表示する。
 * 照会画面処理表示以外では、検索条件をセッションスコープ保持する。
 * 1ページにページングを行う。
 */
@Controller
@SessionAttributes(types=ItemSearchForm.class)
@RequiredArgsConstructor
public class BA0201Controller {

    /** item検索サービス */
    private final ItemSearchService itemSearchService;

    /** 1ページあたりの表示件数 */
    private static final int PAGE_SIZE = 5;

    /**
     * item照会画面を初期表示する。検索条件が未指定で検索した結果を指定件数分だけ表示する
     * 初期表示と検索条件が未指定の場合のページング
     * @param form ItemSearchForm
     * @param result BindingResult
     * @param model Model
     * @return item照会画面
     */
    @GetMapping("/WBA0201/index")
    public String index(@Validated ItemSearchForm form, BindingResult result, Model model) {
        // 初期表示時は検索条件をクリアする
        form.clear();

        try {
            Page<Item> pages = itemSearchService.findAll(form.toCriteria(PAGE_SIZE));
            if (pages != null) {
                // ページングに必要な情報
                model.addAttribute("pages", pages);
                // 検索結果リスト
                model.addAttribute("itemList", pages.getContent());
            }
        } catch(AppException e) {
            result.reject(e.getMessageId());
        }

        return "BA0201/search";
    }

    /**
     * item検索を行う。指定された検索条件で検索した結果を指定件数分だけ表示する
     * 検索ボタン押下時と検索ボタン押下後のページング
     * @param form ItemSearchForm
     * @param result BindingResult
     * @param model Model
     * @return item照会画面
     */
    @GetMapping("/WBA0201/search")
    public String search(@Validated ItemSearchForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "BA0201/search";
        }

        try {
            Page<Item> pages = itemSearchService.findAll(form.toCriteria(PAGE_SIZE));
            if (pages != null) {
                // ページングに必要な情報
                model.addAttribute("pages", pages);
                // 検索結果リスト
                model.addAttribute("itemList", pages.getContent());
            }
        } catch (AppException e){
            result.reject(e.getMessageId());
        }
        
        return "BA0201/search";
    }
}
