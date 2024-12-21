import std/[asyncdispatch, httpclient, osproc]

proc downloadFile(link:string,name:string) {.async.} =
  let client = newAsyncHttpClient()
  try:
   
    await downloadFile(client, 
      link,name)
    echo "File downloaded successfully."
  except Exception as e:
    echo "Error occurred during download or execution: ", e.msg
  finally:
    client.close()
