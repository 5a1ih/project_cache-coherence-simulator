#include "includes.hpp"
#include "MemoryOperation.hpp"

class MemoryTrace
{
private:
    std::vector<MemoryOperation> memory_trace;
    std::string trace_file_path;
public:
    MemoryTrace(std::vector<Cache> &cache, const std::string trace_file_path);
    ~MemoryTrace();
    void parseTracefile();
};

MemoryTrace::MemoryTrace(std::vector<Cache> &cache, const std::string trace_file_path)
    : memory_trace {}, trace_file_path {trace_file_path}
{
    std::ifstream file(trace_file_path);
    std::string line;

    if (file.is_open())
    {
        while (getline(file, line, '\n'))
        {
            std::string splitLine[3];
            std::stringstream ssin(line);
            ssin >> splitLine[0] >> splitLine[1] >> splitLine[2];
            int idxCache = splitLine[0][1]-'0';
            memory_trace.push_back(
                MemoryOperation(cache.at(idxCache), splitLine[1], std::stoi(splitLine[2]))
            );
        }
        file.close();
    }
}

MemoryTrace::~MemoryTrace()
{
}

void MemoryTrace::parseTracefile() 
{
}