package com.vl.repositories.cassandra;

import com.google.common.base.Objects;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@PrimaryKeyClass
public class Key implements Serializable {

	@PrimaryKeyColumn(
			name = "url",
			ordinal = 0,
			type = PrimaryKeyType.CLUSTERED)
	private String url;
	@PrimaryKeyColumn(
			name = "slice_id", ordinal = 1,
			type = PrimaryKeyType.PARTITIONED)
	private int sliceId;

	public Key() {
	}

	public Key(String url, int sliceId) {
		this.url = url;
		this.sliceId = sliceId;
	}

	public String getUrl() {
		return url;
	}

	public Key setUrl(String url) {
		this.url = url;
		return this;
	}

	public int getSliceId() {
		return sliceId;
	}

	public Key setSliceId(int sliceId) {
		this.sliceId = sliceId;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Key))
			return false;
		Key key = (Key) o;
		return sliceId == key.sliceId &&
				Objects.equal(getUrl(), key.getUrl());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getUrl(), sliceId);
	}
}