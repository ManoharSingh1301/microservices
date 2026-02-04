package com.example.assets.asset.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.assets.asset.entity.Asset;

public interface AssetRepository extends JpaRepository<Asset, Long> {
}
