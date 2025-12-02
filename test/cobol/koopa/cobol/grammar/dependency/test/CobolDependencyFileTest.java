package koopa.cobol.grammar.dependency.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import koopa.cobol.grammar.dependency.CobolDependencyGrammar;
import koopa.cobol.parser.ParseResults;
import koopa.core.parsers.ParserCombinator;

/**
 * Test for COBOL Dependency Grammar parsing actual test files
 * 
 * This test verifies that the dependency grammar can parse real COBOL files
 * from the testsuite and extract dependency information.
 */
public class CobolDependencyFileTest {

	@Test
	public void testParseCM101M() throws IOException {
		// Test with CM101M.CBL from the testsuite
		File testFile = new File("testsuite/cobol85/CM101M.CBL");
		assertTrue("Test file should exist", testFile.exists());
		
		// Create a dependency project and parser
		DependencyCobolProject project = createDependencyProject();
		DependencyCobolParser parser = new DependencyCobolParser(project);
		parser.setKeepingTrackOfTokens(true);
		
		// Verify we're using the dependency grammar
		ParserCombinator parserCombinator = project.parserFor(testFile);
		System.out.println("Using parser: " + parserCombinator.getClass().getSimpleName());
		System.out.println("Grammar namespace: " + project.getDependencyGrammar().getNamespace());
		System.out.println("Dependency grammar instance: " + project.getDependencyGrammar().getClass().getSimpleName());
		
		// Parse the file
		ParseResults result = parser.parse(testFile);
		
		// Verify parsing was successful
		assertNotNull("Parse result should not be null", result);
		assertTrue("File should parse successfully", result.isValidInput());
		
		// Print some basic information about the parse
		System.out.println("Parsed file: " + testFile.getName());
		System.out.println("Parse successful: " + result.isValidInput());
		System.out.println("Number of lines: " + result.getNumberOfLines());
		System.out.println("Number of lines with code: " + result.getNumberOfLinesWithCode());
		System.out.println("Parse time: " + result.getTime() + " ms");
	}

	@Test
	public void testParseSimpleCopybook() throws IOException {
		// Test with a simple copybook
		File testFile = new File("testsuite/cobol85/K101A.CPY");
		assertTrue("Test file should exist", testFile.exists());
		
		// Create a dependency project and parser
		DependencyCobolProject project = createDependencyProject();
		DependencyCobolParser parser = new DependencyCobolParser(project);
		parser.setKeepingTrackOfTokens(true);
		
		// Parse the file
		ParseResults result = parser.parse(testFile);
		
		// Verify parsing was successful
		assertNotNull("Parse result should not be null", result);
		assertTrue("File should parse successfully", result.isValidInput());
		
		// Print some basic information about the parse
		System.out.println("Parsed file: " + testFile.getName());
		System.out.println("Parse successful: " + result.isValidInput());
		System.out.println("Number of lines: " + result.getNumberOfLines());
		System.out.println("Number of lines with code: " + result.getNumberOfLinesWithCode());
		System.out.println("Parse time: " + result.getTime() + " ms");
	}

	@Test
	public void testParseProgramWithCalls() throws IOException {
		// Test with a program that has CALL statements
		File testFile = new File("testsuite/cobol85/IC101A.CBL");
		assertTrue("Test file should exist", testFile.exists());
		
		// Create a dependency project and parser
		DependencyCobolProject project = createDependencyProject();
		DependencyCobolParser parser = new DependencyCobolParser(project);
		parser.setKeepingTrackOfTokens(true);
		
		// Parse the file
		ParseResults result = parser.parse(testFile);
		
		// Verify parsing was successful
		assertNotNull("Parse result should not be null", result);
		assertTrue("File should parse successfully", result.isValidInput());
		
		// Print some basic information about the parse
		System.out.println("Parsed file: " + testFile.getName());
		System.out.println("Parse successful: " + result.isValidInput());
		System.out.println("Number of lines: " + result.getNumberOfLines());
		System.out.println("Number of lines with code: " + result.getNumberOfLinesWithCode());
		System.out.println("Parse time: " + result.getTime() + " ms");
	}

	@Test
	public void testDependencyGrammarInstantiation() {
		// Test that the dependency grammar can be instantiated
		CobolDependencyGrammar grammar = CobolDependencyGrammar.instance();
		assertNotNull("Dependency grammar should not be null", grammar);
		
		// Test basic grammar functionality
		assertNotNull("Grammar should have dependencyAnalysis rule", grammar.dependencyAnalysis());
		assertNotNull("Grammar should have dependencyStatement rule", grammar.dependencyStatement());
		assertNotNull("Grammar should have callStatement rule", grammar.callStatement());
		assertNotNull("Grammar should have skipStatement rule", grammar.skipStatement());
	}

	/**
	 * Create a COBOL project configured for dependency analysis
	 */
	private DependencyCobolProject createDependencyProject() {
		DependencyCobolProject project = new DependencyCobolProject();
		
		// Enable preprocessing to handle COPY statements
		project.setDefaultPreprocessing(true);
		
		// Add copybook path
		project.addCopybookPath(new File("testsuite/cobol85/"));
		
		return project;
	}
}
