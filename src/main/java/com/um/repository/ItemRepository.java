package com.um.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.um.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {}
