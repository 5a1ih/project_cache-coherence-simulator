#include "includes.hpp"

class CacheSimulator
{
private:
    int cacheLines;
    int cacheLineSize;      
    std::string processor;

public:
    CacheSimulator(std::string processor, int cacheLine, int cacheLineSize);
    ~CacheSimulator();
};

CacheSimulator::CacheSimulator(std::string processor, int cacheLine, int cacheLineSize)
    : processor {processor}, cacheLines {cacheLine}, cacheLineSize {cacheLines}
{
}

CacheSimulator::~CacheSimulator() {
}
