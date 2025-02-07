import winim/lean
import winim/com
import nimPNG

proc takeScreenshot(filename: string) =
  let virtualWidth = GetSystemMetrics(SM_CXVIRTUALSCREEN)
  let virtualHeight = GetSystemMetrics(SM_CYVIRTUALSCREEN)
  let virtualX = GetSystemMetrics(SM_XVIRTUALSCREEN)
  let virtualY = GetSystemMetrics(SM_YVIRTUALSCREEN)

  let hdcScreen = GetDC(0)
  let hdcMem = CreateCompatibleDC(hdcScreen)
  let hBitmap = CreateCompatibleBitmap(hdcScreen, virtualWidth, virtualHeight)
  let hOld = SelectObject(hdcMem, hBitmap)
  BitBlt(hdcMem, 0, 0, virtualWidth, virtualHeight, hdcScreen, virtualX, virtualY, SRCCOPY)
  SelectObject(hdcMem, hOld)

  var bi: BITMAPINFO
  bi.bmiHeader.biSize = sizeof(BITMAPINFOHEADER).DWORD
  bi.bmiHeader.biWidth = virtualWidth
  bi.bmiHeader.biHeight = -virtualHeight 
  bi.bmiHeader.biPlanes = 1
  bi.bmiHeader.biBitCount = 32
  bi.bmiHeader.biCompression = BI_RGB

  var pixels = newSeq[byte](virtualWidth * virtualHeight * 4)
  discard GetDIBits(hdcMem, hBitmap, 0, virtualHeight.UINT, pixels[0].addr, bi.addr, DIB_RGB_COLORS)
  for i in countup(0, pixels.high, 4):
    swap(pixels[i], pixels[i+2])


  discard savePNG32(filename, pixels, virtualWidth, virtualHeight)

  DeleteObject(hBitmap)
  DeleteDC(hdcMem)
  ReleaseDC(0, hdcScreen)


if CoInitialize(nil) != S_OK:
  quit("Failed to initialize COM")

var classEnum: ICreateDevEnum
if CoCreateInstance(&CLSID_SystemDeviceEnum, nil, CLSCTX_INPROC_SERVER, &IID_ICreateDevEnum, cast[pointer](addr classEnum)) != S_OK:
  quit("Failed to create Device Enumerator")