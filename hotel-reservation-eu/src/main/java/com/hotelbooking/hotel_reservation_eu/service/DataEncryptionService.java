package com.hotelbooking.hotel_reservation_eu.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 数据加密服务
 * 用于PII数据的加密和解密
 */
@Slf4j
@Service
public class DataEncryptionService {

    @Value("${aws.kms.data-encryption-key}")
    private String dataEncryptionKey;

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    /**
     * 加密PII数据
     * @param plainText 明文
     * @return 加密后的Base64字符串
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.trim().isEmpty()) {
            return plainText;
        }

        try {
            // 生成密钥
            SecretKeySpec keySpec = generateKeySpec();
            
            // 生成随机IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            
            // 初始化加密器
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);
            
            // 加密
            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // 将IV和加密数据合并
            byte[] encryptedWithIv = new byte[GCM_IV_LENGTH + encryptedData.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedData, 0, encryptedWithIv, GCM_IV_LENGTH, encryptedData.length);
            
            return Base64.getEncoder().encodeToString(encryptedWithIv);
            
        } catch (Exception e) {
            log.error("加密失败: {}", e.getMessage());
            throw new RuntimeException("数据加密失败", e);
        }
    }

    /**
     * 解密PII数据
     * @param encryptedText 加密的Base64字符串
     * @return 解密后的明文
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.trim().isEmpty()) {
            return encryptedText;
        }

        try {
            // 解码Base64
            byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);
            
            // 提取IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(encryptedWithIv, 0, iv, 0, GCM_IV_LENGTH);
            
            // 提取加密数据
            byte[] encryptedData = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
            System.arraycopy(encryptedWithIv, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);
            
            // 生成密钥
            SecretKeySpec keySpec = generateKeySpec();
            
            // 初始化解密器
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);
            
            // 解密
            byte[] decryptedData = cipher.doFinal(encryptedData);
            
            return new String(decryptedData, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            log.error("解密失败: {}", e.getMessage());
            throw new RuntimeException("数据解密失败", e);
        }
    }

    /**
     * 生成密钥规范
     */
    private SecretKeySpec generateKeySpec() throws Exception {
        // 使用SHA-256哈希配置的密钥，确保密钥长度为32字节
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(dataEncryptionKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    /**
     * 验证加密配置是否正确
     */
    public boolean isEncryptionConfigured() {
        return dataEncryptionKey != null && !dataEncryptionKey.trim().isEmpty();
    }
} 