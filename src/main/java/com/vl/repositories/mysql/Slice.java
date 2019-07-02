package com.vl.repositories.mysql;

import org.springframework.context.annotation.Profile;

import javax.persistence.*;

@Profile("mysql")
@Entity
@Table(name="slices")
public class Slice implements com.vl.repositories.SliceBase {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private int id;
	private String url;
	private String content;
	private int sliceId;

	public int getId() {
		return id;
	}

	public Slice setId(int id) {
		this.id = id;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public Slice setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getContent() {
		return content;
	}

	public Slice setContent(String content) {
		this.content = content;
		return this;
	}

	public int getSliceId() {
		return sliceId;
	}

	public Slice setSliceId(int sliceId) {
		this.sliceId = sliceId;
		return this;
	}
}
