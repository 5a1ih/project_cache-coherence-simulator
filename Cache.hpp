#pragma once
#include "includes.hpp"

class Cache
{
private:
    int cacheLines;
    int cacheLineSize;      
    int processorId;

public:
    Cache(int processorId, int cacheLine, int cacheLineSize);
    ~Cache();
    int getProcessorId() const;
};

Cache::Cache(int processorId, int cacheLines, int cacheLineSize)
    : processorId {processorId}, cacheLines {cacheLines}, cacheLineSize {cacheLineSize}
{
}

Cache::~Cache() {
}

int Cache::getProcessorId() const 
{
    return processorId;
}
