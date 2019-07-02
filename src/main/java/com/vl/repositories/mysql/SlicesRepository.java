package com.vl.repositories.mysql;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;

@Profile("mysql")
public interface SlicesRepository extends CrudRepository<Slice, Integer> {
	Slice getSliceByUrlAndSliceId(String url, int sliceId);
}
