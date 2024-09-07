package com.anurag.video_streaming.models;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Video {

	@Id
	@UuidGenerator
	private String videoId;

	private String title;

	private String description;

	private String contentType;

	private String filePath;

	private String videoPathName;
	
}
