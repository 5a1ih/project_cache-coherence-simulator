#include "includes.hpp"
#include "Cache.hpp"
#include "MemoryTrace.hpp"

int main()
{
    int cacheLines = 512;
    int cacheLineSize = 4; 
    std::vector<Cache> caches;
    caches.push_back(
        Cache(0,cacheLines, cacheLineSize)
    );
    caches.push_back(
        Cache(1,cacheLines, cacheLineSize)
    );
    caches.push_back(
        Cache(2,cacheLines, cacheLineSize)
    );
    caches.push_back(
        Cache(3,cacheLines, cacheLineSize)
    );

    MemoryTrace mt(caches, "trace1.txt");
}