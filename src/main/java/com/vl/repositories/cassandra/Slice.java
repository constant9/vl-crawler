package com.vl.repositories.cassandra;

import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Table;

@Profile("!mysql")
//@Entity
@Table(name="slices")
public class Slice implements com.vl.repositories.SliceBase {

	@EmbeddedId
	@PrimaryKey
	private Key key;

	@Column
	private String content;

	public Key getKey() {
		return key;
	}

	public Slice setKey(Key key) {
		this.key = key;
		return this;
	}

	public String getContent() {
		return content;
	}

	public Slice setContent(String content) {
		this.content = content;
		return this;
	}
}
