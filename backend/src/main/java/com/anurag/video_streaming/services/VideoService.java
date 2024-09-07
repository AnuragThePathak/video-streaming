package com.anurag.video_streaming.services;

import java.util.List;
import java.util.UUID;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.anurag.video_streaming.models.Video;
import com.anurag.video_streaming.repositories.MetadataRepository;
import com.anurag.video_streaming.repositories.ProcessingRepository;
import com.anurag.video_streaming.repositories.VideoFileRepository;
import com.anurag.video_streaming.utils.ResourceAndContentType;
import com.anurag.video_streaming.utils.StreamRangeReturns;

@Service
public class VideoService {

	private final MetadataRepository metadataRepository;
	private final ProcessingRepository processingRepository;
	private final VideoFileRepository videoFileRepository;

	private final static long CHUNK_SIZE = 1024 * 1024;

	VideoService(
			MetadataRepository videoRepository,
			ProcessingRepository processingRepository,
			VideoFileRepository videoFileRepository) {
		this.metadataRepository = videoRepository;
		this.processingRepository = processingRepository;
		this.videoFileRepository = videoFileRepository;
	}

	// Save video
	public Video save(String title, String description, MultipartFile file) {
		String filename = file.getOriginalFilename();
		String name;
		String uuid = UUID.randomUUID().toString();
		int idx;

		if (filename != null && (idx = filename.lastIndexOf(".")) != -1) {
			String extension = filename.substring(idx + 1);
			name = uuid + "." + extension;
		} else {
			name = uuid;
		}

		String filePath = videoFileRepository.save(name, file);
		processingRepository.process(name, filePath);

		Video video = Video.builder()
				.title(title)
				.description(description)
				.contentType(file.getContentType())
				.filePath(filePath)
				.videoPathName(name)
				.build();

		return metadataRepository.save(video);
	}

	// Get video by id
	public Video get(String videoId) {
		return metadataRepository.findById(videoId).orElseThrow();
	}

	// Get video by title
	public Video getByTitle(String title) {
		return metadataRepository.findByTitle(title).orElseThrow();
	}

	// Get all videos
	public List<Video> getAll() {
		return metadataRepository.findAll();
	}

	// Stream video
	public ResourceAndContentType stream(String videoId) {
		Video video = metadataRepository.findById(videoId).orElseThrow();
		String contentType = video.getContentType();
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		Resource resource = videoFileRepository.load(video.getFilePath());
		return new ResourceAndContentType(contentType, resource);
	}

	public StreamRangeReturns streamRange(String videoId, String range) {
		Video video = metadataRepository.findById(videoId).orElseThrow();
		String contentType = video.getContentType();
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		Resource resource = videoFileRepository.load(video.getFilePath());
		long fileLength = 0;
		try {
			fileLength = resource.contentLength();
		} catch (Exception e) {
			throw new RuntimeException("Could not find resource", e);
		}

		HttpRange httpRange = HttpRange.parseRanges(range).get(0);
		long start = httpRange.getRangeStart(fileLength);
		long end, contentLength;

		byte[] data = new byte[(int) CHUNK_SIZE];
		try {
			InputStream inputStream = resource.getInputStream();
			inputStream.skip(start);
			contentLength = inputStream.read(data);
			end = start + contentLength - 1;
			System.out.println("start: " + start + ", end: " + end + ", contentLength: " + contentLength);
		} catch (IOException e) {
			throw new RuntimeException("Could not read file", e);
		}

		return new StreamRangeReturns(
				new ByteArrayResource(data),
				contentType,
				"bytes " + start + "-" + end + "/" + fileLength,
				"bytes",
				contentLength);
	}

	public Resource getHlsMasterFile(String videoId) {
		Video video = metadataRepository.findById(videoId).orElseThrow();
		return processingRepository.getHlsMasterFile(video.getVideoPathName());
	}

	public Resource getHlsSegmentFile(String videoId, int segment) {
		Video video = metadataRepository.findById(videoId).orElseThrow();
		return processingRepository.getHlsSegmentFile(video.getVideoPathName(), segment);
	}

}
