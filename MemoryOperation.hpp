#include "includes.hpp"
#include "Cache.hpp"

class MemoryOperation
{
private:
    Cache& cache;
    std::string memory_operation;
    int word;
public:
    MemoryOperation(Cache &cache, const std::string memory_opration, const int word);
    ~MemoryOperation();
};

MemoryOperation::MemoryOperation(Cache& cache, const std::string memory_opration, const int word)
    : cache {cache}, memory_operation {memory_opration}, word {word}
{
}

MemoryOperation::~MemoryOperation()
{
}