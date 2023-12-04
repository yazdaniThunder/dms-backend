package com.sima.dms.service;

import com.openkm.sdk4j.bean.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public interface ThumbnailService {

    Document createThumbnail(String documentUuid, MultipartFile content);
    Document createThumbnail(String documentUuid, InputStream file, String mimeType);
}
