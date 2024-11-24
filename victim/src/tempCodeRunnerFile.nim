{.passC:"-masm=intel".}

import winim/lean

# Declare external functions
proc GetStdHandle(nStdHandle: int32): HANDLE {.stdcall, importc: "GetStdHandle", dynlib: "kernel32".}
proc WriteConsoleA(hConsoleOutput: HANDLE, lpBuffer: cstring, nNumberOfCharsToWrite: int32,
                   lpNumberOfCharsWritten: ptr int32, lpReserved: pointer): bool {.stdcall, importc: "WriteConsoleA", dynlib: "kernel32".}

proc printMessageWinAsm(message: cstring): void {.asmNoStackFrame.} =
    asm """
        ; Get the handle to the standard output
        mov ecx, -11             ; STD_OUTPUT_HANDLE
        call `GetStdHandle`      ; Call GetStdHandle(-11)

        ; Save the handle
        mov r8, rax              ; r8 = hConsoleOutput

        ; Prepare parameters for WriteConsoleA
        mov rcx, r8              ; rcx = hConsoleOutput
        mov rdx, rdi             ; rdx = message (lpBuffer)
        mov r8d, 24              ; r8 = number of chars to write
        xor r9, r9               ; r9 = NULL (reserved)
        call `WriteConsoleA`     ; Call WriteConsoleA

        ret
    """

# Using the procedure to print a message
let message = "Hello, world from inline assembly on Windows!\n".cstring
printMessageWinAsm(message)
