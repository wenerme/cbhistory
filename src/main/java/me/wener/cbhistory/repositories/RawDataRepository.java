package me.wener.cbhistory.repositories;

import me.wener.cbhistory.domain.RawData;
import org.springframework.data.repository.CrudRepository;

public interface RawDataRepository extends CrudRepository<RawData, Long>
{
}
