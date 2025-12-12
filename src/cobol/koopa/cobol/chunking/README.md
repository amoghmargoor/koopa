# COBOL Chunker for RAG Services

A semantic chunker for COBOL code that uses the `Cobol.kg` grammar to extract meaningful code segments for Retrieval-Augmented Generation (RAG) systems.

## Overview

Traditional text chunkers split code by fixed sizes (e.g., 1000 characters), which often breaks semantic units. This chunker uses Koopa's COBOL grammar to extract chunks at **semantic boundaries**:

- **Program level**: Entire COBOL program
- **Division level**: IDENTIFICATION, ENVIRONMENT, DATA, PROCEDURE divisions
- **Section level**: Sections within divisions (WORKING-STORAGE, FILE SECTION, etc.)
- **Paragraph level**: Named paragraphs in PROCEDURE DIVISION
- **Statement level**: Individual statements (optional, for fine-grained retrieval)

## Features

✅ **Semantic Boundaries**: Chunks respect COBOL structure (divisions, sections, paragraphs)  
✅ **Hierarchical Relationships**: Parent-child relationships between chunks  
✅ **Rich Metadata**: Program names, division/section/paragraph names, line numbers  
✅ **Dependency Extraction**: Identifies COPY statements and CALL statements  
✅ **JSON Export**: Ready-to-use JSON format for RAG systems  

## Usage

### Basic Example

```java
import koopa.cobol.projects.StandardCobolProject;
import koopa.cobol.chunking.CobolChunker;
import koopa.cobol.chunking.CobolChunker.Chunk;
import java.io.File;

// Create a COBOL project
StandardCobolProject project = new StandardCobolProject();
project.setDefaultPreprocessing(true);
project.addCopybookPath(new File("copybooks/"));

// Create chunker
CobolChunker chunker = new CobolChunker(project);

// Extract chunks
List<Chunk> chunks = chunker.chunk(
    new File("program.cbl"),
    CobolChunker.ChunkLevel.PROGRAM,
    CobolChunker.ChunkLevel.DIVISION,
    CobolChunker.ChunkLevel.SECTION,
    CobolChunker.ChunkLevel.PARAGRAPH
);

// Process chunks
for (Chunk chunk : chunks) {
    System.out.println(chunk.level + ": " + chunk.metadata.get("name"));
    System.out.println("Lines: " + chunk.startLine + "-" + chunk.endLine);
    System.out.println("Text: " + chunk.text.substring(0, Math.min(100, chunk.text.length())));
}
```

### Command Line Usage

```bash
# Compile
ant cobol

# Run example
java -cp "build:lib/*" koopa.cobol.chunking.CobolChunkerExample program.cbl output.json
```

## Chunk Structure

Each chunk contains:

```java
public class Chunk {
    ChunkLevel level;              // PROGRAM, DIVISION, SECTION, PARAGRAPH, STATEMENT
    String text;                   // The actual COBOL code
    Map<String, String> metadata;  // Rich metadata (see below)
    int startLine;                 // Starting line number
    int endLine;                   // Ending line number
    Chunk parent;                  // Parent chunk (for hierarchy)
    List<Chunk> children;          // Child chunks
    String chunkId;                // Unique identifier
}
```

### Metadata Fields

| Field | Description | Example |
|-------|-------------|---------|
| `name` | Name of the chunk | "MAIN-PROGRAM", "WORKING-STORAGE", "PROCESS-RECORD" |
| `type` | Type of chunk | "PROGRAM", "DIVISION", "SECTION", "PARAGRAPH" |
| `program` | Program name (for nested chunks) | "MAIN-PROGRAM" |
| `division` | Division name (for sections/paragraphs) | "DATA", "PROCEDURE" |
| `section` | Section name (for paragraphs) | "WORKING-STORAGE" |
| `file` | Source file name | "program.cbl" |
| `filePath` | Full file path | "/path/to/program.cbl" |
| `copyStatements` | Comma-separated list of COPY statements | "WS-COMMON, FILE-LAYOUT" |
| `callStatements` | Comma-separated list of CALL statements | "SUBPROG1, SUBPROG2" |

## JSON Export Format

The chunker can export chunks to JSON for direct use in RAG systems:

