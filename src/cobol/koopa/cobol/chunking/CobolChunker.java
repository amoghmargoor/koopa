package koopa.cobol.chunking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import koopa.cobol.CobolProject;
import koopa.cobol.parser.CobolParser;
import koopa.cobol.parser.ParseResults;
import koopa.core.data.Position;
import koopa.core.trees.KoopaTreeBuilder;
import koopa.core.trees.Tree;
import koopa.core.trees.TreeWalker;
import koopa.core.trees.jaxen.Jaxen;

/**
 * COBOL Chunker for RAG (Retrieval-Augmented Generation) services.
 * 
 * This chunker uses the Cobol.kg grammar to parse COBOL files and extract
 * semantic chunks at multiple levels of granularity:
 * 
 * - Program level: Entire program with metadata
 * - Division level: IDENTIFICATION, ENVIRONMENT, DATA, PROCEDURE divisions
 * - Section level: Sections within divisions (e.g., WORKING-STORAGE SECTION)
 * - Paragraph level: Named paragraphs in PROCEDURE DIVISION
 * - Statement level: Individual statements (for fine-grained retrieval)
 * 
 * Each chunk includes:
 * - Text content (the actual COBOL code)
 * - Metadata (program name, division, section, paragraph names, line numbers)
 * - Hierarchical relationships (parent chunks)
 * - Dependencies (COPY statements, CALL statements)
 */
public class CobolChunker {

	private CobolProject project;
	private CobolParser parser;
	
	/**
	 * Chunk granularity levels
	 */
	public enum ChunkLevel {
		PROGRAM,      // Entire program
		DIVISION,     // Divisions (IDENTIFICATION, ENVIRONMENT, DATA, PROCEDURE)
		SECTION,      // Sections (WORKING-STORAGE, FILE SECTION, etc.)
		PARAGRAPH,    // Named paragraphs
		STATEMENT     // Individual statements
	}

	/**
	 * Represents a single chunk of COBOL code
	 */
	public static class Chunk {
		/** The chunk level (PROGRAM, DIVISION, SECTION, etc.) */
		public final ChunkLevel level;
		
		/** The actual COBOL code text */
		public final String text;
		
		/** Metadata about this chunk */
		public final Map<String, String> metadata;
		
		/** Line number where chunk starts */
		public final int startLine;
		
		/** Line number where chunk ends */
		public final int endLine;
		
		/** Parent chunk (for hierarchical relationships) */
		public final Chunk parent;
		
		/** Child chunks */
		public final List<Chunk> children;
		
		/** Unique identifier for this chunk */
		public final String chunkId;
		
		public Chunk(ChunkLevel level, String text, Map<String, String> metadata,
				int startLine, int endLine, Chunk parent, String chunkId) {
			this.level = level;
			this.text = text;
			this.metadata = new HashMap<>(metadata);
			this.startLine = startLine;
			this.endLine = endLine;
			this.parent = parent;
			this.children = new ArrayList<>();
			this.chunkId = chunkId;
			
			if (parent != null) {
				parent.children.add(this);
			}
		}
		
		@Override
		public String toString() {
			return String.format("Chunk[%s:%s lines %d-%d]", 
				level, metadata.get("name"), startLine, endLine);
		}
	}

	public CobolChunker(CobolProject project) {
		this.project = project;
		this.parser = project.createParser();
		this.parser.setBuildTrees(true); // Enable tree building
	}

