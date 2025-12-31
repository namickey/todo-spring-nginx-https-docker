package com.example.demo.web.ba01;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * ItemRegistViewHelper テストクラス
 */
@DisplayName("ItemRegistViewHelper のテスト")
class ItemRegistViewHelperTest {

    private ItemRegistViewHelper target;

    @BeforeEach
    void setUp() {
        target = new ItemRegistViewHelper();
    }
    
    @ParameterizedTest
    @DisplayName("グループIDに対応するグループ名を取得できる")
    @CsvSource({
        "CD-A01, 文具",
        "CD-A02, その他"
    })
    void shouldReturnCorrectGroupName(String groupid, String expectedLabel) {
        // Given（前提条件）
        // パラメータで設定済み
        
        // When（実行）
        String actualLabel = target.getGroupName(groupid);
        
        // Then（検証）
        assertThat(actualLabel).isEqualTo(expectedLabel);
    }

    @ParameterizedTest
    @DisplayName("不明なグループIDの場合、nullを返す")
    @CsvSource({
        "unknown",
        "''",
        "INVALID"
    })
    void shouldReturnNullForUnknownGroupId(String groupid) {
        // Given（前提条件）
        // パラメータで設定済み
        
        // When（実行）
        String actualLabel = target.getGroupName(groupid);
        
        // Then（検証）
        assertThat(actualLabel).isNull();
    }

    @Test
    @DisplayName("nullのグループIDの場合、nullを返す")
    void shouldReturnNullForNullGroupId() {
        // Given（前提条件）
        String groupid = null;
        
        // When（実行）
        String actualLabel = target.getGroupName(groupid);
        
        // Then（検証）
        assertThat(actualLabel).isNull();
    }
}
