MOV SP, 0x0FFF   ; initialize stack
PUSH 2
PUSH 3
POP B
POP A
CMP A, B
JB true0   ; < comparison
JMP false1
true0: 
false1: 
JMP end