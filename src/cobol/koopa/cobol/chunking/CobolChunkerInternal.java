package koopa.cobol.chunking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
 * semantic chunks at multiple levels of granularity using a hierarchical
 * queue-based algorithm similar to tree-sitter's query.captures() approach.
 * 
 * <h2>Algorithm Overview</h2>
 * 
 * <h3>Query-Based Capture Phase</h3>
 * The algorithm uses XPath queries to find top-level semantic nodes:
 * <ul>
 *   <li>programDefinition → PROGRAM level</li>
 *   <li>identificationDivision, environmentDivision, dataDivision, procedureDivision → DIVISION level</li>
 *   <li>section, workingStorageSection, fileSection, etc. → SECTION level</li>
 *   <li>paragraph → PARAGRAPH level</li>
 *   <li>statement (callStatement, ifStatement, etc.) → STATEMENT level</li>
 * </ul>
 * 
 * <h3>Queue-Based Hierarchical Processing</h3>
 * <ol>
 *   <li>Initialize queue with captured nodes from XPath queries</li>
 *   <li>While queue is not empty:
 *     <ol type="a">
 *       <li>Pop current node</li>
 *       <li>Get node text length</li>
 *       <li>If length &lt; minBlockChars: skip (ignore)</li>
 *       <li>If length &gt; maxBlockChars * TOLERANCE (3450):
 *         <ul>
 *           <li>If node is PARAGRAPH: combine its children sequentially until maxBlockChars (generic approach)</li>
 *           <li>If node has children (and not paragraph): add children to queue (hierarchical breakdown)</li>
 *           <li>If node is leaf: chunk by fixed line count</li>
 *         </ul>
 *       </li>
 *       <li>If length is in valid range (min-max): create CodeBlock</li>
 *     </ol>
 *   </li>
 * </ol>
 * 
 * <h3>Paragraph Children Combining Algorithm</h3>
 * When a paragraph exceeds maxBlockChars:
 * <ol>
 *   <li>Get all children of the paragraph node</li>
 *   <li>Group children sequentially:
 *     <ul>
 *       <li>Start with first child</li>
 *       <li>Keep adding children until combined text length &gt; maxBlockChars</li>
 *       <li>Create CodeBlock for this group</li>
 *       <li>Continue with next child group</li>
 *     </ul>
 *   </li>
 * </ol>
 * 
 * <h2>Chunk Levels</h2>
 * - Program level: Entire program with metadata
 * - Division level: IDENTIFICATION, ENVIRONMENT, DATA, PROCEDURE divisions
 * - Section level: Sections within divisions (e.g., WORKING-STORAGE SECTION)
 * - Paragraph level: Named paragraphs in PROCEDURE DIVISION
 * - Statement level: Individual statements (for fine-grained retrieval)
 * 
 * <h2>Output Format</h2>
 * Each chunk is returned as a CodeBlock matching the TypeScript interface:
 * <ul>
 *   <li>file_path: Absolute path to the COBOL file</li>
 *   <li>identifier: Node identifier (program name, paragraph name, etc.) or null</li>
 *   <li>type: Node type (PROGRAM, DIVISION, SECTION, PARAGRAPH, STATEMENT)</li>
 *   <li>start_line: Starting line number</li>
 *   <li>end_line: Ending line number</li>
 *   <li>content: The actual COBOL code text</li>
 * </ul>
 */
public class CobolChunkerInternal {

	/** Default minimum chunk size (smaller nodes are ignored) */
	private static final int DEFAULT_MIN_BLOCK_CHARS = 50;
	
	/** Default maximum preferred chunk size */
	private static final int DEFAULT_MAX_BLOCK_CHARS = 3000;
	
	/** Tolerance factor for maximum size (15% = 3450 chars max) */
	private static final double MAX_CHARS_TOLERANCE_FACTOR = 1.15;
	
	/** Lines per chunk when splitting oversized leaf nodes */
	private static final int LEAF_CHUNK_LINES = 50;

	private CobolProject project;
	private CobolParser parser;
	
