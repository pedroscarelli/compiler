package com.compiler.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CompilerTest {

    private static final String AREA_PROGRAM = """
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
        """;

    @TempDir
    Path dir;

    private String compile(String program) throws IOException {
        var source = dir.resolve("program.txt").toFile();
        Files.writeString(source.toPath(), program);
        return new Compiler(program, source, new FileManager(null)).getResultFeedback();
    }

    @Test
    void validProgramGeneratesObjectCode() throws IOException {
        assertEquals("program compiled successfully", compile(AREA_PROGRAM));

        var objectCode = Files.readString(dir.resolve("program.il"));
        assertTrue(objectCode.contains(".locals(int64 i_lado)"));
        assertTrue(objectCode.contains("call int64 [mscorlib]System.Int64::Parse(string)"));
        assertTrue(objectCode.contains("call void [mscorlib]System.Console::Write(int64)"));
        assertTrue(objectCode.endsWith("ret\n}\n}\n"));
    }

    @Test
    void undeclaredVariableReportsTheVariableAndLine() throws IOException {
        var result = compile("main\n  i_a;\n  i_b = 1;\nend\n");

        assertEquals("Error on line 3 - i_b not declared", result);
        assertFalse(dir.resolve("program.il").toFile().exists());
    }

    @Test
    void redeclaredVariableIsReported() throws IOException {
        var result = compile("main\n  i_a;\n  i_a;\nend\n");

        assertTrue(result.startsWith("Error on line 3"), result);
        assertTrue(result.contains("i_a already declared"), result);
    }

    @Test
    void incompatibleTypesAreReported() throws IOException {
        var result = compile("main\n  i_a, s_b;\n  i_a = s_b + 1;\nend\n");

        assertTrue(result.startsWith("Error on line 3 - Types not compatible"), result);
    }

    @Test
    void missingEndReportsEndOfFile() throws IOException {
        var result = compile("main\n  i_a;\n");

        assertTrue(result.startsWith("Error on line"), result);
        assertTrue(result.contains("found EOF"), result);
    }

    @Test
    void syntaxErrorOnIndentedLineDoesNotCrash() throws IOException {
        var result = compile("   main\n  i_a;\n  i_a = = 1;\nend\n");

        assertTrue(result.startsWith("Error on line 3"), result);
    }
}
