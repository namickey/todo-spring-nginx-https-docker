package com.example.demo.web.ba02;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.example.demo.core.exception.AppException;
import com.example.demo.entity.Item;
import com.example.demo.web.mapper.ItemMapper;

/**
 * Item検索サービステスト
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ItemSearchService のテスト")
public class ItemSearchServiceTest {

    @InjectMocks
    private ItemSearchService target;

    @Mock
    private ItemMapper itemMapper;

    @Nested
    @DisplayName("正常系テスト")
    class SuccessTest {

        @Test
        @DisplayName("検索結果が1件であること")
        public void shouldReturnOneItem() {
            // Given（前提条件）
            when(itemMapper.countAll(any())).thenReturn(1L);

            List<Item> list = new ArrayList<>();
            list.add(new Item(1, "ペン", 100, "CD-A01", LocalDate.now(), 0));
            when(itemMapper.findAll(any())).thenReturn(list);
            ItemSearchCriteria criteria = new ItemSearchCriteria(null, null, PageRequest.of(0, 5));

            // When（実行）
            Page<Item> result = target.findAll(criteria);

            // Then（検証）
            ArgumentCaptor<ItemSearchCriteria> criteriaCaptor = ArgumentCaptor.forClass(ItemSearchCriteria.class);
            verify(itemMapper, times(1)).countAll(criteriaCaptor.capture());
            verify(itemMapper, times(1)).findAll(criteriaCaptor.capture());
            assertThat(criteriaCaptor.getAllValues()).hasSize(2);
            assertThat(criteriaCaptor.getAllValues().get(0)).isEqualTo(criteria);
            assertThat(criteriaCaptor.getAllValues().get(1)).isEqualTo(criteria);

            assertThat(result.getNumberOfElements()).isEqualTo(1); // 1ページ目は1件
            assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
            assertThat(result.getTotalPages()).isEqualTo(1); // 1件÷5件=1ページ
            assertThat(result.getTotalElements()).isEqualTo(1L);

            assertThat(result.getContent().get(0).getId()).isEqualTo(1);
            assertThat(result.getContent().get(0).getItemName()).isEqualTo("ペン");
            assertThat(result.getContent().get(0).getPrice()).isEqualTo(100);
            assertThat(result.getContent().get(0).getGroupid()).isEqualTo("CD-A01");
            assertThat(result.getContent().get(0).getVersionNo()).isEqualTo(0);
        }

        @Test
        @DisplayName("検索条件に合致する商品を5件取得できる（ページング1ページ目）")
        void testFindItemsByCriteria() {
            // Given（前提条件）
            List<Item> items = new ArrayList<>();
            items.add(new Item(1, "ペン", 100, "CD-A01", LocalDate.now(), 0));
            items.add(new Item(2, "ペン", 200, "CD-A01", LocalDate.now(), 0));
            items.add(new Item(3, "ペン", 300, "CD-A01", LocalDate.now(), 0));
            items.add(new Item(4, "ペン", 400, "CD-A01", LocalDate.now(), 0));
            items.add(new Item(5, "ペン", 500, "CD-A01", LocalDate.now(), 0));
            when(itemMapper.findAll(any())).thenReturn(items); // 1ページ目の5件

            when(itemMapper.countAll(any())).thenReturn(10L); // 合計10件

            ItemSearchCriteria criteria = new ItemSearchCriteria("ペン", null, PageRequest.of(0, 5));

            // When（実行）
            Page<Item> result = target.findAll(criteria);

            // Then（検証）
            verify(itemMapper, times(1)).countAll(any());
            verify(itemMapper, times(1)).findAll(any());

            assertThat(result.getNumberOfElements()).isEqualTo(5); // 1ページ目は5件
            assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
            assertThat(result.getTotalPages()).isEqualTo(2); // 10件÷5件=2ページ
            assertThat(result.getTotalElements()).isEqualTo(10L);
            assertThat(result.getContent().get(0).getItemName()).isEqualTo("ペン");
        }

        @Test
        @DisplayName("トータル件数が1000件の場合、正常に処理されること")
        void testTotal1000Items() {
            // Given（前提条件）
            List<Item> items = new ArrayList<>();
            items.add(new Item(1, "ペン", 100, "CD-A01", LocalDate.now(), 0));
            items.add(new Item(2, "ペン", 200, "CD-A01", LocalDate.now(), 0));
            items.add(new Item(3, "ペン", 300, "CD-A01", LocalDate.now(), 0));
            items.add(new Item(4, "ペン", 400, "CD-A01", LocalDate.now(), 0));
            items.add(new Item(5, "ペン", 500, "CD-A01", LocalDate.now(), 0));
            when(itemMapper.findAll(any())).thenReturn(items); // 1ページ目の5件

            when(itemMapper.countAll(any())).thenReturn(1000L); // 合計1000件

            ItemSearchCriteria criteria = new ItemSearchCriteria(null, null, PageRequest.of(0, 5));

            // When（実行）
            Page<Item> result = target.findAll(criteria);

            // Then（検証）
            verify(itemMapper, times(1)).countAll(any());
            verify(itemMapper, times(1)).findAll(any());

            assertThat(result.getNumberOfElements()).isEqualTo(5); // 1ページ目は5件
            assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
            assertThat(result.getTotalPages()).isEqualTo(200); // 1000件÷5件=200ページ
            assertThat(result.getTotalElements()).isEqualTo(1000L);
            assertThat(result.getContent().get(0).getItemName()).isEqualTo("ペン");
        }
    }

    @Nested
    @DisplayName("異常系テスト")
    class ErrorTest {

        @Test
        @DisplayName("検索結果が無い場合、ゼロ件エラーが発生すること")
        public void testCountZero() {
            // Given（前提条件）
            when(itemMapper.countAll(any())).thenReturn(0L);

            ItemSearchCriteria criteria = new ItemSearchCriteria("aaa", null, PageRequest.of(0, 3));

            // When（実行） & Then（検証）
            assertThatThrownBy(() -> target.findAll(criteria))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("messageId", "ME003");

            verify(itemMapper, times(1)).countAll(any());
            verify(itemMapper, never()).findAll(any());
        }

        @Test
        @DisplayName("検索結果が1001件以上の場合、1000件超過エラーが発生すること")
        public void testCountThousand() {
            // Given（前提条件）
            when(itemMapper.countAll(any())).thenReturn(1001L);

            ItemSearchCriteria criteria = new ItemSearchCriteria("aaa", null, PageRequest.of(0, 3));

            // When（実行） & Then（検証）
            assertThatThrownBy(() -> target.findAll(criteria))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("messageId", "ME002");

            verify(itemMapper, times(1)).countAll(any());
            verify(itemMapper, never()).findAll(any());
        }
    }
}