	/**
	 * Chunk granularity levels
	 */
	private enum ChunkLevel {
		PROGRAM,      // Entire program
		DIVISION,     // Divisions (IDENTIFICATION, ENVIRONMENT, DATA, PROCEDURE)
		SECTION,      // Sections (WORKING-STORAGE, FILE SECTION, etc.)
		PARAGRAPH,    // Named paragraphs
		STATEMENT     // Individual statements
	}

	/**
	 * Represents a single chunk of COBOL code (legacy, for backward compatibility).
	 * @deprecated Use CodeBlock instead
	 */
	@Deprecated
	private static class Chunk {
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

	/**
	 * Represents a single code block matching the TypeScript CodeBlock interface.
	 * This is the output format for REST API clients.
	 */
	public static class CodeBlock {
		/** Absolute file path */
		public final String file_path;
		
		/** Node identifier (program name, paragraph name, etc.) or null */
		public final String identifier;
		
		/** Node type (PROGRAM, DIVISION, SECTION, PARAGRAPH, STATEMENT) */
		public final String type;
		
		/** Starting line number (1-based) */
		public final int start_line;
		
		/** Ending line number (1-based) */
		public final int end_line;
		
		/** The actual COBOL code content */
		public final String content;
		
		public CodeBlock(String file_path, String identifier, String type,
				int start_line, int end_line, String content) {
			this.file_path = file_path;
			this.identifier = identifier;
			this.type = type;
			this.start_line = start_line;
			this.end_line = end_line;
			this.content = content;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null || getClass() != obj.getClass()) return false;
			CodeBlock codeBlock = (CodeBlock) obj;
			return start_line == codeBlock.start_line &&
					end_line == codeBlock.end_line &&
					java.util.Objects.equals(file_path, codeBlock.file_path) &&
					java.util.Objects.equals(identifier, codeBlock.identifier) &&
					java.util.Objects.equals(type, codeBlock.type) &&
					java.util.Objects.equals(content, codeBlock.content);
		}
		
		@Override
		public int hashCode() {
			return java.util.Objects.hash(file_path, identifier, type, start_line, end_line, content);
		}
		
		@Override
		public String toString() {
			return String.format("CodeBlock[%s:%s lines %d-%d]", 
				type, identifier != null ? identifier : "null", start_line, end_line);
		}
	}

	public CobolChunkerInternal(CobolProject project) {
		this.project = project;
		this.parser = project.createParser();
		this.parser.setBuildTrees(true); // Enable tree building
	}

	/**
	 * Chunk a COBOL file at the specified level(s)
	 * 
	 * @param file The COBOL file to chunk
	 * @param minBlockChars Minimum chunk size (use default if <= 0)
	 * @param maxBlockChars Maximum chunk size (use default if <= 0)
	 * @param levels The chunk levels to extract (can specify multiple)
	 * @return List of CodeBlocks matching TypeScript interface
	 */
	public List<CodeBlock> chunk(File file, int minBlockChars, int maxBlockChars, ChunkLevel... levels) throws IOException {
		// Use defaults if parameters not provided
		if (minBlockChars <= 0) {
			minBlockChars = DEFAULT_MIN_BLOCK_CHARS;
		}
		if (maxBlockChars <= 0) {
			maxBlockChars = DEFAULT_MAX_BLOCK_CHARS;
		}
		
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
		
		// Determine which levels to extract
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
		
		// Capture top-level nodes using XPath queries
		List<Tree> capturedNodes = captureTopLevelNodes(rootTree, levelSet);
		
		// Process hierarchically
		return processHierarchicalChunking(capturedNodes, file, minBlockChars, maxBlockChars, levelSet);
	}
	
