cmake_minimum_required(VERSION 3.10)
project(IconExtractor)

set(CMAKE_CXX_STANDARD 11)

add_library(IconExtractor SHARED win32iconextractor.cpp win32iconextractor.h)