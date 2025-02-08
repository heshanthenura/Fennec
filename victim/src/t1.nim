import winim



proc capCreateCaptureWindowA(lpszWindowName: LPCSTR, dwStyle: DWORD, x: int32, y: int32, 
                             width: int32, height: int32, hwndParent: HWND, nID: int32): HWND {.stdcall, dynlib: "avicap32", importc.}




proc captureImage(outputFile: string) =
  const WM_CAP_DRIVER_CONNECT = 0x0400 + 10
  const WM_CAP_DRIVER_DISCONNECT = 0x0400 + 11
  const WM_CAP_SAVEDIB = 0x0400 + 25
  const WM_CAP_GRAB_FRAME = 0x0400 + 60
  const WM_CAP_EDIT_COPY = 0x0400 + 30
  let capWnd = capCreateCaptureWindowA("", WS_POPUP, 0, 0, 1, 1, 0, 0)  # Fully hidden window
  if capWnd == 0:
    echo "Failed to create capture window"
    return

  if SendMessageA(capWnd, WM_CAP_DRIVER_CONNECT, 0, 0) == 0:
    echo "Failed to connect to camera"
    return

  SendMessageA(capWnd, WM_CAP_GRAB_FRAME, 0, 0)  # Capture the frame
  let result = SendMessageA(capWnd, WM_CAP_SAVEDIB, 0, cast[LPARAM](outputFile.cstring))  # Convert to cstring first!

  SendMessageA(capWnd, WM_CAP_DRIVER_DISCONNECT, 0, 0)  # Disconnect camera

  if result == 0:
    echo "Failed to save image"
  else:
    echo "Image saved successfully to: ", outputFile

captureImage("captured.bmp")