	/**
	 * Chunk a COBOL file at the specified level(s)
	 * 
	 * @param file The COBOL file to chunk
	 * @param levels The chunk levels to extract (can specify multiple)
	 * @return List of chunks
	 */
	public List<Chunk> chunk(File file, ChunkLevel... levels) throws IOException {
		// Parse the file
		ParseResults results = parser.parse(file);
		
		if (!results.isValidInput()) {
			throw new IOException("Failed to parse COBOL file: " + file.getName() + 
				" - " + results.getParse().getMessages().getErrors());
		}
		
		// Get the parse tree
		KoopaTreeBuilder treeBuilder = 
			results.getParse().getTarget(KoopaTreeBuilder.class);
		
		if (treeBuilder == null || treeBuilder.getTrees().isEmpty()) {
			throw new IOException("No parse tree available for: " + file.getName());
		}
		
		Tree rootTree = treeBuilder.getTree();
		
		// Extract chunks
		List<Chunk> chunks = new ArrayList<>();
		Set<ChunkLevel> levelSet = new java.util.HashSet<>();
		for (ChunkLevel level : levels) {
			levelSet.add(level);
		}
		
		// If no levels specified, use all levels
		if (levelSet.isEmpty()) {
			levelSet.add(ChunkLevel.PROGRAM);
			levelSet.add(ChunkLevel.DIVISION);
			levelSet.add(ChunkLevel.SECTION);
			levelSet.add(ChunkLevel.PARAGRAPH);
		}
		
		// Extract program-level chunk
		if (levelSet.contains(ChunkLevel.PROGRAM)) {
			Chunk programChunk = extractProgramChunk(rootTree, file);
			if (programChunk != null) {
				chunks.add(programChunk);
			}
		}
		
		// Extract division, section, and paragraph chunks
		extractStructuralChunks(rootTree, chunks, levelSet, null);
		
		return chunks;
	}

	/**
	 * Extract program-level chunk
	 */
	private Chunk extractProgramChunk(Tree rootTree, File file) {
		// Find sourceUnit or programDefinition
		Tree programTree = findNode(rootTree, "sourceUnit");
		if (programTree == null) {
			programTree = findNode(rootTree, "programDefinition");
		}
		
		if (programTree == null) {
			return null;
		}
		
		// Extract program name
		String programName = Jaxen.getAllText(programTree, ".//programName");
		if (programName == null || programName.trim().isEmpty()) {
			programName = file.getName().replaceAll("\\.(CBL|CPY)$", "");
		}
		
		// Get text and position
		String text = programTree.getProgramText();
		Position start = programTree.getStartPosition();
		Position end = programTree.getEndPosition();
		
		// Build metadata
		Map<String, String> metadata = new HashMap<>();
		metadata.put("name", programName);
		metadata.put("type", "PROGRAM");
		metadata.put("file", file.getName());
		metadata.put("filePath", file.getAbsolutePath());
		
		// Extract dependencies
		List<String> copyStatements = extractCopyStatements(programTree);
		List<String> callStatements = extractCallStatements(programTree);
		if (!copyStatements.isEmpty()) {
			metadata.put("copyStatements", String.join(", ", copyStatements));
		}
		if (!callStatements.isEmpty()) {
			metadata.put("callStatements", String.join(", ", callStatements));
		}
		
		return new Chunk(ChunkLevel.PROGRAM, text, metadata,
			start != null ? start.getLinenumber() : 0,
			end != null ? end.getLinenumber() : 0,
			null, generateChunkId("program", programName));
	}

