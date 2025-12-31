package com.example.demo.web.ba01;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.core.exception.AppException;
import com.example.demo.entity.Item;
import com.example.demo.web.mapper.ItemMapper;

import lombok.RequiredArgsConstructor;

/**
 * item登録サービス
 */
@Service
@RequiredArgsConstructor
public class ItemRegistService {
    
    /** itemMapper */
    private final ItemMapper mapper;

    private static final Logger LOG = LoggerFactory.getLogger(ItemRegistService.class);

    /**
     * itemを登録する
     * @param item Item
     */
    @Transactional
    public void registItem(Item item) {
        
        // 同一ItemNameの合計priceが3000円以上の場合、業務エラー
        List<Item> list = mapper.findAllByItemName(item.getItemName());
        
        Integer totalPrice = 0;
        if (item.getPrice() == null) {
            totalPrice = 0;
        } else {
            totalPrice = item.getPrice();
        }

        for (Item item2 : list) {
            totalPrice += item2.getPrice();
        }
        if (totalPrice >= 3000) {
            throw new AppException("ME001");
        }

        try {
            // 登録処理
            mapper.insertItem(item);
            LOG.info("item登録完了:id=" + item.getId());
        } catch (DuplicateKeyException e) {
            // キー重複エラー
            throw new AppException("ME004", "id", e);
        }
    }
}
