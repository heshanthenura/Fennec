import asyncdispatch, ws, json

proc main() {.async.} =
  
  var ws = await newWebSocket("ws://192.168.1.101:8080/ws")
  echo "Connected to WebSocket server!"

  # Send a JSON message immediately after the connection is established
  var jsonObj = %* {"type": "set_role", "role": "victim"}
  let jsonMessage = $jsonObj
  await ws.send(jsonMessage)
  echo "Sent message: ", jsonMessage

  while true:
    let message = await ws.receiveStrPacket()
    echo "Received message: ", message

    # Wait for 5 seconds before sending another message
    await sleepAsync(5000)

  ws.close()
  echo "WebSocket connection closed."

waitFor main()
