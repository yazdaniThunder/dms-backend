//package com.sima.dms.config;
//
//import com.sima.dms.domain.dto.DocumentOcrDto;
//import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//@Configuration
//@EnableConfigurationProperties(RedisProperties.class)
//public class RedisConfig {
//
//    @Bean("candidateFiles")
//    public RedisTemplate<String, String> candidateFiles(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, String> candidateFiles = new RedisTemplate<>();
//        candidateFiles.setConnectionFactory(redisConnectionFactory);
//        candidateFiles.setKeySerializer(new StringRedisSerializer());
//        candidateFiles.setHashKeySerializer(new StringRedisSerializer());
//        return candidateFiles;
//    }
//
//    @Bean("ocrDocument")
//    public RedisTemplate<String, DocumentOcrDto> ocrDocument(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, DocumentOcrDto> ocrDtoRedisTemplate = new RedisTemplate<>();
//        ocrDtoRedisTemplate.setConnectionFactory(redisConnectionFactory);
//        ocrDtoRedisTemplate.setKeySerializer(new StringRedisSerializer());
//        ocrDtoRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        return ocrDtoRedisTemplate;
//    }
//
////    @Bean("convertPdfToImage")
////    public RedisTemplate<String, ConvertDto> convertPdfToImage(RedisConnectionFactory redisConnectionFactory) {
////        RedisTemplate<String, ConvertDto> convertPdfToImage = new RedisTemplate<>();
////        convertPdfToImage.setConnectionFactory(redisConnectionFactory);
////        convertPdfToImage.setKeySerializer(new StringRedisSerializer());
////        convertPdfToImage.setHashKeySerializer(new StringRedisSerializer());
////        return convertPdfToImage;
////    }
//}
