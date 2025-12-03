package koopa.cobol.grammar.dependency.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import koopa.cobol.parser.CobolParser;
import koopa.cobol.parser.ParseResults;
import koopa.core.parsers.Messages;
import koopa.core.parsers.Parse;
import koopa.core.parsers.ParserCombinator;
import koopa.core.util.Tuple;
import koopa.core.data.Token;

/**
 * Debug test to understand parser behavior
 */
public class DebugDependencyParsingTest {

	@Test
	public void debugParseIC101A() throws IOException {
		File testFile = new File("testsuite/cobol85/IC101A.CBL");
		System.out.println("========================================");
		System.out.println("DEBUGGING PARSER EXECUTION");
		System.out.println("========================================");
		System.out.println("Test file: " + testFile.getAbsolutePath());
		System.out.println("File exists: " + testFile.exists());
		System.out.println();
		
		// Create a dependency project and parser
		DependencyCobolProject project = new DependencyCobolProject();
		project.setDefaultPreprocessing(true);
		project.addCopybookPath(new File("testsuite/cobol85/"));
		
		// Create parser
		CobolParser parser = project.createParser();
		parser.setKeepingTrackOfTokens(true);
		
		// Get the parse setup to inspect
		Parse parse = parser.getParseSetup(testFile);
		
		// Get the parser combinator being used
		ParserCombinator parserCombinator = project.parserFor(testFile);
		
		System.out.println("========================================");
		System.out.println("PARSER SETUP");
		System.out.println("========================================");
		System.out.println("Parser class: " + parser.getClass().getName());
		System.out.println("Parser combinator: " + parserCombinator.getClass().getSimpleName());
		System.out.println("Grammar namespace: " + project.getDependencyGrammar().getNamespace());
		System.out.println();
		
		// Parse the file
		System.out.println("========================================");
		System.out.println("STARTING PARSE");
		System.out.println("========================================");
		ParseResults result = parser.parse(testFile, parse);
		
		// Print results
		System.out.println();
		System.out.println("========================================");
		System.out.println("PARSE RESULTS");
		System.out.println("========================================");
		System.out.println("Valid input: " + result.isValidInput());
		System.out.println("Number of lines: " + result.getNumberOfLines());
		System.out.println("Number of lines with code: " + result.getNumberOfLinesWithCode());
		System.out.println("Parse time: " + result.getTime() + " ms");
		
		// Get messages
		Parse resultParse = result.getParse();
		if (resultParse != null) {
			Messages messages = resultParse.getMessages();
			
			System.out.println();
			System.out.println("========================================");
			System.out.println("PARSE MESSAGES");
			System.out.println("========================================");
			System.out.println("Error count: " + messages.getErrorCount());
			System.out.println("Warning count: " + messages.getWarningCount());
			
			if (messages.hasErrors()) {
				System.out.println();
				System.out.println("ERRORS:");
				for (Tuple<Token, String> error : messages.getErrors()) {
					Token token = error.getFirst();
					String message = error.getSecond();
					if (token != null) {
						System.out.println("  At " + token.getStart() + ": " + message);
						System.out.println("  Token: " + token);
					} else {
						System.out.println("  " + message);
					}
				}
			}
			
			if (messages.hasWarnings()) {
				System.out.println();
				System.out.println("WARNINGS:");
				for (Tuple<Token, String> warning : messages.getWarnings()) {
					Token token = warning.getFirst();
					String message = warning.getSecond();
					if (token != null) {
						System.out.println("  At " + token.getStart() + ": " + message);
					} else {
						System.out.println("  " + message);
					}
				}
			}
			
			// Print final frame information if available
			if (resultParse.getFinalFrame() != null) {
				System.out.println();
				System.out.println("========================================");
				System.out.println("FINAL PARSE FRAME");
				System.out.println("========================================");
				System.out.println("Last successful match: " + resultParse.getFinalFrame().toTrace());
			}
			
			if (resultParse.getFinalPosition() != null) {
				System.out.println("Final position: " + resultParse.getFinalPosition());
			}
		}
		
		System.out.println();
		System.out.println("========================================");
		System.out.println("END OF DEBUG OUTPUT");
		System.out.println("========================================");
		
		assertNotNull("Parse result should not be null", result);
	}
}

