import std/net, std/socketstreams, strutils, os, asyncdispatch, logging

proc connectToServer(host: string, port: Port) =
  var socket = newSocket(AF_INET, SOCK_STREAM, IPPROTO_TCP)
  socket.connect(host, port)
  
  while true:
    let receivedString =  socket.recvLine()
    if receivedString.len > 0:
      echo "Received: ", receivedString

when isMainModule:
  let host = "127.0.0.1"
  let port = Port(8080)
  connectToServer(host, port)
