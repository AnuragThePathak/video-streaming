package com.anurag.video_streaming.utils;

import org.springframework.core.io.Resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ResourceAndContentType {
	private final String contentType;
	private final Resource resource;
}