	/**
	 * Capture top-level nodes using XPath queries based on requested chunk levels.
	 * Similar to tree-sitter's query.captures() approach.
	 */
	private List<Tree> captureTopLevelNodes(Tree rootTree, Set<ChunkLevel> levels) {
		List<Tree> capturedNodes = new ArrayList<>();
		
		if (levels.contains(ChunkLevel.PROGRAM)) {
			List<?> programNodes = Jaxen.evaluate(rootTree, ".//programDefinition");
			if (programNodes != null) {
				for (Object obj : programNodes) {
					if (obj instanceof Tree) {
						capturedNodes.add((Tree) obj);
					}
				}
			}
		}
		
		if (levels.contains(ChunkLevel.DIVISION)) {
			String divisionQuery = ".//identificationDivision | .//environmentDivision | .//dataDivision | .//procedureDivision";
			List<?> divisionNodes = Jaxen.evaluate(rootTree, divisionQuery);
			if (divisionNodes != null) {
				for (Object obj : divisionNodes) {
					if (obj instanceof Tree) {
						capturedNodes.add((Tree) obj);
					}
				}
			}
		}
		
		if (levels.contains(ChunkLevel.SECTION)) {
			String sectionQuery = ".//section | .//workingStorageSection | .//fileSection | .//linkageSection | .//configurationSection | .//inputOutputSection";
			List<?> sectionNodes = Jaxen.evaluate(rootTree, sectionQuery);
			if (sectionNodes != null) {
				for (Object obj : sectionNodes) {
					if (obj instanceof Tree) {
						capturedNodes.add((Tree) obj);
					}
				}
			}
		}
		
		if (levels.contains(ChunkLevel.PARAGRAPH)) {
			List<?> paragraphNodes = Jaxen.evaluate(rootTree, ".//paragraph");
			if (paragraphNodes != null) {
				for (Object obj : paragraphNodes) {
					if (obj instanceof Tree) {
						capturedNodes.add((Tree) obj);
					}
				}
			}
		}
		
		if (levels.contains(ChunkLevel.STATEMENT)) {
			String statementQuery = ".//callStatement | .//ifStatement | .//performStatement | .//moveStatement | " +
				".//readStatement | .//writeStatement | .//computeStatement | .//evaluateStatement | " +
				".//addStatement | .//subtractStatement | .//multiplyStatement | .//divideStatement | " +
				".//acceptStatement | .//displayStatement | .//openStatement | .//closeStatement | " +
				".//deleteStatement | .//rewriteStatement | .//startStatement | .//stopStatement | " +
				".//gobackStatement | .//exitStatement | .//returnStatement | .//stringStatement | " +
				".//unstringStatement | .//inspectStatement | .//searchStatement | .//sortStatement | " +
				".//mergeStatement | .//releaseStatement | .//returnStatement";
			List<?> statementNodes = Jaxen.evaluate(rootTree, statementQuery);
			if (statementNodes != null) {
				for (Object obj : statementNodes) {
					if (obj instanceof Tree) {
						capturedNodes.add((Tree) obj);
					}
				}
			}
		}
		
		return capturedNodes;
	}
	
	/**
	 * Process captured nodes hierarchically using queue-based algorithm.
	 */
	private List<CodeBlock> processHierarchicalChunking(List<Tree> capturedNodes, File file,
			int minBlockChars, int maxBlockChars, Set<ChunkLevel> levels) {
		
		List<CodeBlock> codeBlocks = new ArrayList<>();
		Queue<Tree> queue = new LinkedList<>(capturedNodes);
		double maxTolerance = maxBlockChars * MAX_CHARS_TOLERANCE_FACTOR;
		
		while (!queue.isEmpty()) {
			Tree currentNode = queue.poll();
			if (currentNode == null) {
				continue;
			}

			String nodeText = currentNode.getProgramTextWithinBounds();
			int textLength = nodeText.length();
			
			// Skip nodes that are too small
			if (textLength < minBlockChars) {
				continue;
			}
			
			// Check if node exceeds maximum size
			if (textLength > maxTolerance) {
				// Special handling for paragraphs: combine children
				if (currentNode.isNode("paragraph")) {
					String paragraphName = extractNodeIdentifier(currentNode, "paragraph");
					List<CodeBlock> combinedBlocks = combineNodeChildren(
						currentNode, file, "PARAGRAPH", paragraphName, maxBlockChars);
					codeBlocks.addAll(combinedBlocks);
				} else if (currentNode.getChildCount() > 0) {
					// Has children: add them to queue for hierarchical breakdown
					for (Tree child : currentNode.getChildren()) {
						if (child != null && child.isNode()) {
							queue.offer(child);
						}
					}
				} else {
					// Leaf node: chunk by lines
					String nodeType = getNodeType(currentNode);
					String identifier = extractNodeIdentifier(currentNode, nodeType);
					List<CodeBlock> lineChunks = chunkLeafNodeByLines(
						currentNode, file, nodeType, identifier, maxBlockChars);
					codeBlocks.addAll(lineChunks);
				}
			} else {
				// Perfect size: create CodeBlock
				String nodeType = getNodeType(currentNode);
				String identifier = extractNodeIdentifier(currentNode, nodeType);
				CodeBlock block = createCodeBlockFromNode(currentNode, file, nodeType, identifier);
				if (block != null) {
					codeBlocks.add(block);
				}
			}
		}
		
		return codeBlocks;
	}
	
