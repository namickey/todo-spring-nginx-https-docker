package com.example.demo.web.ba01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import com.example.demo.core.exception.AppException;
import com.example.demo.entity.Item;
import com.example.demo.web.mapper.ItemMapper;

/**
 * ItemRegistService テストクラス
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ItemRegistService のテスト")
class ItemRegistServiceTest {
    
    @InjectMocks
    private ItemRegistService target;

    @Mock
    private ItemMapper itemMapper;

    private Item validItem;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2023, 7, 1);
        validItem = new Item(1, "ペン", 1000, "CD-A01", testDate, 0);
    }

    @Nested
    @DisplayName("正常系テスト")
    class SuccessTest {

        @Test
        @DisplayName("正常な商品を登録できる")
        void shouldRegisterItemSuccessfully() {
            // Given（前提条件）
            ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
            when(itemMapper.findAllByItemName(anyString())).thenReturn(new ArrayList<>());
            when(itemMapper.insertItem(any(Item.class))).thenReturn(1);

            // When（実行）
            target.registItem(validItem);

            // Then（検証）
            verify(itemMapper).insertItem(itemCaptor.capture());
            Item capturedItem = itemCaptor.getValue();
            
            assertThat(capturedItem.getId()).isEqualTo(1);
            assertThat(capturedItem.getItemName()).isEqualTo("ペン");
            assertThat(capturedItem.getPrice()).isEqualTo(1000);
            assertThat(capturedItem.getGroupid()).isEqualTo("CD-A01");
            assertThat(capturedItem.getRegistDate()).isEqualTo(testDate);
            assertThat(capturedItem.getVersionNo()).isEqualTo(0);
        }

        @Test
        @DisplayName("価格がnullの商品を登録できる")
        void shouldRegisterItemWithNullPrice() {
            // Given（前提条件）
            Item itemWithNullPrice = new Item(2, "ペン", null, "CD-A01", testDate, 0);
            ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
            when(itemMapper.findAllByItemName(anyString())).thenReturn(new ArrayList<>());
            when(itemMapper.insertItem(any(Item.class))).thenReturn(1);

            // When（実行）
            target.registItem(itemWithNullPrice);

            // Then（検証）
            verify(itemMapper).insertItem(itemCaptor.capture());
            Item capturedItem = itemCaptor.getValue();
            
            assertThat(capturedItem.getId()).isEqualTo(2);
            assertThat(capturedItem.getItemName()).isEqualTo("ペン");
            assertThat(capturedItem.getPrice()).isNull();
            assertThat(capturedItem.getGroupid()).isEqualTo("CD-A01");
            assertThat(capturedItem.getRegistDate()).isEqualTo(testDate);
            assertThat(capturedItem.getVersionNo()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("異常系テスト")
    class ErrorTest {

        @Test
        @DisplayName("同一商品名の合計価格が3000円以上の場合、業務エラーとなる")
        void shouldThrowAppExceptionWhenTotalPriceExceeds3000() {
            // Given（前提条件）
            List<Item> existingItems = Arrays.asList(
                new Item(1, "ペン", 1000, "CD-A01", testDate, 0),
                new Item(2, "ペン", 1500, "CD-A01", testDate, 0)
            );
            Item newItem = new Item(5, "ペン", 1000, "CD-A01", testDate, 0);
            when(itemMapper.findAllByItemName("ペン")).thenReturn(existingItems);

            // When（実行） & Then（検証）
            assertThatThrownBy(() -> target.registItem(newItem))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("messageId", "ME001");
        }

        @Test
        @DisplayName("キー重複エラーが発生した場合、業務エラーとなる")
        void shouldThrowAppExceptionWhenDuplicateKeyOccurs() {
            // Given（前提条件）
            when(itemMapper.findAllByItemName(anyString())).thenReturn(new ArrayList<>());
            when(itemMapper.insertItem(any(Item.class))).thenThrow(new DuplicateKeyException("Duplicate key"));

            // When（実行） & Then（検証）
            assertThatThrownBy(() -> target.registItem(validItem))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("messageId", "ME004");
        }
    }
}
