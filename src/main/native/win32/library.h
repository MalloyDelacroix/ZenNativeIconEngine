#ifndef ICONEXTRACTOR_LIBRARY_H
#define ICONEXTRACTOR_LIBRARY_H

#include <rpc.h>

typedef void (*Callback)(HICON);

extern "C" __declspec(dllexport) void getIcon(const char* filePath, Callback call);

#endif