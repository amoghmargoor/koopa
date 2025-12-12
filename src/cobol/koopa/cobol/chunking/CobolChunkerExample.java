package koopa.cobol.chunking;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import koopa.cobol.projects.StandardCobolProject;
import koopa.cobol.chunking.CobolChunker.Chunk;

/**
 * Example usage of CobolChunker for RAG services.
 * 
 * This demonstrates how to:
 * 1. Create a chunker
 * 2. Extract chunks at different levels
 * 3. Process multiple files from testsuite
 * 4. Export chunks to JSON for RAG systems
 */
public class CobolChunkerExample {

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Usage:");
			System.out.println("  Single file: CobolChunkerExample <cobol-file> [output-json]");
			System.out.println("  Batch mode:  CobolChunkerExample --batch <directory> [output-dir]");
			System.out.println("  Testsuite:   CobolChunkerExample --testsuite [output-dir]");
			System.exit(1);
		}
		
		// Check for batch mode
		if ("--batch".equals(args[0])) {
			if (args.length < 2) {
				System.err.println("Error: --batch requires a directory path");
				System.exit(1);
			}
			File directory = new File(args[1]);
			File outputDir = args.length >= 3 ? new File(args[2]) : null;
			processBatch(directory, outputDir);
			return;
		}
		
		// Check for testsuite mode
		if ("--testsuite".equals(args[0])) {
			File outputDir = args.length >= 2 ? new File(args[1]) : new File("chunks-output");
			processTestsuite(outputDir);
			return;
		}
		
		// Single file mode
		File cobolFile = new File(args[0]);
		if (!cobolFile.exists()) {
			System.err.println("File not found: " + cobolFile);
			System.exit(1);
		}
		
		// Create a COBOL project
		StandardCobolProject project = new StandardCobolProject();
		project.setDefaultPreprocessing(true);
		project.addCopybookPath(cobolFile.getParentFile());
		
		// Create chunker
		CobolChunker chunker = new CobolChunker(project);
		
		// Extract chunks at multiple levels
		System.out.println("Chunking COBOL file: " + cobolFile.getName());
		List<Chunk> chunks = chunker.chunk(cobolFile,
			CobolChunker.ChunkLevel.PROGRAM,
			CobolChunker.ChunkLevel.DIVISION,
			CobolChunker.ChunkLevel.SECTION,
			CobolChunker.ChunkLevel.PARAGRAPH
		);
		
		System.out.println("Extracted " + chunks.size() + " chunks");
		
		// Print summary
		printChunkSummary(chunks);
		
		// Export to JSON if output file specified
		if (args.length >= 2) {
			File outputFile = new File(args[1]);
			exportToJson(chunks, outputFile);
			System.out.println("\nChunks exported to: " + outputFile);
		}
	}
	
	/**
	 * Process multiple files from testsuite
	 */
	private static void processTestsuite(File outputDir) throws IOException {
		File testsuiteDir = new File("testsuite/cobol85");
		if (!testsuiteDir.exists()) {
			System.err.println("Testsuite directory not found: " + testsuiteDir);
			System.exit(1);
		}
		
		// Select a few representative files from different categories
		String[] sampleFiles = {
			"CM101M.CBL",  // Compilation group
			"IC101A.CBL",  // Inter-program communication (CALL statements)
			"IF101A.CBL",  // IF statements
			"NC101A.CBL",  // Numeric comparison
			"SQ101M.CBL",  // Sequential file operations
			"ST101A.CBL"   // String handling
		};
		
		System.out.println("Processing " + sampleFiles.length + " sample files from testsuite...");
		System.out.println("Output directory: " + outputDir.getAbsolutePath());
		
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		
		// Create a COBOL project
		StandardCobolProject project = new StandardCobolProject();
		project.setDefaultPreprocessing(true);
		project.addCopybookPath(testsuiteDir);
		
		// Create chunker
		CobolChunker chunker = new CobolChunker(project);
		
		// Statistics
		int totalFiles = 0;
		int successfulFiles = 0;
		int totalChunks = 0;
		Map<CobolChunker.ChunkLevel, Integer> chunksByLevel = new HashMap<>();
		List<Chunk> allChunks = new ArrayList<>();
		
		// Process each file
		for (String fileName : sampleFiles) {
			File cobolFile = new File(testsuiteDir, fileName);
			if (!cobolFile.exists()) {
				System.out.println("Skipping (not found): " + fileName);
				continue;
			}
			
			totalFiles++;
			System.out.println("\n[" + totalFiles + "/" + sampleFiles.length + "] Processing: " + fileName);
			
			try {
				List<Chunk> chunks = chunker.chunk(cobolFile,
					CobolChunker.ChunkLevel.PROGRAM,
					CobolChunker.ChunkLevel.DIVISION,
					CobolChunker.ChunkLevel.SECTION,
					CobolChunker.ChunkLevel.PARAGRAPH
				);
				
				successfulFiles++;
				totalChunks += chunks.size();
				allChunks.addAll(chunks);
				
				// Count by level
				for (Chunk chunk : chunks) {
					chunksByLevel.put(chunk.level, 
						chunksByLevel.getOrDefault(chunk.level, 0) + 1);
				}
				
				// Export individual file
				File outputFile = new File(outputDir, fileName.replace(".CBL", ".json"));
				exportToJson(chunks, outputFile);
				
				System.out.println("  ✓ Extracted " + chunks.size() + " chunks");
				System.out.println("  ✓ Exported to: " + outputFile.getName());
				
			} catch (Exception e) {
				System.err.println("  ✗ Error processing " + fileName + ": " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		// Export combined chunks
		File combinedFile = new File(outputDir, "all-chunks.json");
		exportToJson(allChunks, combinedFile);
		
		// Print summary
		System.out.println("\n" + "=".repeat(60));
		System.out.println("BATCH PROCESSING SUMMARY");
		System.out.println("=".repeat(60));
		System.out.println("Total files processed: " + totalFiles);
		System.out.println("Successful: " + successfulFiles);
		System.out.println("Failed: " + (totalFiles - successfulFiles));
		System.out.println("Total chunks extracted: " + totalChunks);
		System.out.println("\nChunks by level:");
		for (CobolChunker.ChunkLevel level : CobolChunker.ChunkLevel.values()) {
			int count = chunksByLevel.getOrDefault(level, 0);
			if (count > 0) {
				System.out.println("  " + level + ": " + count);
			}
		}
		System.out.println("\nCombined chunks exported to: " + combinedFile.getName());
	}
	
	/**
	 * Process all .CBL files in a directory
	 */
	private static void processBatch(File directory, File outputDir) throws IOException {
		if (!directory.exists() || !directory.isDirectory()) {
			System.err.println("Error: Not a valid directory: " + directory);
			System.exit(1);
		}
		
		if (outputDir == null) {
			outputDir = new File(directory, "chunks");
		}
		
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		
		File[] files = directory.listFiles((dir, name) -> 
			name.endsWith(".CBL") || name.endsWith(".cbl"));
		
		if (files == null || files.length == 0) {
			System.err.println("No COBOL files found in: " + directory);
			return;
		}
		
		System.out.println("Processing " + files.length + " files from: " + directory);
		System.out.println("Output directory: " + outputDir.getAbsolutePath());
		
		// Create a COBOL project
		StandardCobolProject project = new StandardCobolProject();
		project.setDefaultPreprocessing(true);
		project.addCopybookPath(directory);
		
		// Create chunker
		CobolChunker chunker = new CobolChunker(project);
		
		int processed = 0;
		int failed = 0;
		
		for (File file : files) {
			try {
				System.out.println("Processing: " + file.getName());
				List<Chunk> chunks = chunker.chunk(file,
					CobolChunker.ChunkLevel.PROGRAM,
					CobolChunker.ChunkLevel.DIVISION,
					CobolChunker.ChunkLevel.SECTION,
					CobolChunker.ChunkLevel.PARAGRAPH
				);
				
				File outputFile = new File(outputDir, 
					file.getName().replaceAll("\\.(CBL|cbl)$", ".json"));
				exportToJson(chunks, outputFile);
				
				System.out.println("  ✓ " + chunks.size() + " chunks -> " + outputFile.getName());
				processed++;
				
			} catch (Exception e) {
				System.err.println("  ✗ Error: " + e.getMessage());
				failed++;
			}
		}
		
		System.out.println("\nCompleted: " + processed + " successful, " + failed + " failed");
	}
	
	/**
	 * Print a summary of extracted chunks
	 */
	private static void printChunkSummary(List<Chunk> chunks) {
		System.out.println("\n=== Chunk Summary ===");
		for (Chunk chunk : chunks) {
			System.out.printf("%s: %s (lines %d-%d)\n",
				chunk.level,
				chunk.metadata.get("name"),
				chunk.startLine,
				chunk.endLine
			);
			
			// Print hierarchy
			if (!chunk.children.isEmpty()) {
				System.out.printf("  Contains %d child chunks\n", chunk.children.size());
			}
		}
	}
	
	/**
	 * Export chunks to JSON format for RAG systems
	 */
	private static void exportToJson(List<Chunk> chunks, File outputFile) throws IOException {
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		json.append("  \"chunks\": [\n");
		
		for (int i = 0; i < chunks.size(); i++) {
			Chunk chunk = chunks.get(i);
			json.append("    {\n");
			json.append("      \"chunkId\": \"").append(chunk.chunkId).append("\",\n");
			json.append("      \"level\": \"").append(chunk.level).append("\",\n");
			json.append("      \"text\": ").append(escapeJson(chunk.text)).append(",\n");
			json.append("      \"startLine\": ").append(chunk.startLine).append(",\n");
			json.append("      \"endLine\": ").append(chunk.endLine).append(",\n");
			json.append("      \"metadata\": {\n");
			
			// Add metadata
			boolean first = true;
			for (Map.Entry<String, String> entry : chunk.metadata.entrySet()) {
				if (!first) json.append(",\n");
				json.append("        \"").append(entry.getKey()).append("\": ")
					.append(escapeJson(entry.getValue()));
				first = false;
			}
			json.append("\n      }");
			
			// Add parent reference
			if (chunk.parent != null) {
				json.append(",\n      \"parentChunkId\": \"").append(chunk.parent.chunkId).append("\"");
			}
			
			// Add child references
			if (!chunk.children.isEmpty()) {
				json.append(",\n      \"childChunkIds\": [");
				for (int j = 0; j < chunk.children.size(); j++) {
					if (j > 0) json.append(", ");
					json.append("\"").append(chunk.children.get(j).chunkId).append("\"");
				}
				json.append("]");
			}
			
			json.append("\n    }");
			if (i < chunks.size() - 1) {
				json.append(",");
			}
			json.append("\n");
		}
		
		json.append("  ]\n");
		json.append("}\n");
		
		try (FileWriter writer = new FileWriter(outputFile)) {
			writer.write(json.toString());
		}
	}
	
	/**
	 * Escape string for JSON
	 */
	private static String escapeJson(String str) {
		if (str == null) {
			return "null";
		}
		return "\"" + str.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\n", "\\n")
			.replace("\r", "\\r")
			.replace("\t", "\\t") + "\"";
	}
}

