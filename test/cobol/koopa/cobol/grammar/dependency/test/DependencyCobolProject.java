package koopa.cobol.grammar.dependency.test;

import java.io.File;

import koopa.cobol.CobolFiles;
import koopa.cobol.grammar.dependency.CobolDependencyGrammar;
import koopa.cobol.grammar.CobolGrammar;
import koopa.cobol.grammar.preprocessing.CobolPreprocessingGrammar;
import koopa.cobol.parser.CobolParser;
import koopa.cobol.parser.dependency.CobolDependencyParser;
import koopa.cobol.projects.StandardCobolProject;
import koopa.core.parsers.ParserCombinator;

/**
 * A custom COBOL project that uses the dependency grammar instead of the main grammar.
 * This allows us to test the dependency grammar specifically.
 */
public class DependencyCobolProject extends StandardCobolProject {

    public final CobolDependencyGrammar dependencyGrammar = CobolDependencyGrammar.instance();

    @Override
    public ParserCombinator parserFor(File file) {
        // Use the dependency grammar for parsing, but handle different file types
        final boolean isCopybook = CobolFiles.isCopybook(file);
        
        if (isCopybook) {
            // For copybooks, use the main grammar's copybook parser since 
            // dependency grammar doesn't have a copybook parser yet
            System.out.println("Using main grammar for copybook: " + file.getName());
            return super.parserFor(file);
        } else {
            // For COBOL files, use the dependency grammar
            System.out.println("Using dependency grammar for COBOL file: " + file.getName());
            return dependencyGrammar.dependencyAnalysis();
        }
    }

    @Override
    public CobolGrammar getGrammar() {
        // Return the main grammar for interface compatibility
        // The key is that parserFor() uses the dependency grammar
        return super.getGrammar();
    }

    @Override
    public CobolPreprocessingGrammar getPreprocessingGrammar() {
        return dependencyGrammar;
    }

    @Override
    public CobolParser createParser() {
        CobolDependencyParser parser = new CobolDependencyParser();
        parser.setProject(this);
        return parser;
    }

    /**
     * Get the actual dependency grammar being used for parsing
     */
    public CobolDependencyGrammar getDependencyGrammar() {
        return dependencyGrammar;
    }
}