	/**
	 * Combine children of a node sequentially until reaching maxBlockChars.
	 * Used for paragraphs that exceed the maximum size.
	 */
	private List<CodeBlock> combineNodeChildren(Tree node, File file, String nodeType,
			String identifier, int maxBlockChars) {
		
		List<CodeBlock> codeBlocks = new ArrayList<>();
		List<Tree> children = new ArrayList<>();
		
		// Collect all child nodes
		for (Tree child : node.getChildren()) {
			if (child != null && child.isNode()) {
				children.add(child);
			}
		}
		
		if (children.isEmpty()) {
			// No children, fall back to line chunking
			return chunkLeafNodeByLines(node, file, nodeType, identifier, maxBlockChars);
		}
		
		// Group children sequentially
		List<Tree> currentGroup = new ArrayList<>();
		StringBuilder currentText = new StringBuilder();
		Position groupStart = null;
		Position groupEnd = null;
		
		for (Tree child : children) {
			String childText = child.getProgramTextWithinBounds();
			Position childStart = child.getStartPosition();
			Position childEnd = child.getEndPosition();
			
			// Check if adding this child would exceed maxBlockChars
			if (currentText.length() + childText.length() > maxBlockChars && !currentGroup.isEmpty()) {
				// Create CodeBlock for current group
				CodeBlock block = new CodeBlock(
					file.getAbsolutePath(),
					identifier,
					nodeType,
					groupStart != null ? groupStart.getLinenumber() : 0,
					groupEnd != null ? groupEnd.getLinenumber() : 0,
					currentText.toString()
				);
				codeBlocks.add(block);
				
				// Start new group
				currentGroup.clear();
				currentText = new StringBuilder();
				groupStart = null;
				groupEnd = null;
			}
			
			// Add child to current group
			currentGroup.add(child);
			currentText.append(childText);
			if (groupStart == null && childStart != null) {
				groupStart = childStart;
			}
			if (childEnd != null) {
				groupEnd = childEnd;
			}
		}
		
		// Add remaining group if any
		if (!currentGroup.isEmpty()) {
			CodeBlock block = new CodeBlock(
				file.getAbsolutePath(),
				identifier,
				nodeType,
				groupStart != null ? groupStart.getLinenumber() : 0,
				groupEnd != null ? groupEnd.getLinenumber() : 0,
				currentText.toString()
			);
			codeBlocks.add(block);
		}
		
		return codeBlocks;
	}
	
	/**
	 * Chunk a leaf node by fixed line count when it exceeds maximum size.
	 */
	private List<CodeBlock> chunkLeafNodeByLines(Tree node, File file, String nodeType,
			String identifier, int maxBlockChars) {

		List<CodeBlock> codeBlocks = new ArrayList<>();
		String text = node.getProgramTextWithinBounds();
		String[] lines = text.split("\n", -1);
		
		if (lines.length == 0) {
			return codeBlocks;
		}
		
		Position nodeStart = node.getStartPosition();
		int startLine = nodeStart != null ? nodeStart.getLinenumber() : 0;
		
		// Group lines into chunks of LEAF_CHUNK_LINES
		for (int i = 0; i < lines.length; i += LEAF_CHUNK_LINES) {
			int endIndex = Math.min(i + LEAF_CHUNK_LINES, lines.length);
			StringBuilder chunkText = new StringBuilder();
			
			for (int j = i; j < endIndex; j++) {
				chunkText.append(lines[j]);
				if (j < endIndex - 1) {
					chunkText.append("\n");
				}
			}
			
			int chunkStartLine = startLine + i;
			int chunkEndLine = startLine + endIndex - 1;
			
			CodeBlock block = new CodeBlock(
				file.getAbsolutePath(),
				identifier,
				nodeType,
				chunkStartLine,
				chunkEndLine,
				chunkText.toString()
			);
			codeBlocks.add(block);
		}
		
		return codeBlocks;
	}
	
