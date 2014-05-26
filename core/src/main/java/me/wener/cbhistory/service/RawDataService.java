package me.wener.cbhistory.service;

import me.wener.cbhistory.domain.RawData;

public interface RawDataService
{
    void delete(Long id);
    RawData findBySid(Long id, int page);
}
