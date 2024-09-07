package com.anurag.video_streaming.repositories;

import org.springframework.core.io.Resource;

public interface ProcessingRepository {

	String process(String videoPathName, String storedPath);

	Resource getHlsMasterFile(String videoPathName);

	Resource getHlsSegmentFile(String videoPathName, int segmentNumber);
	
}
