package me.wener.cbhistory.service;

import me.wener.cbhistory.domain.RawData;

public interface RawDataService
{
    RawData save(RawData rawData);
    RawData findOne(Long id);
}
