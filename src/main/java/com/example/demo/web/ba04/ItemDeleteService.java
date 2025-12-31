package com.example.demo.web.ba04;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.web.mapper.ItemMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemDeleteService {
    private final ItemMapper itemMapper;

    @Transactional
    public void deleteOne(Integer id, Integer versionNo) {
        int count = itemMapper.delete(id, versionNo);
        if (count == 0) {
            throw new OptimisticLockingFailureException("ME901");
        }
    }
}
