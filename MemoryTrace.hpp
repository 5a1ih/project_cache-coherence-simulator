#include "includes.hpp"

class MemoryTrace
{
private:
    /* data */
public:
    MemoryTrace(/* args */);
    ~MemoryTrace();
};

MemoryTrace::MemoryTrace(/* args */)
{
}

MemoryTrace::~MemoryTrace()
{
}



struct memory_operation
{
    std::string proceccor;
    char operation;
    int address;
};
