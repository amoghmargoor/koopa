package koopa.cobol.grammar.dependency;

import static koopa.core.data.tags.SyntacticTag.WORD;

import koopa.cobol.grammar.CobolBaseGrammar;

public abstract class CobolDependencyBaseGrammar extends CobolBaseGrammar {

	@Override
	public String getNamespace() {
		return "cobol-dependency";
	}

}
