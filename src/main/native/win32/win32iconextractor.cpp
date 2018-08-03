/*
 *    Copyright 2018 Kyle Hickey
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

#include "win32iconextractor.h"
#include <shlobj.h>
#include <commoncontrols.h>

extern "C" __declspec(dllexport)
void getIcon(const char* filePath, Callback call) {
    SHFILEINFO shFileInfo;
    SHGetFileInfo(filePath, -1, &shFileInfo, sizeof(shFileInfo), SHGFI_SYSICONINDEX);
    HIMAGELIST* imageList;
    HRESULT hResult = SHGetImageList(SHIL_JUMBO, IID_IImageList, (void**)&imageList);

    if (hResult == S_OK) {
        HICON icon;
        hResult = ((IImageList*)imageList)->GetIcon(shFileInfo.iIcon, ILD_TRANSPARENT, &icon);
        if (hResult == S_OK) {
            call(icon);
        }
    }
}
