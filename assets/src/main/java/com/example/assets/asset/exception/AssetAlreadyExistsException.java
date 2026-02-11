package com.example.assets.asset.exception;
 
public class AssetAlreadyExistsException extends RuntimeException {
   
    public AssetAlreadyExistsException(String message) {
        super(message);
    }
   
    public AssetAlreadyExistsException(Long id) {
        super("Asset already exists with id: " + id);
    }
}