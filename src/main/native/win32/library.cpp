#include "library.h"
#include <shlobj.h>
//#include <shlguid.h>
//#include <shellapi.h>
//#include <commctrl.h>
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
