# Fennec

<p align="center">
<img align='center' src="logo.jpeg" width=400px >
</p>

## Fennec Command and Control (C2) framework with three components: a victim agent, a server, and a control app. The victim connects to the server, and the control app manages the victim through the server.

## Requirements
 
### `JDK 21:` Required for running the server and control app.

### `Gradle 8.5:` Required for building and managing the Java server and control app.

### `NIM 21:` Required for compiling victim app.

## Instructions

1. **Host the Server**  
   Start the server and make it publicly accessible.

2. **Connect Clients and Victims**  
   Configure the client and victim to connect to the server by updating the IP address and port as needed.


## Usage

### Commands
- **List all connected victims:**  
  ```bash
  lv
  
   ```


- **Select a victim:**  
  ```bash
  sv <id>
  
   ```
   example:-
     ```bash
  sv 0
  
   ```

- **Execute commands on the selected victim:**  
  After selecting a victim using `sv <victim_id>`, you can execute the following commands:

    - **View running processes:**  
    Use the following command to display a list of all running processes on the selected victim:  
    ```bash
    exec tasklist
    ```

    - **Kill a specific process:**  
    Use the following command to terminate a specific process on the selected victim:  
    ```bash
    exec kill <process_name>
    ```
    **Example:**  
    To kill the Task Manager process (`taskmgr.exe`), use:

    ```bash
    exec kill taskmgr.exe
    ```