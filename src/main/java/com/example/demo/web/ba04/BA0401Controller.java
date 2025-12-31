package com.example.demo.web.ba04;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.example.demo.core.exception.AppException;
import com.example.demo.entity.Item;
import com.example.demo.web.ba02.ItemSearchForm;
import com.example.demo.web.ba02.ItemSearchService;

import lombok.RequiredArgsConstructor;

/**
 * item削除画面コントローラ
 */
@Controller
@SessionAttributes(types=ItemSearchForm.class)
@RequiredArgsConstructor
public class BA0401Controller {

    /** item削除サービス */
    private final ItemDeleteService itemDeleteService;
    /** item検索サービス */
    private final ItemSearchService itemSearchService;
    /** メッセージ */
    private final MessageSource messages;
    /** 1ページあたりの表示件数 */
    private static final int PAGE_SIZE = 5;

    /**
     * item削除画面を初期表示する。検索条件が未指定で検索した結果を指定件数分だけ表示する
     * 初期表示と検索条件が未指定の場合のページング
     * @param form ItemSearchForm
     * @param result BindingResult
     * @param model Model
     * @return item削除画面
     */
    @GetMapping("/WBA0401/index")
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
                //model.addAttribute("itemDeleteForm", deleteForm);
            }
        } catch(AppException e) {
            result.reject(e.getMessageId());
        }

        return "BA0401/delete";
    }

    /**
     * item検索を行う。指定された検索条件で検索した結果を指定件数分だけ表示する
     * 検索ボタン押下時と検索ボタン押下後のページング
     * @param form ItemSearchForm
     * @param result BindingResult
     * @param model Model
     * @return item削除画面
     */
    @GetMapping("/WBA0401/search")
    public String search(@Validated ItemSearchForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "BA0401/delete";
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
        
        return "BA0401/delete";
    }

    /**
     * itemを削除する
     * @param model Model
     * @return item削除画面
     */
    @PostMapping("/WBA0401/delete")
    public String index(ItemSearchForm form, @Validated ItemDeleteForm deleteForm, BindingResult result, Model model) {
        
        if (result.hasErrors()) {
            // 削除フォームに単項目エラーがあった場合には、予期しないエラーのため、システムエラー画面へ遷移する
            return "error";
        }

        // 削除処理
        try {
            itemDeleteService.deleteOne(deleteForm.getId(), deleteForm.getVersionNo());
        } catch (OptimisticLockingFailureException e) {
            model.addAttribute("message", 
                messages.getMessage(e.getMessage(), null, Locale.getDefault()));
            return "BA0401/delete";
        }
        
        // 検索処理
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
            return "BA0401/delete";
        }
        return "BA0401/delete";
    }
}