	/**
	 * Extract structural chunks (divisions, sections, paragraphs)
	 */
	private void extractStructuralChunks(Tree rootTree, List<Chunk> chunks,
			Set<ChunkLevel> levels, Chunk parent) {
		
		TreeWalker walker = new TreeWalker(rootTree);
		walker.next(); // Skip root
		
		Stack<Chunk> chunkStack = new Stack<>();
		if (parent != null) {
			chunkStack.push(parent);
		}
		
		while (true) {
			Tree tree = walker.next();
			if (tree == null) {
				break;
			}
			
			Chunk chunk = null;
			Chunk currentParent = chunkStack.isEmpty() ? parent : chunkStack.peek();
			
			// Check for divisions
			if (levels.contains(ChunkLevel.DIVISION)) {
				if (tree.isNode("identificationDivision")) {
					chunk = extractDivisionChunk(tree, "IDENTIFICATION", currentParent);
					if (chunk != null) {
						chunks.add(chunk);
						chunkStack.push(chunk);
						walker.skipRemainderOfTree(tree);
						continue;
					}
				} else if (tree.isNode("environmentDivision")) {
					chunk = extractDivisionChunk(tree, "ENVIRONMENT", currentParent);
					if (chunk != null) {
						chunks.add(chunk);
						chunkStack.push(chunk);
						walker.skipRemainderOfTree(tree);
						continue;
					}
				} else if (tree.isNode("dataDivision")) {
					chunk = extractDivisionChunk(tree, "DATA", currentParent);
					if (chunk != null) {
						chunks.add(chunk);
						chunkStack.push(chunk);
						walker.skipRemainderOfTree(tree);
						continue;
					}
				} else if (tree.isNode("procedureDivision")) {
					chunk = extractDivisionChunk(tree, "PROCEDURE", currentParent);
					if (chunk != null) {
						chunks.add(chunk);
						chunkStack.push(chunk);
						walker.skipRemainderOfTree(tree);
						continue;
					}
				}
			}
			
			// Check for sections
			if (levels.contains(ChunkLevel.SECTION)) {
				if (tree.isNode("section") || 
					tree.isNode("workingStorageSection") ||
					tree.isNode("fileSection") ||
					tree.isNode("linkageSection") ||
					tree.isNode("configurationSection") ||
					tree.isNode("inputOutputSection")) {
					
					String sectionName = extractSectionName(tree);
					chunk = extractSectionChunk(tree, sectionName, currentParent);
					if (chunk != null) {
						chunks.add(chunk);
						chunkStack.push(chunk);
						walker.skipRemainderOfTree(tree);
						continue;
					}
				}
			}
			
			// Check for paragraphs
			if (levels.contains(ChunkLevel.PARAGRAPH)) {
				if (tree.isNode("paragraph")) {
					String paragraphName = Jaxen.getAllText(tree, ".//paragraphName");
					if (paragraphName != null && !paragraphName.trim().isEmpty()) {
						chunk = extractParagraphChunk(tree, paragraphName, currentParent);
						if (chunk != null) {
							chunks.add(chunk);
							// Don't push paragraph to stack - they're leaf nodes
							walker.skipRemainderOfTree(tree);
							continue;
						}
					}
				}
			}
		}
	}

	/**
	 * Extract a division chunk
	 */
	private Chunk extractDivisionChunk(Tree tree, String divisionName, Chunk parent) {
		String text = tree.getProgramText();
		Position start = tree.getStartPosition();
		Position end = tree.getEndPosition();
		
		Map<String, String> metadata = new HashMap<>();
		metadata.put("name", divisionName);
		metadata.put("type", "DIVISION");
		
		// Extract program name from parent
		if (parent != null && parent.metadata.containsKey("name")) {
			metadata.put("program", parent.metadata.get("name"));
		}
		
		return new Chunk(ChunkLevel.DIVISION, text, metadata,
			start != null ? start.getLinenumber() : 0,
			end != null ? end.getLinenumber() : 0,
			parent, generateChunkId("division", divisionName));
	}

	/**
	 * Extract a section chunk
	 */
	private Chunk extractSectionChunk(Tree tree, String sectionName, Chunk parent) {
		String text = tree.getProgramText();
		Position start = tree.getStartPosition();
		Position end = tree.getEndPosition();
		
		Map<String, String> metadata = new HashMap<>();
		metadata.put("name", sectionName);
		metadata.put("type", "SECTION");
		
		// Extract division from parent
		if (parent != null) {
			if (parent.metadata.containsKey("name")) {
				metadata.put("division", parent.metadata.get("name"));
			}
			if (parent.metadata.containsKey("program")) {
				metadata.put("program", parent.metadata.get("program"));
			}
		}
		
		return new Chunk(ChunkLevel.SECTION, text, metadata,
			start != null ? start.getLinenumber() : 0,
			end != null ? end.getLinenumber() : 0,
			parent, generateChunkId("section", sectionName));
	}

