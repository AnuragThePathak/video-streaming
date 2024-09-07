package com.anurag.video_streaming.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anurag.video_streaming.models.Video;

public interface MetadataRepository extends JpaRepository<Video, String> {

	Optional<Video> findByTitle(String title);

}
