# Source Code to MSIL Compiler

This project implements a complete compiler (lexical, syntactic and semantic analysis) that reads a source code file (`.txt`), generates object code in MSIL (`.il`), and then allows it to be converted into an executable (`.exe`) on Windows.

### Requirements:

Java 17 or higher

---

## 🚀 Features

- **Text Editor**: Interface for creating, opening, editing and saving code files.
- **Feedback Terminal**: Displays error or success messages.
- **File Manager**: Browse the system to select and save source files.
- **Lexical Analysis**: Identifies tokens defined by regular expression rules.
- **Syntactic Analysis**: Validates the program structure according to the LL grammar.
- **Semantic Analysis**: Checks type consistency and semantic rules.
- **MSIL Code Generation**: Emits IL instructions compatible with .NET/CLI.

---

## 📚 Grammar and Configuration (GALS)

The compiler was built with GALS and uses the following configuration:

### Options    
GenerateScanner = true  
GenerateParser = true  
Language = Java  
ScannerName = Lexico  
ParserName = Sintatico  
SemanticName = Semantico  
ScannerCaseSensitive = true  
ScannerTable = Compact  
Input = String  
Parser = LL  


### Tokens    
pr: [a-z] [a-zA-Z]* (reserved word)  
id:! (i_|f_|b_|s_) ([a-z][A-Z]? | [A-Z]) (([a-z] | [0-9]) [A-Z]?) (identifier)  
cte: [1-9] [0-9]* | 0 (integer constant)  
cte_2: ([1-9][0-9]* | 0) , 0-9* (float constant)  
ct: " ([^\n"%](%x))* " (string constant)  


:! >@ \n [^@]* \n @< (comment)  


:[\n\s\t] (line break, space and tab)  


// Reserved words and symbols  
delimiter tokens (main, read, write, etc.) and logical, relational and arithmetic operators.

### Non-Terminals
<lista_steps>  <lista_id>        ...
### Main Productions

```bnf
<program> ::= #100 main <lista_steps> end #101 ;
<lista_steps> ::= <instrucao> ";" <lista_steps1> ;
<instrucao> ::= <lista_id> <lista_id1> | <input> | <output> | <if> | <repeat> ;
...
```

For the full grammar, see the GALS configuration file 'grammar-rules.gals' in the 'gals' folder.
To use GALS, run the following command in the 'gals' folder:  
```bash
java -jar gals.jar
```
Then, if you want to inspect the grammars in the program, open the 'grammar-rules.gals' file, which is also in the 'gals' folder.

GALS documentation: https://gals.sourceforge.net/

---

## 🧪 Building and testing

```bash
mvn test       # semantic actions plus end-to-end compiler tests
mvn package    # builds target/compiler-1.0.jar
```

---

## 💡 How to Use

1. Create a source code file following the defined grammar (for example, program.txt).

2. Run the compiler from a command prompt:
   ```bash
   java -jar compiler.jar
   ```
3. This will open the compiler. You can then open the file you created by clicking the open button or using ctrl + o (or, if you prefer, write your code from scratch in the editor, in which case you must save the file before continuing).

4. After opening your code written in txt, you will see its content in the text editor.

5. Click compile, or use the F7 hotkey. If there are errors, the compiler terminal will show the line and the error.

6. If there are no errors, it will generate the .il program with the same name as the .txt file, in the folder where the file you opened or saved is located.

7. Now, in your computer's command prompt, in the folder where the file is saved, use the command:
   
```bash
C:\Windows\Microsoft.NET\Framework64\v4.0.30319\ilasm program.il
```
8. This will produce a program.exe file. To run it, execute the command in the terminal:
```bash
program
```

---

## 📝 Usage Example

### Input file (area.txt):
```bash
main
  i_lado, i_area;

  repeat
     read (i_lado);
     if i_lado < 0 || i_lado == 0
        writeln ("invalid value");
     end;
  until i_lado > 0;
  i_area = i_lado * i_lado;
  write (i_area);

end
```
### Generated .il:
```bash
.assembly extern mscorlib {}
.assembly _codigo_objeto{}
.module _codigo_objeto.exe

.class public _UNICA{
  .method static public void _principal(){
     .entrypoint
     .locals (int64 i_lado)
	 .locals (int64 i_area)
	 
	 // repeat
     L1:
	 call string [mscorlib]System.Console::ReadLine()
     call int64 [mscorlib]System.Int64::Parse(string)
     stloc i_lado
     
	 // if i_lado < 0 || i_lado == 0
	 ldloc i_lado
     conv.r8
     ldc.i8 0
     conv.r8 
     clt
     ldloc i_lado
     conv.r8
     ldc.i8 0
     conv.r8 
     ceq
     or
     brfalse L3
     
	 ldstr "invalid value"
     call void [mscorlib]System.Console::WriteLine(string)
     br L2  
     L3:
	 L2:
	 
	 // until i_lado > 0
     ldloc i_lado
     conv.r8
     ldc.i8 0
     conv.r8 
     cgt
     brfalse L1        

     ldloc i_lado
     conv.r8
     ldloc i_lado
     conv.r8
     mul
     conv.i8
     stloc i_area
     ldloc i_area
     conv.r8
     conv.i8
     call void [mscorlib]System.Console::Write(int64)

     ret
  }
}
```
