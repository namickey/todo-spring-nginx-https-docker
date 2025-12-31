package com.example.demo.web.ba03;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Item;
import com.example.demo.web.mapper.ItemMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemDetailService {

    private final ItemMapper itemMapper;

    public Item find(Integer id) {
        return itemMapper.findById(id);
    }
}
