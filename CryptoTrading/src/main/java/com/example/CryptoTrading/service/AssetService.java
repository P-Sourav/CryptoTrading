package com.example.CryptoTrading.service;

import java.util.List;

import com.example.CryptoTrading.entities.Asset;
import com.example.CryptoTrading.entities.Coin;
import com.example.CryptoTrading.entities.User;

public interface AssetService 
{
	Asset createAsset(User user,Coin coin, double quantity);
	
	Asset getAssetById(Long assetId) throws Exception;
	
	Asset getAssetByUserId(Long userId, Long assetId);

	List<Asset> getUserAssets(Long userId);
	
	Asset updateAsset(Long assetId, double quantity) throws Exception;
	
	Asset findAssetByUserIdAndCoinId(Long userId, String coinId);
	
	void deleteAsset(Long assetId);
	
}
