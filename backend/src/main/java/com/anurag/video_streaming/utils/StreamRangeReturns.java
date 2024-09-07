package com.anurag.video_streaming.utils;

import org.springframework.core.io.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class StreamRangeReturns {
	private final Resource resource;
	private final String contentType;
	private final String contentRange;
	private final String acceptRanges;
	private final long contentLength;
}
