
    if parsedJson.hasKey("type") and parsedJson["type"].getStr() == "exec":
      let command = parsedJson["command"].getStr()
      let client = parsedJson["client_id"].getStr()
      if command == "pwd": 
        await ws.send(%* {"type": "exec", "client_id": client,"rt":"res","data":getCurrentDir()})
        