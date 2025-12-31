package com.example.demo.web.ba02;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.core.exception.AppException;
import com.example.demo.entity.Item;
import com.example.demo.web.mapper.ItemMapper;

import lombok.RequiredArgsConstructor;

/**
 * item検索サービス
 */
@Service
@RequiredArgsConstructor
public class ItemSearchService {
    
    /** itemMapper */
    private final ItemMapper mapper;

    /** 最大検索結果件数 */
    private static final int SEARCH_MAX = 1000;

    /**
     * 全件検索
     * @param criteria 検索条件
     * @return 検索結果リスト
     */
    @Transactional
    public Page<Item> findAll(ItemSearchCriteria criteria){

        // 件数取得
        long total = mapper.countAll(criteria);
        if (total > SEARCH_MAX) {
            // 最大検索結果件数を超過エラー
            throw new AppException("ME002");
        } else if (total == 0) {
            // 検索結果0件エラー
            throw new AppException("ME003");
        }

        // データ取得
        List<Item> itemList = mapper.findAll(criteria);
        return new PageImpl<Item>(itemList, criteria.getPageable(), total);
    }
}
