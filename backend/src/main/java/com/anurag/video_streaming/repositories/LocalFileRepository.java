package com.anurag.video_streaming.repositories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@Repository
public class LocalFileRepository implements VideoFileRepository {

	private final String basePath;

	LocalFileRepository(@Value("${files.video.path}") String basePath) {

		this.basePath = StringUtils.cleanPath(basePath);
	}

	@PostConstruct
	private void createFolder() {
		Path dir = Path.of(basePath);
		if (!Files.exists(dir)) {
			try {
				Files.createDirectories(Path.of(basePath));
			} catch (IOException e) {
				throw new RuntimeException("Could not create directory: " + dir, e);
			}
		}
	}

	@Override
	public String save(String filename, MultipartFile file) {
		Path path = Path.of(basePath, filename);

		try {
			Files.copy(file.getInputStream(), path);
		} catch (IOException e) {
			throw new RuntimeException("Could not save file: " + path, e);
		}

		return path.toString();
	}

	@Override
	public Resource load(String filePath) {
		return new FileSystemResource(filePath);
	}

}
