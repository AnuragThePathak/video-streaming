package com.anurag.video_streaming.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.anurag.video_streaming.models.Video;
import com.anurag.video_streaming.services.VideoService;
import com.anurag.video_streaming.utils.ResourceAndContentType;
import com.anurag.video_streaming.utils.StreamRangeReturns;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/videos")
@CrossOrigin(origins = "http://localhost:5173")
public class VideoController {

	private final VideoService videoService;

	VideoController(VideoService videoService) {
		this.videoService = videoService;
	}

	@PostMapping
	public Video save(@RequestParam String title,
			@RequestParam String description,
			@RequestParam MultipartFile file) {
		return videoService.save(title, description, file);
	}

	@GetMapping("/{videoId}")
	public Video get(@PathVariable String videoId) {
		return videoService.get(videoId);
	}

	@GetMapping("/title/{title}")
	public Video getByTitle(@PathVariable String title) {
		return videoService.getByTitle(title);
	}

	@GetMapping
	public List<Video> getAll() {
		return videoService.getAll();
	}

	@GetMapping("/stream/{videoId}")
	public ResponseEntity<Resource> stream(@PathVariable String videoId) {
		ResourceAndContentType resourceAndContentType = videoService.stream(videoId);
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(
						resourceAndContentType.getContentType()))
				.body(resourceAndContentType.getResource());
	}

	@GetMapping("/stream/range/{videoId}")
	public ResponseEntity<Resource> streamRange(
			@PathVariable String videoId,
			@RequestHeader(value = "Range", required = false) String range) {
		if (range == null) {
			ResourceAndContentType resourceAndContentType = videoService.stream(videoId);
			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(
							resourceAndContentType.getContentType()))
					.body(resourceAndContentType.getResource());
		}

		StreamRangeReturns values = videoService.streamRange(videoId, range);

		return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
				.contentType(MediaType.parseMediaType(values.getContentType()))
				.contentLength(values.getContentLength())
				.header("Content-Range", values.getContentRange())
				.header("Accept-Ranges", values.getAcceptRanges())
				.body(values.getResource());
	}

	@GetMapping("/{videoId}/master.m3u8")
	public ResponseEntity<Resource> getHlsMasterFile(@PathVariable String videoId) {
		Resource resource = videoService.getHlsMasterFile(videoId);
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
				.body(resource);
	}

	@GetMapping("/{videoId}/segment_{segment}.ts")
	public ResponseEntity<Resource> getHlsSegmentFile(@PathVariable String videoId,
			@PathVariable int segment) {
		Resource resource = videoService.getHlsSegmentFile(videoId, segment);
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("video/mp2t"))
				.body(resource);
	}

}
