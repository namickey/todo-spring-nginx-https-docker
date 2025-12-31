package com.example.demo.web.ba05;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Item;
import com.example.demo.web.mapper.ItemMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemUpdateService {
    private final ItemMapper itemMapper;

    @Transactional
    public void update(Item item) {

        // 未実装：業務ロジック実装

        int i = itemMapper.updateById(item);
        if (i == 0) {
            throw new OptimisticLockingFailureException("ME901");
        }
    }
}
