package com.example.demo.web.ba01;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.core.exception.AppException;

@WebMvcTest(controllers = BA0101Controller.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ItemRegistViewHelper.class))
@TestPropertySource(properties = {
        "spring.thymeleaf.cache=false"
})
public class BA0101ControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ItemRegistService itemRegistService;

    @Autowired
    ItemRegistViewHelper itemRegistViewHelper;

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void indexPage() throws Exception {

        ItemForm expected = new ItemForm(13, "ペン", 1000, "CD-A01", LocalDate.now());

        mockMvc.perform(get("/WBA0101/index"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0101/regist"))
                .andExpect(model().attributeExists("itemForm"))
                .andExpect(model().attribute("itemForm", org.hamcrest.Matchers.samePropertyValuesAs(expected)));
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void confirmPage_validInput() throws Exception {

        mockMvc.perform(
                post("/WBA0101/confirm")
                        .with(csrf())
                        .param("id", "13")
                        .param("itemName", "ペン")
                        .param("price", "1000")
                        .param("groupid", "CD-A01")
                        .param("registDate", LocalDate.now().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0101/confirm"))
                .andExpect(model().attributeExists("itemForm"));
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void confirmPage_invalidInput() throws Exception {
        mockMvc.perform(
                post("/WBA0101/confirm")
                        .with(csrf())
                        .param("id", "") // invalid: empty id
                        .param("itemName", "size is over 10 length") // invalid: over 10 length
                        .param("price", "-1") // invalid: negative price
                        .param("groupid", "size is over 6 length") // invalid: over 6 length
                        .param("registDate", "abc") // invalid: not a date format
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("/BA0101/regist"))
                .andExpect(model().attributeHasFieldErrors("itemForm", "id", "itemName", "price", "groupid",
                        "registDate"));
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void confirmPage_invalidInput_isBunguPrice() throws Exception {
        mockMvc.perform(
                post("/WBA0101/confirm")
                        .with(csrf())
                        .param("id", "1")
                        .param("itemName", "pen")
                        .param("price", "3000") // 相関チェック
                        .param("groupid", "CD-A01") // 相関チェック
                        .param("registDate", "2023-10-01") // 相関チェック
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("/BA0101/regist"))
                .andExpect(model().attributeHasFieldErrors("itemForm", "bunguPrice"));

    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void registPage_validInput() throws Exception {

        // サービスの正常処理をモック
        doNothing().when(itemRegistService).registItem(any());

        mockMvc.perform(
                post("/WBA0101/regist")
                        .with(csrf())
                        .param("regist", "") // registパラメータを追加
                        .param("id", "13")
                        .param("itemName", "ペン")
                        // .param("price", null) // 価格はnullで0に変換される
                        .param("groupid", "CD-A01")
                        .param("registDate", LocalDate.now().toString()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/WBA0101/complete"));

        // サービスメソッドが呼ばれたことを検証
        verify(itemRegistService, times(1)).registItem(any());
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void registPage_invalidInput() throws Exception {

        mockMvc.perform(
                post("/WBA0101/regist")
                        .with(csrf())
                        .param("regist", "") // registパラメータを追加
                        .param("id", "") // invalid: empty id
                        .param("itemName", "size is over 10 length") // invalid: over 10 length
                        .param("price", "-1") // invalid: negative price
                        .param("groupid", "size is over 6 length") // invalid: over 6 length
                        .param("registDate", "abc")) // invalid: not a date format
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("/BA0101/regist"))
                .andExpect(model().attributeHasFieldErrors("itemForm", "id", "itemName", "price", "groupid",
                        "registDate"));

        // サービスメソッドが呼ばれていないことを検証
        verify(itemRegistService, never()).registItem(any());
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void registPage_serviceException_ME001() throws Exception {

        // サービスでAppExceptionが発生する場合をモック（グローバルエラー）
        doThrow(new AppException("ME001")).when(itemRegistService).registItem(any());

        mockMvc.perform(
                post("/WBA0101/regist")
                        .with(csrf())
                        .param("regist", "") // registパラメータを追加
                        .param("id", "13")
                        .param("itemName", "ペン")
                        .param("price", "1000")
                        .param("groupid", "CD-A01")
                        .param("registDate", LocalDate.now().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0101/regist"))
                .andExpect(model().attributeHasErrors("itemForm"))
                .andExpect(model().attributeErrorCount("itemForm", 1))
                .andExpect(result -> {
                    ModelAndView mv = result.getModelAndView();
                    if(mv != null) {
                        // エラーコードの確認
                        assertEquals("ME001",
                                ((BindingResult) mv.getModel()
                                        .get("org.springframework.validation.BindingResult.itemForm"))
                                        .getGlobalErrors().stream().findFirst().orElseThrow().getCode());
                    }
                });

        // サービスメソッドが呼ばれたことを検証
        verify(itemRegistService, times(1)).registItem(any());
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void registPage_serviceException_ME004() throws Exception {

        // サービスでAppExceptionが発生する場合をモック（グローバルエラー）
        doThrow(new AppException("ME004", "id", new DuplicateKeyException(""))).when(itemRegistService).registItem(any());

        mockMvc.perform(
                post("/WBA0101/regist")
                        .with(csrf())
                        .param("regist", "") // registパラメータを追加
                        .param("id", "13")
                        .param("itemName", "ペン")
                        .param("price", "1000")
                        .param("groupid", "CD-A01")
                        .param("registDate", LocalDate.now().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0101/regist"))
                .andExpect(model().attributeHasErrors("itemForm"))
                .andExpect(model().attributeErrorCount("itemForm", 1))
                .andExpect(model().attributeHasFieldErrorCode("itemForm", "id", "ME004"));
                
        // サービスメソッドが呼ばれたことを検証
        verify(itemRegistService, times(1)).registItem(any());
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void backPage() throws Exception {

        mockMvc.perform(
                post("/WBA0101/regist")
                        .with(csrf())
                        .param("back", "") // backパラメータを追加
                        .param("id", "13")
                        .param("itemName", "ペン")
                        .param("price", "1000")
                        .param("groupid", "CD-A01")
                        .param("registDate", LocalDate.now().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0101/regist"))
                .andExpect(model().attributeExists("itemForm"));

        // backの場合はサービスメソッドが呼ばれないことを検証
        verify(itemRegistService, never()).registItem(any());
    }

    @Test
    @WithMockUser(roles = "DATA_MANAGER")
    void completePage() throws Exception {

        mockMvc.perform(
                get("/WBA0101/complete"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("BA0101/complete"));

        // completeの場合はサービスメソッドが呼ばれないことを検証
        verify(itemRegistService, never()).registItem(any());
    }
}
