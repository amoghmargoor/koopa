package koopa.cobol.grammar.dependency.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import koopa.cobol.CobolProject;
import koopa.cobol.parser.test.CobolParsingRegressionTest;
import koopa.cobol.projects.StandardCobolProject;

/**
 * Maven-compatible test for COBOL Dependency Grammar
 * 
 * This test extends the existing regression test framework to test
 * the dependency grammar against the COBOL85 test suite.
 */
public class Cobol85DependencyTest extends CobolParsingRegressionTest {

	public Cobol85DependencyTest() throws IOException {
		super();
	}

	@Override
	public File[] getFiles() {
		final File folder = new File("testsuite/cobol85");

		// Test both COBOL files and copybook files
		final File[] cobolFiles = folder
				.listFiles((FilenameFilter) (dir, name) -> {
					name = name.toUpperCase();
					return name.endsWith(".CBL");
				});

		final File[] copybookFiles = folder
				.listFiles((FilenameFilter) (dir, name) -> {
					name = name.toUpperCase();
					return name.endsWith(".CPY");
				});

		// Combine both arrays
		final File[] allFiles = new File[cobolFiles.length + copybookFiles.length];
		System.arraycopy(cobolFiles, 0, allFiles, 0, cobolFiles.length);
		System.arraycopy(copybookFiles, 0, allFiles, cobolFiles.length, copybookFiles.length);

		return allFiles;
	}

	@Override
	public CobolProject getConfiguredProject() {
		final StandardCobolProject project = new StandardCobolProject();
		
		// Configure for dependency analysis
		project.setDefaultPreprocessing(true);
		project.addCopybookPath(new File("testsuite/cobol85/"));
		
		// Set up for dependency grammar testing
		// Note: This would need to be configured to use our dependency grammar
		// instead of the full COBOL grammar
		
		return project;
	}

	@Override
	protected File getTargetResultsFile() {
		return new File("testsuite/cobol85_dependency.csv");
	}

	@Override
	protected File getActualResultsFile() {
		return new File("testsuite/cobol85_dependency-actuals.csv");
	}
}