	/**
	 * Create a CodeBlock from a Tree node.
	 */
	private CodeBlock createCodeBlockFromNode(Tree node, File file, String nodeType, String identifier) {
		String text = node.getProgramTextWithinBounds();
		Position start = node.getStartPosition();
		Position end = node.getEndPosition();
		
		return new CodeBlock(
			file.getAbsolutePath(),
			identifier,
			nodeType,
			start != null ? start.getLinenumber() : 0,
			end != null ? end.getLinenumber() : 0,
			text
		);
	}
	
	/**
	 * Extract node identifier (program name, paragraph name, etc.).
	 * Returns null if no identifier found.
	 */
	private String extractNodeIdentifier(Tree node, String nodeType) {
		if ("PROGRAM".equals(nodeType) || "programDefinition".equals(node.getName())) {
			String programName = Jaxen.getAllText(node, ".//programName");
			if (programName != null && !programName.trim().isEmpty()) {
				return programName.trim();
			}
		} else if ("PARAGRAPH".equals(nodeType) || "paragraph".equals(node.getName())) {
			String paragraphName = Jaxen.getAllText(node, ".//paragraphName");
			if (paragraphName != null && !paragraphName.trim().isEmpty()) {
				return paragraphName.trim();
			}
		} else if ("SECTION".equals(nodeType)) {
			String sectionName = Jaxen.getAllText(node, ".//sectionName");
			if (sectionName != null && !sectionName.trim().isEmpty()) {
				return sectionName.trim();
			}
			// Fall back to node type
			if (node.isNode("workingStorageSection")) {
				return "WORKING-STORAGE";
			} else if (node.isNode("fileSection")) {
				return "FILE";
			} else if (node.isNode("linkageSection")) {
				return "LINKAGE";
			} else if (node.isNode("configurationSection")) {
				return "CONFIGURATION";
			} else if (node.isNode("inputOutputSection")) {
				return "INPUT-OUTPUT";
			}
		} else if ("DIVISION".equals(nodeType)) {
			if (node.isNode("identificationDivision")) {
				return "IDENTIFICATION";
			} else if (node.isNode("environmentDivision")) {
				return "ENVIRONMENT";
			} else if (node.isNode("dataDivision")) {
				return "DATA";
			} else if (node.isNode("procedureDivision")) {
				return "PROCEDURE";
			}
		}
		
		return null;
	}
	
	/**
	 * Get node type string from Tree node.
	 */
	private String getNodeType(Tree node) {
		String nodeName = node.getName();
		if (nodeName == null) {
			return "UNKNOWN";
		}
		
		if (nodeName.equals("programDefinition")) {
			return "PROGRAM";
		} else if (nodeName.equals("identificationDivision") || 
				   nodeName.equals("environmentDivision") ||
				   nodeName.equals("dataDivision") ||
				   nodeName.equals("procedureDivision")) {
			return "DIVISION";
		} else if (nodeName.equals("section") ||
				   nodeName.equals("workingStorageSection") ||
				   nodeName.equals("fileSection") ||
				   nodeName.equals("linkageSection") ||
				   nodeName.equals("configurationSection") ||
				   nodeName.equals("inputOutputSection")) {
			return "SECTION";
		} else if (nodeName.equals("paragraph")) {
			return "PARAGRAPH";
		} else if (nodeName.endsWith("Statement")) {
			return "STATEMENT";
		}
		
		return nodeName.toUpperCase();
	}
	
	/**
	 * Legacy chunk method for backward compatibility.
	 * @deprecated Use chunk(File, int, int, ChunkLevel...) instead
	 */
	@Deprecated
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
		String text = programTree.getProgramTextWithinBounds();
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
		String text = tree.getProgramTextWithinBounds();
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
		String text = tree.getProgramTextWithinBounds();
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
		String text = tree.getProgramTextWithinBounds();
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

