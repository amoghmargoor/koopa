# COBOL Dependency Grammar

A simplified COBOL grammar focused specifically on extracting dependency information for building dependency graphs and RAG systems.

## Overview

This grammar is designed to be lightweight and focused, parsing only the essential constructs needed to understand dependencies between COBOL files:

- **COPY statements** - References to copybooks and libraries
- **CALL statements** - References to other programs/functions
- **Program definitions** - Program names and structure
- **Function definitions** - Function names and structure

## Files

- `CobolDependency.kg` - The main grammar definition
- `CobolDependency.properties` - Package configuration
- `CobolDependencyBaseGrammar.java` - Base grammar implementation
- `CobolDependencyGrammar.java` - Main grammar class
- `example/DependencyExtractor.java` - Example usage

## Key Features

### 1. COPY Statement Parsing
```cobol
COPY textName [OF libraryName]
COPY WS-COMMON OF COBLIB
```

### 2. CALL Statement Parsing
```cobol
CALL programName USING parameter1 parameter2
CALL 'SUBPROG' USING WS-DATA RETURNING WS-RESULT
```

### 3. Program Definition Parsing
```cobol
IDENTIFICATION DIVISION.
PROGRAM-ID. MAIN-PROGRAM.
...
END PROGRAM MAIN-PROGRAM.
```

### 4. Function Definition Parsing
```cobol
IDENTIFICATION DIVISION.
FUNCTION-ID. CALCULATE-TOTAL.
...
END FUNCTION CALCULATE-TOTAL.
```

### 5. Skip Non-Dependency Statements
The grammar automatically skips any statements that don't contain dependency information:
- Data definitions (unless they contain COPY statements)
- Procedure statements (unless they contain CALL statements)
- Comments and whitespace
- Any other COBOL constructs not related to dependencies

## Usage

### Basic Usage
```java
CobolDependencyGrammar grammar = CobolDependencyGrammar.instance();
ParserCombinator parser = grammar.dependencyAnalysis();
Parse parse = new Parse(source);

if (parser.matches(parse)) {
    // Parse successful - extract dependency information
    // from the parse tree
}
```

### Using the Example Extractor
```java
DependencyExtractor extractor = new DependencyExtractor();
DependencyInfo info = extractor.extractDependencies(cobolFile);
System.out.println(info);
```

## Dependency Information Extracted

The grammar extracts:

1. **COPY Dependencies**: Files referenced via COPY statements
2. **CALL Dependencies**: Programs/functions called via CALL statements  
3. **Program Names**: Names of defined programs and functions
4. **Library References**: Libraries referenced in COPY statements

## Building Dependency Graphs

The example `DependencyExtractor` class includes functionality to build dependency graphs:

```java
List<DependencyInfo> dependencies = extractor.extractDependencies(cobolFiles);
DependencyGraph graph = extractor.buildDependencyGraph(dependencies);
System.out.println(graph);
```

## Advantages over Full COBOL Grammar

1. **Focused**: Only parses dependency-related constructs
2. **Lightweight**: Much smaller and faster than full COBOL grammar
3. **Maintainable**: Easy to understand and modify
4. **Efficient**: Optimized for dependency extraction use cases
5. **RAG-Ready**: Designed specifically for building knowledge graphs

## Integration with RAG Systems

This grammar is perfect for:
- Building COBOL code knowledge graphs
- Creating dependency-aware RAG systems
- Analyzing COBOL codebases for impact analysis
- Generating documentation about program relationships

## Example Output

```
File: MAIN-PROG.CBL
Program Names: [MAIN-PROGRAM]
COPY Dependencies: [WS-COMMON, FILE-DEFS]
CALL Dependencies: [SUBPROG, CALCULATE-TOTAL]

Dependency Graph:
Nodes: [MAIN-PROG.CBL, WS-COMMON, FILE-DEFS, SUBPROG, CALCULATE-TOTAL]
Edges:
  MAIN-PROG.CBL --[COPY]--> WS-COMMON
  MAIN-PROG.CBL --[COPY]--> FILE-DEFS
  MAIN-PROG.CBL --[CALL]--> SUBPROG
  MAIN-PROG.CBL --[CALL]--> CALCULATE-TOTAL
```
