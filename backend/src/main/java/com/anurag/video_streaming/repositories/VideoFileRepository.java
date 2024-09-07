package com.anurag.video_streaming.repositories;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface VideoFileRepository {

	String save(String filename, MultipartFile file);

	Resource load(String filePath);

}
