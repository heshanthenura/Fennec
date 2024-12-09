import winim/com

# Procedure to get the list of process names
proc getProcessList(): seq[string] =
  # Initialize the WMI object to query the system
  var wmi = GetObject(r"winmgmts:{impersonationLevel=impersonate}!\\.\root\cimv2")

  # Create a list to store the process names
  var processList: seq[string] = @[]

  # Query to get all processes
  for i in wmi.execQuery("select * from win32_process"):
    processList.add(i.name)  # Add the process name to the list

  return processList

# Call the procedure and get the process list
let processes = getProcessList()

# Output the process names
echo "List of Process Names:"
echo processes
