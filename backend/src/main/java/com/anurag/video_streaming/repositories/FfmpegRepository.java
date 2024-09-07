package com.anurag.video_streaming.repositories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class FfmpegRepository implements ProcessingRepository {

	private final String hlsPath;

	FfmpegRepository(@Value("${files.video.hls.path}") String hlsPath) {
		this.hlsPath = hlsPath;
	}

	@Override
	public String process(String videoPathName, String storedPath) {
		Path path = Path.of(storedPath);
		Path outputPath = Path.of(hlsPath, videoPathName);
		try {
			Files.createDirectories(outputPath);
		} catch (IOException e) {
			throw new RuntimeException("Could not create directory: " + outputPath, e);
		}

		String ffmpegCmd = String.format(
				"ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\"  \"%s/master.m3u8\" ",
				path, outputPath, outputPath);

		ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", ffmpegCmd);
		processBuilder.inheritIO();
		Process process = null;
		try {
			process = processBuilder.start();
			int exitCode = process.waitFor();
			if (exitCode != 0) {
				throw new RuntimeException("Video processing failed with exit code: " + exitCode);
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not start process", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Process interrupted", e);
		} finally {
			if (process != null) {
				process.destroy();
			}
		}

		return videoPathName;
	}

	@Override
	public Resource getHlsMasterFile(String videoPathName) {
		Path path = Path.of(hlsPath, videoPathName, "master.m3u8");
		
		if (!Files.exists(path)) {
			throw new RuntimeException("Master file not found: " + path);
		}
		return new FileSystemResource(path);
	}

	@Override
	public Resource getHlsSegmentFile(String videoPathName, int segment) {
		Path path = Path.of(hlsPath, videoPathName, String.format("segment_%03d.ts", segment));
		
		if (!Files.exists(path)) {
			throw new RuntimeException("Segment file not found: " + path);
		}
		return new FileSystemResource(path);
	}
}