	/**
	 * Extract a paragraph chunk
	 */
	private Chunk extractParagraphChunk(Tree tree, String paragraphName, Chunk parent) {
		String text = tree.getProgramText();
		Position start = tree.getStartPosition();
		Position end = tree.getEndPosition();
		
		Map<String, String> metadata = new HashMap<>();
		metadata.put("name", paragraphName);
		metadata.put("type", "PARAGRAPH");
		
		// Extract hierarchy from parent chain
		if (parent != null) {
			Chunk current = parent;
			while (current != null) {
				if (current.level == ChunkLevel.DIVISION) {
					metadata.put("division", current.metadata.get("name"));
				} else if (current.level == ChunkLevel.SECTION) {
					metadata.put("section", current.metadata.get("name"));
				}
				if (current.metadata.containsKey("program")) {
					metadata.put("program", current.metadata.get("program"));
				}
				current = current.parent;
			}
		}
		
		return new Chunk(ChunkLevel.PARAGRAPH, text, metadata,
			start != null ? start.getLinenumber() : 0,
			end != null ? end.getLinenumber() : 0,
			parent, generateChunkId("paragraph", paragraphName));
	}

	/**
	 * Extract section name from tree
	 */
	private String extractSectionName(Tree tree) {
		// Try to find sectionName node
		String name = Jaxen.getAllText(tree, ".//sectionName");
		if (name != null && !name.trim().isEmpty()) {
			return name.trim();
		}
		
		// Fall back to node type
		if (tree.isNode("workingStorageSection")) {
			return "WORKING-STORAGE";
		} else if (tree.isNode("fileSection")) {
			return "FILE";
		} else if (tree.isNode("linkageSection")) {
			return "LINKAGE";
		} else if (tree.isNode("configurationSection")) {
			return "CONFIGURATION";
		} else if (tree.isNode("inputOutputSection")) {
			return "INPUT-OUTPUT";
		}
		
		return "UNNAMED-SECTION";
	}

	/**
	 * Extract COPY statements from a tree
	 */
	private List<String> extractCopyStatements(Tree tree) {
		List<String> copies = new ArrayList<>();
		List<?> copyNodes = Jaxen.evaluate(tree, ".//copyStatement");
		if (copyNodes != null) {
			for (Object obj : copyNodes) {
				if (obj instanceof Tree) {
					Tree copyNode = (Tree) obj;
					String textName = Jaxen.getAllText(copyNode, ".//textName");
					if (textName != null && !textName.trim().isEmpty()) {
						copies.add(textName.trim());
					}
				}
			}
		}
		return copies;
	}

	/**
	 * Extract CALL statements from a tree
	 */
	private List<String> extractCallStatements(Tree tree) {
		List<String> calls = new ArrayList<>();
		List<?> callNodes = Jaxen.evaluate(tree, ".//callStatement");
		if (callNodes != null) {
			for (Object obj : callNodes) {
				if (obj instanceof Tree) {
					Tree callNode = (Tree) obj;
					String programName = Jaxen.getAllText(callNode, ".//programName");
					if (programName != null && !programName.trim().isEmpty()) {
						calls.add(programName.trim());
					}
				}
			}
		}
		return calls;
	}

	/**
	 * Find a node with the given name in the tree
	 */
	private Tree findNode(Tree tree, String nodeName) {
		if (tree.isNode(nodeName)) {
			return tree;
		}
		for (Tree child : tree.getChildren()) {
			Tree found = findNode(child, nodeName);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	/**
	 * Generate a unique chunk ID
	 */
	private String generateChunkId(String type, String name) {
		return String.format("%s:%s:%d", type, name, System.currentTimeMillis());
	}
}

