package com.example.assets.asset.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.example.assets.asset.entity.Asset;
import com.example.assets.asset.enums.AssetStatus;
import java.util.List;
 
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByStatus(AssetStatus status);
}
 