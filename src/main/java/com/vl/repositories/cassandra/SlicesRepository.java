package com.vl.repositories.cassandra;

import com.vl.repositories.cassandra.Slice;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Profile("!mysql")

public interface SlicesRepository extends CassandraRepository<Slice, Key> {
	Slice findOneByKeyUrlAndKeySliceId(String url, int sliceId);
	List<Slice> findAllByKeyUrl(String url);
}
