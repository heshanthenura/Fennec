import winim

const WM_CAP_DRIVER_CONNECT = 0x0400 + 10
const WM_CAP_DRIVER_DISCONNECT = 0x0400 + 11
const WM_CAP_SAVEDIB = 0x0400 + 25
const WM_CAP_GRAB_FRAME = 0x0400 + 60
const WM_CAP_EDIT_COPY = 0x0400 + 30

proc capCreateCaptureWindowA(lpszWindowName: LPCSTR, dwStyle: DWORD, x: int32, y: int32, 
                             width: int32, height: int32, hwndParent: HWND, nID: int32): HWND {.stdcall, dynlib: "avicap32", importc.}

proc captureImage(outputFile: string) =
  let hwndParent = GetDesktopWindow()
  let capWnd = capCreateCaptureWindowA("Webcam Capture", WS_CHILD or WS_VISIBLE, 0, 0, 640, 480, hwndParent, 0)
  echo capWnd
  if capWnd == 0:
    echo "Failed to create capture window"
    return

  if SendMessageA(capWnd, WM_CAP_DRIVER_CONNECT, 0, 0) == 0:
    echo "Failed to connect to camera"
    return

  SendMessageA(capWnd, WM_CAP_GRAB_FRAME, 0, 0)
  let filePath: cstring = outputFile
  let result = SendMessageA(capWnd, WM_CAP_SAVEDIB, 0, cast[LPARAM](filePath))
  SendMessageA(capWnd, WM_CAP_DRIVER_DISCONNECT, 0, 0)
  if result == 0:
    echo "Failed to save image"
  else:
    echo "Image saved successfully to: ", outputFile

captureImage("captured.bmp")