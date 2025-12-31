package com.example.demo.web.ba02;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.core.exception.AppException;
import com.example.demo.entity.Item;

@WebMvcTest(controllers = BA0201Controller.class)
@TestPropertySource(properties = {
        "spring.thymeleaf.cache=false"
})
public class BA0201ControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ItemSearchService itemSearchService;

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void indexPage_正常系() throws Exception {
        // テストデータの準備
        List<Item> itemList = Arrays.asList(
            new Item(1, "ペン", 100, "CD-A01", LocalDate.now(), 1),
            new Item(2, "鉛筆", 50, "CD-A02", LocalDate.now(), 1)
        );
        Page<Item> mockPage = new PageImpl<>(itemList, PageRequest.of(0, 5), 2);

        // モックの設定
        when(itemSearchService.findAll(any(ItemSearchCriteria.class))).thenReturn(mockPage);

        // テスト実行
        mockMvc.perform(get("/WBA0201/index"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0201/search"))
                .andExpect(model().attributeExists("itemSearchForm"))
                .andExpect(model().attributeExists("pages"))
                .andExpect(model().attributeExists("itemList"))
                .andExpect(model().attribute("itemList", itemList));
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void indexPage_サービス例外発生() throws Exception {
        // モックの設定 - AppExceptionを発生させる
        when(itemSearchService.findAll(any(ItemSearchCriteria.class)))
                .thenThrow(new AppException("ME003"));

        // テスト実行
        mockMvc.perform(get("/WBA0201/index"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0201/search"))
                .andExpect(model().attributeExists("itemSearchForm"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void searchPage_正常系() throws Exception {
        // テストデータの準備
        List<Item> itemList = Arrays.asList(
            new Item(1, "ペン", 100, "CD-A01", LocalDate.now(), 1)
        );
        Page<Item> mockPage = new PageImpl<>(itemList, PageRequest.of(0, 5), 1);

        // モックの設定
        when(itemSearchService.findAll(any(ItemSearchCriteria.class))).thenReturn(mockPage);

        // テスト実行
        mockMvc.perform(get("/WBA0201/search")
                .param("itemName", "ペン")
                .param("price", "100"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0201/search"))
                .andExpect(model().attributeExists("itemSearchForm"))
                .andExpect(model().attributeExists("pages"))
                .andExpect(model().attributeExists("itemList"))
                .andExpect(model().attribute("itemList", itemList));
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void searchPage_入力値検証エラー() throws Exception {
        // テスト実行 - バリデーションエラーが発生するパラメータ
        mockMvc.perform(get("/WBA0201/search")
                .param("itemName", "1234567890123456789012345") // 10桁を超過
                .param("price", "20000")) // 10000を超過
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0201/search"))
                .andExpect(model().attributeExists("itemSearchForm"))
                .andExpect(model().attributeHasFieldErrors("itemSearchForm", "itemName", "price"));
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void searchPage_サービス例外発生() throws Exception {
        // モックの設定 - AppExceptionを発生させる
        when(itemSearchService.findAll(any(ItemSearchCriteria.class)))
                .thenThrow(new AppException("ME002"));

        // テスト実行
        mockMvc.perform(get("/WBA0201/search")
                .param("itemName", "ペン")
                .param("price", "100"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0201/search"))
                .andExpect(model().attributeExists("itemSearchForm"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void searchPage_検索結果null() throws Exception {
        // モックの設定 - nullを返す
        when(itemSearchService.findAll(any(ItemSearchCriteria.class))).thenReturn(null);

        // テスト実行
        mockMvc.perform(get("/WBA0201/search")
                .param("itemName", "ペン"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0201/search"))
                .andExpect(model().attributeExists("itemSearchForm"))
                .andExpect(model().attributeDoesNotExist("pages"))
                .andExpect(model().attributeDoesNotExist("itemList"));
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void searchPage_ページング() throws Exception {
        // テストデータの準備
        List<Item> itemList = Arrays.asList(
            new Item(1, "ペン", 100, "CD-A01", LocalDate.now(), 1),
            new Item(2, "鉛筆", 50, "CD-A02", LocalDate.now(), 1),
            new Item(3, "消しゴム", 80, "CD-A03", LocalDate.now(), 1)
        );
        Page<Item> mockPage = new PageImpl<>(itemList, PageRequest.of(1, 5), 10);

        // モックの設定
        when(itemSearchService.findAll(any(ItemSearchCriteria.class))).thenReturn(mockPage);

        // テスト実行 - 2ページ目を指定
        mockMvc.perform(get("/WBA0201/search")
                .param("itemName", "")
                .param("page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0201/search"))
                .andExpect(model().attributeExists("itemSearchForm"))
                .andExpect(model().attributeExists("pages"))
                .andExpect(model().attributeExists("itemList"))
                .andExpect(model().attribute("itemList", itemList));
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void indexPage_フォームクリア確認() throws Exception {
        // テストデータの準備
        List<Item> itemList = Arrays.asList(
            new Item(1, "ペン", 100, "CD-A01", LocalDate.now(), 1)
        );
        Page<Item> mockPage = new PageImpl<>(itemList, PageRequest.of(0, 5), 1);

        // モックの設定
        when(itemSearchService.findAll(any(ItemSearchCriteria.class))).thenReturn(mockPage);

        // テスト実行 - フォームにパラメータを設定してもクリアされることを確認
        mockMvc.perform(get("/WBA0201/index")
                .param("itemName", "既存の検索条件")
                .param("price", "999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0201/search"));
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void searchPage_空の検索条件() throws Exception {
        // テストデータの準備
        List<Item> itemList = Arrays.asList(
            new Item(1, "ペン", 100, "CD-A01", LocalDate.now(), 1),
            new Item(2, "鉛筆", 50, "CD-A02", LocalDate.now(), 1)
        );
        Page<Item> mockPage = new PageImpl<>(itemList, PageRequest.of(0, 5), 2);

        // モックの設定
        when(itemSearchService.findAll(any(ItemSearchCriteria.class))).thenReturn(mockPage);

        // テスト実行 - 空の検索条件
        mockMvc.perform(get("/WBA0201/search"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0201/search"))
                .andExpect(model().attributeExists("itemSearchForm"))
                .andExpect(model().attributeExists("pages"))
                .andExpect(model().attributeExists("itemList"))
                .andExpect(model().attribute("itemList", itemList));
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void searchPage_商品名のみ指定() throws Exception {
        // テストデータの準備
        List<Item> itemList = Arrays.asList(
            new Item(1, "ペン", 100, "CD-A01", LocalDate.now(), 1)
        );
        Page<Item> mockPage = new PageImpl<>(itemList, PageRequest.of(0, 5), 1);

        // モックの設定
        when(itemSearchService.findAll(any(ItemSearchCriteria.class))).thenReturn(mockPage);

        // テスト実行 - 商品名のみ指定
        mockMvc.perform(get("/WBA0201/search")
                .param("itemName", "ペン"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0201/search"))
                .andExpect(model().attributeExists("itemSearchForm"))
                .andExpect(model().attributeExists("pages"))
                .andExpect(model().attributeExists("itemList"))
                .andExpect(model().attribute("itemList", itemList));
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void searchPage_価格のみ指定() throws Exception {
        // テストデータの準備
        List<Item> itemList = Arrays.asList(
            new Item(2, "鉛筆", 100, "CD-A02", LocalDate.now(), 1)
        );
        Page<Item> mockPage = new PageImpl<>(itemList, PageRequest.of(0, 5), 1);

        // モックの設定
        when(itemSearchService.findAll(any(ItemSearchCriteria.class))).thenReturn(mockPage);

        // テスト実行 - 価格のみ指定
        mockMvc.perform(get("/WBA0201/search")
                .param("price", "100"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0201/search"))
                .andExpect(model().attributeExists("itemSearchForm"))
                .andExpect(model().attributeExists("pages"))
                .andExpect(model().attributeExists("itemList"))
                .andExpect(model().attribute("itemList", itemList));
    }
}
