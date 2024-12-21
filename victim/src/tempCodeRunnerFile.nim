        let responseJson = %* {
          "type": "exec",
          "client_id": client,
          "state": "res",
          "command": command,
          "data": if killResult: "Process terminated: " & processName
                  else: "Failed to terminate process: " & processName
        }