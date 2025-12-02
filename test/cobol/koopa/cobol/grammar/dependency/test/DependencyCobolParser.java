package koopa.cobol.grammar.dependency.test;

import java.io.File;
import java.io.IOException;

import koopa.cobol.parser.CobolParser;
import koopa.cobol.parser.ParseResults;
import koopa.cobol.grammar.dependency.CobolDependencyGrammar;

/**
 * A specialized COBOL parser that uses the dependency grammar for parsing.
 * This parser ensures that dependency analysis is performed using the 
 * CobolDependencyGrammar instead of the main CobolGrammar.
 */
public class DependencyCobolParser extends CobolParser {

    private DependencyCobolProject dependencyProject;

    /**
     * Create a dependency parser with the given dependency project
     */
    public DependencyCobolParser(DependencyCobolProject project) {
        super();
        this.dependencyProject = project;
        setProject(project);
    }

    @Override
    public ParseResults parse(File file) throws IOException {
        if (dependencyProject == null) {
            throw new IllegalStateException("DependencyCobolParser requires a DependencyCobolProject");
        }

        System.out.println("DependencyCobolParser parsing: " + file.getName());
        System.out.println("Using dependency grammar: " + dependencyProject.getDependencyGrammar().getClass().getSimpleName());
        
        // Use the parent's parsing logic but with our dependency project
        return super.parse(file);
    }

    @Override
    public ParseResults parse(File file, java.io.Reader reader) throws IOException {
        if (dependencyProject == null) {
            throw new IllegalStateException("DependencyCobolParser requires a DependencyCobolProject");
        }

        System.out.println("DependencyCobolParser parsing: " + file.getName() + " (from Reader)");
        System.out.println("Using dependency grammar: " + dependencyProject.getDependencyGrammar().getClass().getSimpleName());
        
        // Use the parent's parsing logic but with our dependency project
        return super.parse(file, reader);
    }

    /**
     * Get the dependency grammar being used by this parser
     */
    public CobolDependencyGrammar getDependencyGrammar() {
        return dependencyProject.getDependencyGrammar();
    }

    /**
     * Get the dependency project being used by this parser
     */
    public DependencyCobolProject getDependencyProject() {
        return dependencyProject;
    }
}
