package com.vl.model;

import com.google.common.base.MoreObjects;

public class SliceDto {
	private int number;
	private String content;
	private String url;

	//region ...getters setters

	public String getUrl() {
		return url;
	}

	public SliceDto setUrl(String url) {
		this.url = url;
		return this;
	}

	public int getNumber() {
		return number;
	}

	public SliceDto setNumber(int number) {
		this.number = number;
		return this;
	}

	public String getContent() {
		return content;
	}

	public SliceDto setContent(String content) {
		this.content = content;
		return this;
	}
	//endregion

	//region ...toString-hash-equals
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("number", number)
				.add("url", content)
				.add("content", content)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof SliceDto))
			return false;
		SliceDto slice = (SliceDto) o;
		return getNumber() == slice.getNumber() &&
				com.google.common.base.Objects.equal(getContent(), slice.getContent()) &&
				com.google.common.base.Objects.equal(getUrl(), slice.getUrl());
	}

	@Override
	public int hashCode() {
		return com.google.common.base.Objects.hashCode(getNumber(), getContent(), getUrl());
	}
	//endregion
}