```json
{
  "chunks": [
    {
      "chunkId": "program:MAIN-PROGRAM:1234567890",
      "level": "PROGRAM",
      "text": "IDENTIFICATION DIVISION.\nPROGRAM-ID. MAIN-PROGRAM.\n...",
      "startLine": 1,
      "endLine": 500,
      "metadata": {
        "name": "MAIN-PROGRAM",
        "type": "PROGRAM",
        "file": "program.cbl",
        "copyStatements": "WS-COMMON",
        "callStatements": "SUBPROG1, SUBPROG2"
      },
      "childChunkIds": ["division:IDENTIFICATION:1234567891", ...]
    },
    {
      "chunkId": "division:DATA:1234567892",
      "level": "DIVISION",
      "text": "DATA DIVISION.\nWORKING-STORAGE SECTION.\n...",
      "startLine": 50,
      "endLine": 200,
      "metadata": {
        "name": "DATA",
        "type": "DIVISION",
        "program": "MAIN-PROGRAM"
      },
      "parentChunkId": "program:MAIN-PROGRAM:1234567890",
      "childChunkIds": ["section:WORKING-STORAGE:1234567893", ...]
    }
  ]
}
```

## Chunking Strategies for RAG

### Strategy 1: Hierarchical Chunking (Recommended)

Extract chunks at multiple levels to provide context at different granularities:

```java
chunks = chunker.chunk(file,
    ChunkLevel.PROGRAM,    // Full program context
    ChunkLevel.DIVISION,   // Division-level context
    ChunkLevel.SECTION,    // Section-level context
    ChunkLevel.PARAGRAPH   // Paragraph-level detail
);
```

**Benefits:**
- RAG can retrieve at appropriate granularity
- Hierarchical relationships provide context
- Smaller chunks for precise retrieval, larger chunks for context

### Strategy 2: Paragraph-Only Chunking

For PROCEDURE DIVISION-focused RAG:

```java
chunks = chunker.chunk(file, ChunkLevel.PARAGRAPH);
```

**Benefits:**
- Each paragraph is a self-contained unit
- Good for function-level code search
- Smaller, focused chunks

### Strategy 3: Section-Level Chunking

For data structure analysis:

```java
chunks = chunker.chunk(file, ChunkLevel.SECTION);
```

**Benefits:**
- WORKING-STORAGE sections as complete units
- FILE SECTION entries grouped together
- Good for data structure understanding

## Integration with RAG Systems

### For Vector Databases (Pinecone, Weaviate, etc.)

1. **Extract chunks** using the chunker
2. **Generate embeddings** for each chunk's `text` field
3. **Store embeddings** with chunk metadata as metadata fields
4. **Use hierarchical relationships** to retrieve parent/child chunks when needed

Example:
```python
# Pseudo-code
chunks = cobol_chunker.chunk(file)
for chunk in chunks:
    embedding = embed(chunk.text)
    vector_db.upsert(
        id=chunk.chunkId,
        vector=embedding,
        metadata={
            "level": chunk.level,
            "name": chunk.metadata["name"],
            "program": chunk.metadata.get("program"),
            "startLine": chunk.startLine,
            "endLine": chunk.endLine,
            "parentChunkId": chunk.parent.chunkId if chunk.parent else None
        }
    )
```

### For LangChain / LlamaIndex

Use the JSON export directly:

```python
import json

with open("chunks.json") as f:
    chunks_data = json.load(f)

# Create documents
documents = []
for chunk in chunks_data["chunks"]:
    doc = Document(
        text=chunk["text"],
        metadata={
            **chunk["metadata"],
            "chunkId": chunk["chunkId"],
            "level": chunk["level"],
            "startLine": chunk["startLine"],
            "endLine": chunk["endLine"]
        }
    )
    documents.append(doc)

# Create index
index = VectorStoreIndex.from_documents(documents)
```

## Best Practices

1. **Enable Preprocessing**: Always enable preprocessing to handle COPY statements correctly
   ```java
   project.setDefaultPreprocessing(true);
   project.addCopybookPath(new File("copybooks/"));
   ```

2. **Choose Appropriate Levels**: Don't extract all levels if you don't need them - it increases storage and processing time

3. **Use Hierarchical Retrieval**: When a paragraph chunk matches, also retrieve its parent section/division for context

4. **Include Dependencies**: The metadata includes COPY and CALL statements - use these to build dependency graphs

5. **Handle Large Programs**: For very large programs, consider chunking at SECTION or PARAGRAPH level only

## Limitations

- **Requires Valid COBOL**: The chunker needs to successfully parse the COBOL file
- **Grammar-Dependent**: Chunk boundaries depend on the Cobol.kg grammar structure
- **No Statement-Level Chunking Yet**: Statement-level chunking is planned but not yet implemented

## Future Enhancements

- [ ] Statement-level chunking
- [ ] Overlapping chunks (sliding window)
- [ ] Chunk size limits with smart splitting
- [ ] Export to other formats (CSV, Parquet)
- [ ] Incremental chunking (only changed sections)
- [ ] Cross-reference extraction (data names, paragraph names)

## See Also

- [CobolDependencyGrammar](../grammar/dependency/README.md) - For dependency extraction
- [Koopa Parser Documentation](../../../../README.md) - General parser documentation
- [TRACING-GUIDE.md](../../../../TRACING-GUIDE.md) - How to debug parsing issues

