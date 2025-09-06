package koopa.cobol.grammar.dependency;

import koopa.core.data.markers.Start;
import koopa.core.parsers.ParserCombinator;
import koopa.core.parsers.FutureParser;

import static koopa.core.parsers.combinators.Opt.NOSKIP;
import static koopa.core.grammars.combinators.Scoped.Visibility.PUBLIC;
import static koopa.core.grammars.combinators.Scoped.Visibility.PRIVATE;
import static koopa.core.grammars.combinators.Scoped.Visibility.HIDING;


/**
 * <b>This is generated code.<b>
 * <p>
 * @see <code>src/cobol/koopa/cobol/grammar/dependency/CobolDependency.kg</code>
 */
public class CobolDependencyGrammar extends CobolDependencyBaseGrammar {
    private static CobolDependencyGrammar INSTANCE = null;

    protected CobolDependencyGrammar() {
    }
    
    public static CobolDependencyGrammar instance() {
      if (INSTANCE == null)
        INSTANCE = new CobolDependencyGrammar();
        
      return INSTANCE;
    }
    
    // ========================================================
    // dependencyAnalysis
    // ........................................................
    
    private ParserCombinator dependencyAnalysisParser = null;
    
    public final Start dependencyAnalysis = Start.on(getNamespace(), "dependencyAnalysis");
    
    public ParserCombinator dependencyAnalysis() {
      if (dependencyAnalysisParser == null) {
        FutureParser future = scoped("dependencyAnalysis", PUBLIC, true);
        dependencyAnalysisParser = future;
        future.setParser(
          sequence(
            star(
              choice(
                dependencyStatement(),
                skipStatement()
              )
            ),
            optional(
              eof()
            )
          )
        );
      }
    
      return dependencyAnalysisParser;
    }
    
    // ========================================================
    // dependencyStatement
    // ........................................................
    
    private ParserCombinator dependencyStatementParser = null;
    
    public final Start dependencyStatement = Start.on(getNamespace(), "dependencyStatement");
    
    public ParserCombinator dependencyStatement() {
      if (dependencyStatementParser == null) {
        FutureParser future = scoped("dependencyStatement", PUBLIC, true);
        dependencyStatementParser = future;
        future.setParser(
          choice(
            copyStatement(),
            callStatement(),
            programDefinition(),
            functionDefinition()
          )
        );
      }
    
      return dependencyStatementParser;
    }
    
    // ========================================================
    // skipStatement
    // ........................................................
    
    private ParserCombinator skipStatementParser = null;
    
    public final Start skipStatement = Start.on(getNamespace(), "skipStatement");
    
    public ParserCombinator skipStatement() {
      if (skipStatementParser == null) {
        FutureParser future = scoped("skipStatement", PUBLIC, true);
        skipStatementParser = future;
        future.setParser(
          choice(
            anyStatement(),
            anyParagraph(),
            anySection(),
            anyDivision(),
            anyLine()
          )
        );
      }
    
      return skipStatementParser;
    }
    
    // ========================================================
    // copyStatement
    // ........................................................
    
    private ParserCombinator copyStatementParser = null;
    
    public final Start copyStatement = Start.on(getNamespace(), "copyStatement");
    
    public ParserCombinator copyStatement() {
      if (copyStatementParser == null) {
        FutureParser future = scoped("copyStatement", PUBLIC, true);
        copyStatementParser = future;
        future.setParser(
          sequence(
            copyStatementBody(),
            literal(".")
          )
        );
      }
    
      return copyStatementParser;
    }
    
    // ========================================================
    // copyStatementBody
    // ........................................................
    
    private ParserCombinator copyStatementBodyParser = null;
    
    protected final Start copyStatementBody = Start.on(getNamespace(), "copyStatementBody");
    
    protected ParserCombinator copyStatementBody() {
      if (copyStatementBodyParser == null) {
        FutureParser future = scoped("copyStatementBody", PRIVATE, true);
        copyStatementBodyParser = future;
        future.setParser(
          sequence(
            keyword("COPY"),
            textName(),
            optional(
              sequence(
                choice(
                  keyword("OF"),
                  keyword("IN")
                ),
                libraryName()
              )
            ),
            optional(
              sequence(
                keyword("SUPPRESS"),
                optional(
                  keyword("PRINTING")
                )
              )
            ),
            optional(
              copyStatementBody$replacing()
            )
          )
        );
      }
    
      return copyStatementBodyParser;
    }
    
    // ========================================================
    // replacing
    // ........................................................
    
    private ParserCombinator copyStatementBody$replacingParser = null;
    
    public final Start copyStatementBody$replacing = Start.on(getNamespace(), "replacing");
    
    public ParserCombinator copyStatementBody$replacing() {
      if (copyStatementBody$replacingParser == null) {
        FutureParser future = scoped("replacing", PUBLIC, true);
        copyStatementBody$replacingParser = future;
        future.setParser(
          sequence(
            keyword("REPLACING"),
            plus(
              replacementInstruction()
            )
          )
        );
      }
    
      return copyStatementBody$replacingParser;
    }
    
    // ========================================================
    // replacementInstruction
    // ........................................................
    
    private ParserCombinator replacementInstructionParser = null;
    
    public final Start replacementInstruction = Start.on(getNamespace(), "replacementInstruction");
    
    public ParserCombinator replacementInstruction() {
      if (replacementInstructionParser == null) {
        FutureParser future = scoped("replacementInstruction", PUBLIC, true);
        replacementInstructionParser = future;
        future.setParser(
          sequence(
            optional(
              choice(
                replacementInstruction$leading(),
                replacementInstruction$trailing()
              )
            ),
            replacementOperand(),
            keyword("BY"),
            replacementOperand()
          )
        );
      }
    
      return replacementInstructionParser;
    }
    
    // ========================================================
    // leading
    // ........................................................
    
    private ParserCombinator replacementInstruction$leadingParser = null;
    
    public final Start replacementInstruction$leading = Start.on(getNamespace(), "leading");
    
    public ParserCombinator replacementInstruction$leading() {
      if (replacementInstruction$leadingParser == null) {
        FutureParser future = scoped("leading", PUBLIC, true);
        replacementInstruction$leadingParser = future;
        future.setParser(
          keyword("LEADING")
        );
      }
    
      return replacementInstruction$leadingParser;
    }
    
    // ========================================================
    // trailing
    // ........................................................
    
    private ParserCombinator replacementInstruction$trailingParser = null;
    
    public final Start replacementInstruction$trailing = Start.on(getNamespace(), "trailing");
    
    public ParserCombinator replacementInstruction$trailing() {
      if (replacementInstruction$trailingParser == null) {
        FutureParser future = scoped("trailing", PUBLIC, true);
        replacementInstruction$trailingParser = future;
        future.setParser(
          keyword("TRAILING")
        );
      }
    
      return replacementInstruction$trailingParser;
    }
    
    // ========================================================
    // replacementOperand
    // ........................................................
    
    private ParserCombinator replacementOperandParser = null;
    
    public final Start replacementOperand = Start.on(getNamespace(), "replacementOperand");
    
    public ParserCombinator replacementOperand() {
      if (replacementOperandParser == null) {
        FutureParser future = scoped("replacementOperand", PUBLIC, true);
        replacementOperandParser = future;
        future.setParser(
          choice(
            pseudoLiteral(),
            literal(),
            cobolWord()
          )
        );
      }
    
      return replacementOperandParser;
    }
    
    // ========================================================
    // callStatement
    // ........................................................
    
    private ParserCombinator callStatementParser = null;
    
    public final Start callStatement = Start.on(getNamespace(), "callStatement");
    
    public ParserCombinator callStatement() {
      if (callStatementParser == null) {
        FutureParser future = scoped("callStatement", PUBLIC, true);
        callStatementParser = future;
        future.setParser(
          sequence(
            callStatementBody(),
            literal(".")
          )
        );
      }
    
      return callStatementParser;
    }
    
    // ========================================================
    // callStatementBody
    // ........................................................
    
    private ParserCombinator callStatementBodyParser = null;
    
    protected final Start callStatementBody = Start.on(getNamespace(), "callStatementBody");
    
    protected ParserCombinator callStatementBody() {
      if (callStatementBodyParser == null) {
        FutureParser future = scoped("callStatementBody", PRIVATE, true);
        callStatementBodyParser = future;
        future.setParser(
          sequence(
            keyword("CALL"),
            choice(
              literal(),
              cobolWord()
            ),
            optional(
              sequence(
                keyword("USING"),
                plus(
                  choice(
                    callStatementBody$byReference(),
                    callStatementBody$byContent(),
                    callStatementBody$byValue()
                  )
                )
              )
            ),
            optional(
              sequence(
                returning(),
                dataName()
              )
            ),
            optional(
              sequence(
                onOverflow(),
                callStatementBody$onOverflowStatement()
              )
            ),
            optional(
              sequence(
                onException(),
                callStatementBody$onExceptionStatement()
              )
            ),
            optional(
              sequence(
                notOnException(),
                callStatementBody$notOnExceptionStatement()
              )
            ),
            optional(
              callStatementBody$endCall()
            )
          )
        );
      }
    
      return callStatementBodyParser;
    }
    
    // ========================================================
    // byReference
    // ........................................................
    
    private ParserCombinator callStatementBody$byReferenceParser = null;
    
    public final Start callStatementBody$byReference = Start.on(getNamespace(), "byReference");
    
    public ParserCombinator callStatementBody$byReference() {
      if (callStatementBody$byReferenceParser == null) {
        FutureParser future = scoped("byReference", PUBLIC, true);
        callStatementBody$byReferenceParser = future;
        future.setParser(
          sequence(
            optional(
              sequence(
                optional(
                  keyword("BY")
                ),
                keyword("REFERENCE")
              )
            ),
            plus(
              choice(
                modifier(),
                arg()
              )
            )
          )
        );
      }
    
      return callStatementBody$byReferenceParser;
    }
    
    // ========================================================
    // byContent
    // ........................................................
    
    private ParserCombinator callStatementBody$byContentParser = null;
    
    public final Start callStatementBody$byContent = Start.on(getNamespace(), "byContent");
    
    public ParserCombinator callStatementBody$byContent() {
      if (callStatementBody$byContentParser == null) {
        FutureParser future = scoped("byContent", PUBLIC, true);
        callStatementBody$byContentParser = future;
        future.setParser(
          sequence(
            optional(
              keyword("BY")
            ),
            keyword("CONTENT"),
            plus(
              choice(
                modifier(),
                arg()
              )
            )
          )
        );
      }
    
      return callStatementBody$byContentParser;
    }
    
    // ========================================================
    // byValue
    // ........................................................
    
    private ParserCombinator callStatementBody$byValueParser = null;
    
    public final Start callStatementBody$byValue = Start.on(getNamespace(), "byValue");
    
    public ParserCombinator callStatementBody$byValue() {
      if (callStatementBody$byValueParser == null) {
        FutureParser future = scoped("byValue", PUBLIC, true);
        callStatementBody$byValueParser = future;
        future.setParser(
          sequence(
            optional(
              keyword("BY")
            ),
            keyword("VALUE"),
            plus(
              choice(
                modifier(),
                arg()
              )
            )
          )
        );
      }
    
      return callStatementBody$byValueParser;
    }
    
    // ========================================================
    // onOverflowStatement
    // ........................................................
    
    private ParserCombinator callStatementBody$onOverflowStatementParser = null;
    
    public final Start callStatementBody$onOverflowStatement = Start.on(getNamespace(), "onOverflowStatement");
    
    public ParserCombinator callStatementBody$onOverflowStatement() {
      if (callStatementBody$onOverflowStatementParser == null) {
        FutureParser future = scoped("onOverflowStatement", PUBLIC, true);
        callStatementBody$onOverflowStatementParser = future;
        future.setParser(
          star(
            statement()
          )
        );
      }
    
      return callStatementBody$onOverflowStatementParser;
    }
    
    // ========================================================
    // onExceptionStatement
    // ........................................................
    
    private ParserCombinator callStatementBody$onExceptionStatementParser = null;
    
    public final Start callStatementBody$onExceptionStatement = Start.on(getNamespace(), "onExceptionStatement");
    
    public ParserCombinator callStatementBody$onExceptionStatement() {
      if (callStatementBody$onExceptionStatementParser == null) {
        FutureParser future = scoped("onExceptionStatement", PUBLIC, true);
        callStatementBody$onExceptionStatementParser = future;
        future.setParser(
          star(
            statement()
          )
        );
      }
    
      return callStatementBody$onExceptionStatementParser;
    }
    
    // ========================================================
    // notOnExceptionStatement
    // ........................................................
    
    private ParserCombinator callStatementBody$notOnExceptionStatementParser = null;
    
    public final Start callStatementBody$notOnExceptionStatement = Start.on(getNamespace(), "notOnExceptionStatement");
    
    public ParserCombinator callStatementBody$notOnExceptionStatement() {
      if (callStatementBody$notOnExceptionStatementParser == null) {
        FutureParser future = scoped("notOnExceptionStatement", PUBLIC, true);
        callStatementBody$notOnExceptionStatementParser = future;
        future.setParser(
          star(
            statement()
          )
        );
      }
    
      return callStatementBody$notOnExceptionStatementParser;
    }
    
    // ========================================================
    // endCall
    // ........................................................
    
    private ParserCombinator callStatementBody$endCallParser = null;
    
    public final Start callStatementBody$endCall = Start.on(getNamespace(), "endCall");
    
    public ParserCombinator callStatementBody$endCall() {
      if (callStatementBody$endCallParser == null) {
        FutureParser future = scoped("endCall", PUBLIC, true);
        callStatementBody$endCallParser = future;
        future.setParser(
          keyword("END-CALL")
        );
      }
    
      return callStatementBody$endCallParser;
    }
    
    // ========================================================
    // programDefinition
    // ........................................................
    
    private ParserCombinator programDefinitionParser = null;
    
    public final Start programDefinition = Start.on(getNamespace(), "programDefinition");
    
    public ParserCombinator programDefinition() {
      if (programDefinitionParser == null) {
        FutureParser future = scoped("programDefinition", PUBLIC, true);
        programDefinitionParser = future;
        future.setParser(
          sequence(
            programDefinitionBody(),
            literal(".")
          )
        );
      }
    
      return programDefinitionParser;
    }
    
    // ========================================================
    // programDefinitionBody
    // ........................................................
    
    private ParserCombinator programDefinitionBodyParser = null;
    
    protected final Start programDefinitionBody = Start.on(getNamespace(), "programDefinitionBody");
    
    protected ParserCombinator programDefinitionBody() {
      if (programDefinitionBodyParser == null) {
        FutureParser future = scoped("programDefinitionBody", PRIVATE, true);
        programDefinitionBodyParser = future;
        future.setParser(
          sequence(
            as("identificationDivision",
              sequence(
                optional(
                  as("header",
                    sequence(
                      choice(
                        keyword("ID"),
                        keyword("IDENTIFICATION")
                      ),
                      keyword("DIVISION"),
                      literal(".")
                    )
                  )
                ),
                programIdParagraph()
              )
            ),
            optional(
              environmentDivision()
            ),
            optional(
              dataDivision()
            ),
            optional(
              procedureDivision()
            ),
            keyword("END"),
            keyword("PROGRAM"),
            programName()
          )
        );
      }
    
      return programDefinitionBodyParser;
    }
    
    // ========================================================
    // functionDefinition
    // ........................................................
    
    private ParserCombinator functionDefinitionParser = null;
    
    public final Start functionDefinition = Start.on(getNamespace(), "functionDefinition");
    
    public ParserCombinator functionDefinition() {
      if (functionDefinitionParser == null) {
        FutureParser future = scoped("functionDefinition", PUBLIC, true);
        functionDefinitionParser = future;
        future.setParser(
          sequence(
            functionDefinitionBody(),
            literal(".")
          )
        );
      }
    
      return functionDefinitionParser;
    }
    
    // ========================================================
    // functionDefinitionBody
    // ........................................................
    
    private ParserCombinator functionDefinitionBodyParser = null;
    
    protected final Start functionDefinitionBody = Start.on(getNamespace(), "functionDefinitionBody");
    
    protected ParserCombinator functionDefinitionBody() {
      if (functionDefinitionBodyParser == null) {
        FutureParser future = scoped("functionDefinitionBody", PRIVATE, true);
        functionDefinitionBodyParser = future;
        future.setParser(
          sequence(
            as("identificationDivision",
              sequence(
                optional(
                  as("header",
                    sequence(
                      choice(
                        keyword("ID"),
                        keyword("IDENTIFICATION")
                      ),
                      keyword("DIVISION"),
                      literal(".")
                    )
                  )
                ),
                functionIdParagraph()
              )
            ),
            optional(
              environmentDivision()
            ),
            optional(
              dataDivision()
            ),
            optional(
              procedureDivision()
            ),
            keyword("END"),
            keyword("FUNCTION"),
            functionName()
          )
        );
      }
    
      return functionDefinitionBodyParser;
    }
    
    // ========================================================
    // programIdParagraph
    // ........................................................
    
    private ParserCombinator programIdParagraphParser = null;
    
    public final Start programIdParagraph = Start.on(getNamespace(), "programIdParagraph");
    
    public ParserCombinator programIdParagraph() {
      if (programIdParagraphParser == null) {
        FutureParser future = scoped("programIdParagraph", PUBLIC, true);
        programIdParagraphParser = future;
        future.setParser(
          sequence(
            keyword("PROGRAM-ID"),
            literal("."),
            programName(),
            optional(
              keyword("IS")
            ),
            optional(
              commonName()
            ),
            optional(
              keyword("AS")
            ),
            optional(
              commonName()
            )
          )
        );
      }
    
      return programIdParagraphParser;
    }
    
    // ========================================================
    // functionIdParagraph
    // ........................................................
    
    private ParserCombinator functionIdParagraphParser = null;
    
    public final Start functionIdParagraph = Start.on(getNamespace(), "functionIdParagraph");
    
    public ParserCombinator functionIdParagraph() {
      if (functionIdParagraphParser == null) {
        FutureParser future = scoped("functionIdParagraph", PUBLIC, true);
        functionIdParagraphParser = future;
        future.setParser(
          sequence(
            keyword("FUNCTION-ID"),
            literal("."),
            functionName(),
            optional(
              keyword("IS")
            ),
            optional(
              commonName()
            ),
            optional(
              keyword("AS")
            ),
            optional(
              commonName()
            )
          )
        );
      }
    
      return functionIdParagraphParser;
    }
    
    // ========================================================
    // identificationDivision
    // ........................................................
    
    private ParserCombinator identificationDivisionParser = null;
    
    public final Start identificationDivision = Start.on(getNamespace(), "identificationDivision");
    
    public ParserCombinator identificationDivision() {
      if (identificationDivisionParser == null) {
        FutureParser future = scoped("identificationDivision", PUBLIC, true);
        identificationDivisionParser = future;
        future.setParser(
          sequence(
            choice(
              keyword("ID"),
              keyword("IDENTIFICATION")
            ),
            keyword("DIVISION"),
            literal("."),
            star(
              choice(
                programIdParagraph(),
                functionIdParagraph(),
                copyStatement()
              )
            )
          )
        );
      }
    
      return identificationDivisionParser;
    }
    
    // ========================================================
    // environmentDivision
    // ........................................................
    
    private ParserCombinator environmentDivisionParser = null;
    
    public final Start environmentDivision = Start.on(getNamespace(), "environmentDivision");
    
    public ParserCombinator environmentDivision() {
      if (environmentDivisionParser == null) {
        FutureParser future = scoped("environmentDivision", PUBLIC, true);
        environmentDivisionParser = future;
        future.setParser(
          sequence(
            keyword("ENVIRONMENT"),
            keyword("DIVISION"),
            literal("."),
            star(
              choice(
                configurationSection(),
                inputOutputSection(),
                copyStatement()
              )
            )
          )
        );
      }
    
      return environmentDivisionParser;
    }
    
    // ========================================================
    // dataDivision
    // ........................................................
    
    private ParserCombinator dataDivisionParser = null;
    
    public final Start dataDivision = Start.on(getNamespace(), "dataDivision");
    
    public ParserCombinator dataDivision() {
      if (dataDivisionParser == null) {
        FutureParser future = scoped("dataDivision", PUBLIC, true);
        dataDivisionParser = future;
        future.setParser(
          sequence(
            keyword("DATA"),
            keyword("DIVISION"),
            literal("."),
            star(
              choice(
                fileSection(),
                workingStorageSection(),
                linkageSection(),
                copyStatement()
              )
            )
          )
        );
      }
    
      return dataDivisionParser;
    }
    
    // ========================================================
    // procedureDivision
    // ........................................................
    
    private ParserCombinator procedureDivisionParser = null;
    
    public final Start procedureDivision = Start.on(getNamespace(), "procedureDivision");
    
    public ParserCombinator procedureDivision() {
      if (procedureDivisionParser == null) {
        FutureParser future = scoped("procedureDivision", PUBLIC, true);
        procedureDivisionParser = future;
        future.setParser(
          sequence(
            keyword("PROCEDURE"),
            keyword("DIVISION"),
            literal("."),
            optional(
              usingClause()
            ),
            optional(
              returningClause()
            ),
            star(
              choice(
                statement(),
                copyStatement()
              )
            )
          )
        );
      }
    
      return procedureDivisionParser;
    }
    
    // ========================================================
    // configurationSection
    // ........................................................
    
    private ParserCombinator configurationSectionParser = null;
    
    public final Start configurationSection = Start.on(getNamespace(), "configurationSection");
    
    public ParserCombinator configurationSection() {
      if (configurationSectionParser == null) {
        FutureParser future = scoped("configurationSection", PUBLIC, true);
        configurationSectionParser = future;
        future.setParser(
          sequence(
            keyword("CONFIGURATION"),
            keyword("SECTION"),
            literal("."),
            star(
              choice(
                specialNamesParagraph(),
                copyStatement()
              )
            )
          )
        );
      }
    
      return configurationSectionParser;
    }
    
    // ========================================================
    // inputOutputSection
    // ........................................................
    
    private ParserCombinator inputOutputSectionParser = null;
    
    public final Start inputOutputSection = Start.on(getNamespace(), "inputOutputSection");
    
    public ParserCombinator inputOutputSection() {
      if (inputOutputSectionParser == null) {
        FutureParser future = scoped("inputOutputSection", PUBLIC, true);
        inputOutputSectionParser = future;
        future.setParser(
          sequence(
            keyword("INPUT-OUTPUT"),
            keyword("SECTION"),
            literal("."),
            star(
              choice(
                fileControlParagraph(),
                copyStatement()
              )
            )
          )
        );
      }
    
      return inputOutputSectionParser;
    }
    
    // ========================================================
    // fileSection
    // ........................................................
    
    private ParserCombinator fileSectionParser = null;
    
    public final Start fileSection = Start.on(getNamespace(), "fileSection");
    
    public ParserCombinator fileSection() {
      if (fileSectionParser == null) {
        FutureParser future = scoped("fileSection", PUBLIC, true);
        fileSectionParser = future;
        future.setParser(
          sequence(
            keyword("FILE"),
            keyword("SECTION"),
            literal("."),
            star(
              choice(
                fileDescriptionEntry(),
                copyStatement()
              )
            )
          )
        );
      }
    
      return fileSectionParser;
    }
    
    // ========================================================
    // workingStorageSection
    // ........................................................
    
    private ParserCombinator workingStorageSectionParser = null;
    
    public final Start workingStorageSection = Start.on(getNamespace(), "workingStorageSection");
    
    public ParserCombinator workingStorageSection() {
      if (workingStorageSectionParser == null) {
        FutureParser future = scoped("workingStorageSection", PUBLIC, true);
        workingStorageSectionParser = future;
        future.setParser(
          sequence(
            keyword("WORKING-STORAGE"),
            keyword("SECTION"),
            literal("."),
            star(
              choice(
                dataDescriptionEntry(),
                copyStatement()
              )
            )
          )
        );
      }
    
      return workingStorageSectionParser;
    }
    
    // ========================================================
    // linkageSection
    // ........................................................
    
    private ParserCombinator linkageSectionParser = null;
    
    public final Start linkageSection = Start.on(getNamespace(), "linkageSection");
    
    public ParserCombinator linkageSection() {
      if (linkageSectionParser == null) {
        FutureParser future = scoped("linkageSection", PUBLIC, true);
        linkageSectionParser = future;
        future.setParser(
          sequence(
            keyword("LINKAGE"),
            keyword("SECTION"),
            literal("."),
            star(
              choice(
                dataDescriptionEntry(),
                copyStatement()
              )
            )
          )
        );
      }
    
      return linkageSectionParser;
    }
    
    // ========================================================
    // specialNamesParagraph
    // ........................................................
    
    private ParserCombinator specialNamesParagraphParser = null;
    
    public final Start specialNamesParagraph = Start.on(getNamespace(), "specialNamesParagraph");
    
    public ParserCombinator specialNamesParagraph() {
      if (specialNamesParagraphParser == null) {
        FutureParser future = scoped("specialNamesParagraph", PUBLIC, true);
        specialNamesParagraphParser = future;
        future.setParser(
          sequence(
            keyword("SPECIAL-NAMES"),
            literal("."),
            star(
              choice(
                specialNameStatement(),
                copyStatement()
              )
            ),
            optional(
              literal(".")
            )
          )
        );
      }
    
      return specialNamesParagraphParser;
    }
    
    // ========================================================
    // fileControlParagraph
    // ........................................................
    
    private ParserCombinator fileControlParagraphParser = null;
    
    public final Start fileControlParagraph = Start.on(getNamespace(), "fileControlParagraph");
    
    public ParserCombinator fileControlParagraph() {
      if (fileControlParagraphParser == null) {
        FutureParser future = scoped("fileControlParagraph", PUBLIC, true);
        fileControlParagraphParser = future;
        future.setParser(
          sequence(
            keyword("FILE-CONTROL"),
            literal("."),
            plus(
              choice(
                selectStatement(),
                copyStatement()
              )
            )
          )
        );
      }
    
      return fileControlParagraphParser;
    }
    
    // ========================================================
    // fileDescriptionEntry
    // ........................................................
    
    private ParserCombinator fileDescriptionEntryParser = null;
    
    public final Start fileDescriptionEntry = Start.on(getNamespace(), "fileDescriptionEntry");
    
    public ParserCombinator fileDescriptionEntry() {
      if (fileDescriptionEntryParser == null) {
        FutureParser future = scoped("fileDescriptionEntry", PUBLIC, true);
        fileDescriptionEntryParser = future;
        future.setParser(
          sequence(
            keyword("FD"),
            fileName(),
            star(
              optional(
                fileDescriptionClause()
              )
            ),
            literal(".")
          )
        );
      }
    
      return fileDescriptionEntryParser;
    }
    
    // ========================================================
    // dataDescriptionEntry
    // ........................................................
    
    private ParserCombinator dataDescriptionEntryParser = null;
    
    public final Start dataDescriptionEntry = Start.on(getNamespace(), "dataDescriptionEntry");
    
    public ParserCombinator dataDescriptionEntry() {
      if (dataDescriptionEntryParser == null) {
        FutureParser future = scoped("dataDescriptionEntry", PUBLIC, true);
        dataDescriptionEntryParser = future;
        future.setParser(
          sequence(
            choice(
              levelNumber(),
              levelNumber77()
            ),
            optional(
              choice(
                dataName(),
                filler()
              )
            ),
            optional(
              pictureClause()
            ),
            optional(
              valueClause()
            ),
            optional(
              copyStatement()
            )
          )
        );
      }
    
      return dataDescriptionEntryParser;
    }
    
    // ========================================================
    // programName
    // ........................................................
    
    private ParserCombinator programNameParser = null;
    
    public final Start programName = Start.on(getNamespace(), "programName");
    
    public ParserCombinator programName() {
      if (programNameParser == null) {
        FutureParser future = scoped("programName", PUBLIC, true);
        programNameParser = future;
        future.setParser(
          choice(
            alphanumeric(),
            justAName()
          )
        );
      }
    
      return programNameParser;
    }
    
    // ========================================================
    // alphanumeric
    // ........................................................
    
    private ParserCombinator alphanumericParser = null;
    
    public final Start alphanumeric = Start.on(getNamespace(), "alphanumeric");
    
    public ParserCombinator alphanumeric() {
      if (alphanumericParser == null) {
        FutureParser future = scoped("alphanumeric", PUBLIC, true);
        alphanumericParser = future;
        future.setParser(
          choice(
            alphanumericLiteral(),
            alphanumericConstant()
          )
        );
      }
    
      return alphanumericParser;
    }
    
    // ========================================================
    // alphanumericConstant
    // ........................................................
    
    private ParserCombinator alphanumericConstantParser = null;
    
    public final Start alphanumericConstant = Start.on(getNamespace(), "alphanumericConstant");
    
    public ParserCombinator alphanumericConstant() {
      if (alphanumericConstantParser == null) {
        FutureParser future = scoped("alphanumericConstant", PUBLIC, true);
        alphanumericConstantParser = future;
        future.setParser(
          justAName()
        );
      }
    
      return alphanumericConstantParser;
    }
    
    // ========================================================
    // justAName
    // ........................................................
    
    private ParserCombinator justANameParser = null;
    
    protected final Start justAName = Start.on(getNamespace(), "justAName");
    
    protected ParserCombinator justAName() {
      if (justANameParser == null) {
        FutureParser future = scoped("justAName", PRIVATE, true);
        justANameParser = future;
        future.setParser(
          sequence(
            not(
              hexadecimalLiteral()
            ),
            cobolWord()
          )
        );
      }
    
      return justANameParser;
    }
    
    // ========================================================
    // hexadecimalLiteral
    // ........................................................
    
    private ParserCombinator hexadecimalLiteralParser = null;
    
    protected final Start hexadecimalLiteral = Start.on(getNamespace(), "hexadecimalLiteral");
    
    protected ParserCombinator hexadecimalLiteral() {
      if (hexadecimalLiteralParser == null) {
        FutureParser future = scoped("hexadecimalLiteral", PRIVATE, true);
        hexadecimalLiteralParser = future;
        future.setParser(
          choice(
            hexadecimal(),
            alphanumericHexadecimal(),
            nationalAlphanumericHexadecimal(),
            booleanHexadecimal()
          )
        );
      }
    
      return hexadecimalLiteralParser;
    }
    
    // ========================================================
    // hexadecimal
    // ........................................................
    
    private ParserCombinator hexadecimalParser = null;
    
    public final Start hexadecimal = Start.on(getNamespace(), "hexadecimal");
    
    public ParserCombinator hexadecimal() {
      if (hexadecimalParser == null) {
        FutureParser future = scoped("hexadecimal", PUBLIC, true);
        hexadecimalParser = future;
        future.setParser(
          sequence(
            literal("H"),
            opt(NOSKIP,
              str()
            )
          )
        );
      }
    
      return hexadecimalParser;
    }
    
    // ========================================================
    // alphanumericHexadecimal
    // ........................................................
    
    private ParserCombinator alphanumericHexadecimalParser = null;
    
    public final Start alphanumericHexadecimal = Start.on(getNamespace(), "alphanumericHexadecimal");
    
    public ParserCombinator alphanumericHexadecimal() {
      if (alphanumericHexadecimalParser == null) {
        FutureParser future = scoped("alphanumericHexadecimal", PUBLIC, true);
        alphanumericHexadecimalParser = future;
        future.setParser(
          sequence(
            literal("X"),
            opt(NOSKIP,
              str()
            )
          )
        );
      }
    
      return alphanumericHexadecimalParser;
    }
    
    // ========================================================
    // nationalAlphanumericHexadecimal
    // ........................................................
    
    private ParserCombinator nationalAlphanumericHexadecimalParser = null;
    
    public final Start nationalAlphanumericHexadecimal = Start.on(getNamespace(), "nationalAlphanumericHexadecimal");
    
    public ParserCombinator nationalAlphanumericHexadecimal() {
      if (nationalAlphanumericHexadecimalParser == null) {
        FutureParser future = scoped("nationalAlphanumericHexadecimal", PUBLIC, true);
        nationalAlphanumericHexadecimalParser = future;
        future.setParser(
          sequence(
            literal("NX"),
            opt(NOSKIP,
              str()
            )
          )
        );
      }
    
      return nationalAlphanumericHexadecimalParser;
    }
    
    // ========================================================
    // booleanHexadecimal
    // ........................................................
    
    private ParserCombinator booleanHexadecimalParser = null;
    
    public final Start booleanHexadecimal = Start.on(getNamespace(), "booleanHexadecimal");
    
    public ParserCombinator booleanHexadecimal() {
      if (booleanHexadecimalParser == null) {
        FutureParser future = scoped("booleanHexadecimal", PUBLIC, true);
        booleanHexadecimalParser = future;
        future.setParser(
          sequence(
            literal("BX"),
            opt(NOSKIP,
              str()
            )
          )
        );
      }
    
      return booleanHexadecimalParser;
    }
    
    // ========================================================
    // functionName
    // ........................................................
    
    private ParserCombinator functionNameParser = null;
    
    public final Start functionName = Start.on(getNamespace(), "functionName");
    
    public ParserCombinator functionName() {
      if (functionNameParser == null) {
        FutureParser future = scoped("functionName", PUBLIC, true);
        functionNameParser = future;
        future.setParser(
          justAName()
        );
      }
    
      return functionNameParser;
    }
    
    // ========================================================
    // commonName
    // ........................................................
    
    private ParserCombinator commonNameParser = null;
    
    public final Start commonName = Start.on(getNamespace(), "commonName");
    
    public ParserCombinator commonName() {
      if (commonNameParser == null) {
        FutureParser future = scoped("commonName", PUBLIC, true);
        commonNameParser = future;
        future.setParser(
          justAName()
        );
      }
    
      return commonNameParser;
    }
    
    // ========================================================
    // textName
    // ........................................................
    
    private ParserCombinator textNameParser = null;
    
    public final Start textName = Start.on(getNamespace(), "textName");
    
    public ParserCombinator textName() {
      if (textNameParser == null) {
        FutureParser future = scoped("textName", PUBLIC, true);
        textNameParser = future;
        future.setParser(
          justAName()
        );
      }
    
      return textNameParser;
    }
    
    // ========================================================
    // libraryName
    // ........................................................
    
    private ParserCombinator libraryNameParser = null;
    
    public final Start libraryName = Start.on(getNamespace(), "libraryName");
    
    public ParserCombinator libraryName() {
      if (libraryNameParser == null) {
        FutureParser future = scoped("libraryName", PUBLIC, true);
        libraryNameParser = future;
        future.setParser(
          justAName()
        );
      }
    
      return libraryNameParser;
    }
    
    // ========================================================
    // dataName
    // ........................................................
    
    private ParserCombinator dataNameParser = null;
    
    public final Start dataName = Start.on(getNamespace(), "dataName");
    
    public ParserCombinator dataName() {
      if (dataNameParser == null) {
        FutureParser future = scoped("dataName", PUBLIC, true);
        dataNameParser = future;
        future.setParser(
          justAName()
        );
      }
    
      return dataNameParser;
    }
    
    // ========================================================
    // fileName
    // ........................................................
    
    private ParserCombinator fileNameParser = null;
    
    public final Start fileName = Start.on(getNamespace(), "fileName");
    
    public ParserCombinator fileName() {
      if (fileNameParser == null) {
        FutureParser future = scoped("fileName", PUBLIC, true);
        fileNameParser = future;
        future.setParser(
          choice(
            alphanumeric(),
            justAName()
          )
        );
      }
    
      return fileNameParser;
    }
    
    // ========================================================
    // literal
    // ........................................................
    
    private ParserCombinator literalParser = null;
    
    public final Start literal = Start.on(getNamespace(), "literal");
    
    public ParserCombinator literal() {
      if (literalParser == null) {
        FutureParser future = scoped("literal", PUBLIC, true);
        literalParser = future;
        future.setParser(
          choice(
            numericLiteral(),
            alphanumericLiteral()
          )
        );
      }
    
      return literalParser;
    }
    
    // ========================================================
    // numericLiteral
    // ........................................................
    
    private ParserCombinator numericLiteralParser = null;
    
    public final Start numericLiteral = Start.on(getNamespace(), "numericLiteral");
    
    public ParserCombinator numericLiteral() {
      if (numericLiteralParser == null) {
        FutureParser future = scoped("numericLiteral", PUBLIC, true);
        numericLiteralParser = future;
        future.setParser(
          choice(
            integerLiteral(),
            decimalLiteral()
          )
        );
      }
    
      return numericLiteralParser;
    }
    
    // ========================================================
    // integerLiteral
    // ........................................................
    
    private ParserCombinator integerLiteralParser = null;
    
    public final Start integerLiteral = Start.on(getNamespace(), "integerLiteral");
    
    public ParserCombinator integerLiteral() {
      if (integerLiteralParser == null) {
        FutureParser future = scoped("integerLiteral", PUBLIC, true);
        integerLiteralParser = future;
        future.setParser(
          choice(
            sequence(
              literal("+"),
              opt(NOSKIP,
                uintgr()
              )
            ),
            sequence(
              literal("-"),
              opt(NOSKIP,
                uintgr()
              )
            ),
            uintgr()
          )
        );
      }
    
      return integerLiteralParser;
    }
    
    // ========================================================
    // decimalLiteral
    // ........................................................
    
    private ParserCombinator decimalLiteralParser = null;
    
    public final Start decimalLiteral = Start.on(getNamespace(), "decimalLiteral");
    
    public ParserCombinator decimalLiteral() {
      if (decimalLiteralParser == null) {
        FutureParser future = scoped("decimalLiteral", PUBLIC, true);
        decimalLiteralParser = future;
        future.setParser(
          choice(
            sequence(
              choice(
                literal("+"),
                literal("-")
              ),
              opt(NOSKIP,
                unsigned_decimal()
              )
            ),
            unsigned_decimal()
          )
        );
      }
    
      return decimalLiteralParser;
    }
    
    // ========================================================
    // alphanumericLiteral
    // ........................................................
    
    private ParserCombinator alphanumericLiteralParser = null;
    
    public final Start alphanumericLiteral = Start.on(getNamespace(), "alphanumericLiteral");
    
    public ParserCombinator alphanumericLiteral() {
      if (alphanumericLiteralParser == null) {
        FutureParser future = scoped("alphanumericLiteral", PUBLIC, true);
        alphanumericLiteralParser = future;
        future.setParser(
          choice(
            sequence(
              literal("X"),
              opt(NOSKIP,
                str()
              )
            ),
            sequence(
              literal("N"),
              opt(NOSKIP,
                str()
              )
            ),
            str()
          )
        );
      }
    
      return alphanumericLiteralParser;
    }
    
    // ========================================================
    // pseudoLiteral
    // ........................................................
    
    private ParserCombinator pseudoLiteralParser = null;
    
    public final Start pseudoLiteral = Start.on(getNamespace(), "pseudoLiteral");
    
    public ParserCombinator pseudoLiteral() {
      if (pseudoLiteralParser == null) {
        FutureParser future = scoped("pseudoLiteral", PUBLIC, true);
        pseudoLiteralParser = future;
        future.setParser(
          sequence(
            sequence(
              literal("="),
              opt(NOSKIP,
                literal("=")
              )
            ),
            upto(
              star(
                any()
              ),
              // Closure:
              sequence(
                literal("="),
                opt(NOSKIP,
                  sequence(
                    literal("="),
                    not(
                      literal("=")
                    )
                  )
                )
              )
            ),
            sequence(
              literal("="),
              opt(NOSKIP,
                literal("=")
              )
            )
          )
        );
      }
    
      return pseudoLiteralParser;
    }
    
    // ========================================================
    // str
    // ........................................................
    
    private ParserCombinator strParser = null;
    
    public final Start str = Start.on(getNamespace(), "str");
    
    public ParserCombinator str() {
      if (strParser == null) {
        FutureParser future = scoped("str", PUBLIC, true);
        strParser = future;
        future.setParser(
          sequence(
            sequence(
              tagged(STRING),
              any()
            ),
            optional(
              opt(NOSKIP,
                plus(
                  sequence(
                    tagged(STRING),
                    any()
                  )
                )
              )
            )
          )
        );
      }
    
      return strParser;
    }
    
    // ========================================================
    // unsigned_decimal
    // ........................................................
    
    private ParserCombinator unsigned_decimalParser = null;
    
    public final Start unsigned_decimal = Start.on(getNamespace(), "unsigned_decimal");
    
    public ParserCombinator unsigned_decimal() {
      if (unsigned_decimalParser == null) {
        FutureParser future = scoped("unsigned_decimal", PUBLIC, true);
        unsigned_decimalParser = future;
        future.setParser(
          choice(
            sequence(
              uintgr(),
              opt(NOSKIP,
                sequence(
                  choice(
                    literal(","),
                    literal(".")
                  ),
                  uintgr()
                )
              )
            ),
            sequence(
              literal("."),
              opt(NOSKIP,
                uintgr()
              )
            )
          )
        );
      }
    
      return unsigned_decimalParser;
    }
    
    // ========================================================
    // uintgr
    // ........................................................
    
    private ParserCombinator uintgrParser = null;
    
    public final Start uintgr = Start.on(getNamespace(), "uintgr");
    
    public ParserCombinator uintgr() {
      if (uintgrParser == null) {
        FutureParser future = scoped("uintgr", PUBLIC, true);
        uintgrParser = future;
        future.setParser(
          sequence(
            tagged(NUMBER),
            any(),
            optional(
              opt(NOSKIP,
                plus(
                  sequence(
                    tagged(NUMBER),
                    any()
                  )
                )
              )
            )
          )
        );
      }
    
      return uintgrParser;
    }
    
    // ========================================================
    // levelNumber
    // ........................................................
    
    private ParserCombinator levelNumberParser = null;
    
    public final Start levelNumber = Start.on(getNamespace(), "levelNumber");
    
    public ParserCombinator levelNumber() {
      if (levelNumberParser == null) {
        FutureParser future = scoped("levelNumber", PUBLIC, true);
        levelNumberParser = future;
        future.setParser(
          sequence(
            tagged(NUMBER),
            any()
          )
        );
      }
    
      return levelNumberParser;
    }
    
    // ========================================================
    // levelNumber77
    // ........................................................
    
    private ParserCombinator levelNumber77Parser = null;
    
    public final Start levelNumber77 = Start.on(getNamespace(), "levelNumber77");
    
    public ParserCombinator levelNumber77() {
      if (levelNumber77Parser == null) {
        FutureParser future = scoped("levelNumber77", PUBLIC, true);
        levelNumber77Parser = future;
        future.setParser(
          number("77")
        );
      }
    
      return levelNumber77Parser;
    }
    
    // ========================================================
    // filler
    // ........................................................
    
    private ParserCombinator fillerParser = null;
    
    public final Start filler = Start.on(getNamespace(), "filler");
    
    public ParserCombinator filler() {
      if (fillerParser == null) {
        FutureParser future = scoped("filler", PUBLIC, true);
        fillerParser = future;
        future.setParser(
          keyword("FILLER")
        );
      }
    
      return fillerParser;
    }
    
    // ========================================================
    // pictureClause
    // ........................................................
    
    private ParserCombinator pictureClauseParser = null;
    
    public final Start pictureClause = Start.on(getNamespace(), "pictureClause");
    
    public ParserCombinator pictureClause() {
      if (pictureClauseParser == null) {
        FutureParser future = scoped("pictureClause", PUBLIC, true);
        pictureClauseParser = future;
        future.setParser(
          sequence(
            keyword("PICTURE"),
            optional(
              keyword("IS")
            ),
            pictureString()
          )
        );
      }
    
      return pictureClauseParser;
    }
    
    // ========================================================
    // valueClause
    // ........................................................
    
    private ParserCombinator valueClauseParser = null;
    
    public final Start valueClause = Start.on(getNamespace(), "valueClause");
    
    public ParserCombinator valueClause() {
      if (valueClauseParser == null) {
        FutureParser future = scoped("valueClause", PUBLIC, true);
        valueClauseParser = future;
        future.setParser(
          sequence(
            keyword("VALUE"),
            optional(
              keyword("IS")
            ),
            literal()
          )
        );
      }
    
      return valueClauseParser;
    }
    
    // ========================================================
    // pictureString
    // ........................................................
    
    private ParserCombinator pictureStringParser = null;
    
    public final Start pictureString = Start.on(getNamespace(), "pictureString");
    
    public ParserCombinator pictureString() {
      if (pictureStringParser == null) {
        FutureParser future = scoped("pictureString", PUBLIC, true);
        pictureStringParser = future;
        future.setParser(
          sequence(
            tagged(WORD),
            any()
          )
        );
      }
    
      return pictureStringParser;
    }
    
    // ========================================================
    // modifier
    // ........................................................
    
    private ParserCombinator modifierParser = null;
    
    public final Start modifier = Start.on(getNamespace(), "modifier");
    
    public ParserCombinator modifier() {
      if (modifierParser == null) {
        FutureParser future = scoped("modifier", PUBLIC, true);
        modifierParser = future;
        future.setParser(
          choice(
            keyword("OMITTED"),
            cobolWord()
          )
        );
      }
    
      return modifierParser;
    }
    
    // ========================================================
    // arg
    // ........................................................
    
    private ParserCombinator argParser = null;
    
    public final Start arg = Start.on(getNamespace(), "arg");
    
    public ParserCombinator arg() {
      if (argParser == null) {
        FutureParser future = scoped("arg", PUBLIC, true);
        argParser = future;
        future.setParser(
          choice(
            literal(),
            dataName(),
            cobolWord()
          )
        );
      }
    
      return argParser;
    }
    
    // ========================================================
    // usingClause
    // ........................................................
    
    private ParserCombinator usingClauseParser = null;
    
    public final Start usingClause = Start.on(getNamespace(), "usingClause");
    
    public ParserCombinator usingClause() {
      if (usingClauseParser == null) {
        FutureParser future = scoped("usingClause", PUBLIC, true);
        usingClauseParser = future;
        future.setParser(
          sequence(
            keyword("USING"),
            plus(
              choice(
                byReference(),
                byContent(),
                byValue()
              )
            )
          )
        );
      }
    
      return usingClauseParser;
    }
    
    // ========================================================
    // returningClause
    // ........................................................
    
    private ParserCombinator returningClauseParser = null;
    
    public final Start returningClause = Start.on(getNamespace(), "returningClause");
    
    public ParserCombinator returningClause() {
      if (returningClauseParser == null) {
        FutureParser future = scoped("returningClause", PUBLIC, true);
        returningClauseParser = future;
        future.setParser(
          sequence(
            keyword("RETURNING"),
            dataName()
          )
        );
      }
    
      return returningClauseParser;
    }
    
    // ========================================================
    // specialNameStatement
    // ........................................................
    
    private ParserCombinator specialNameStatementParser = null;
    
    public final Start specialNameStatement = Start.on(getNamespace(), "specialNameStatement");
    
    public ParserCombinator specialNameStatement() {
      if (specialNameStatementParser == null) {
        FutureParser future = scoped("specialNameStatement", PUBLIC, true);
        specialNameStatementParser = future;
        future.setParser(
          sequence(
            cobolWord(),
            optional(
              keyword("IS")
            ),
            cobolWord()
          )
        );
      }
    
      return specialNameStatementParser;
    }
    
    // ========================================================
    // selectStatement
    // ........................................................
    
    private ParserCombinator selectStatementParser = null;
    
    public final Start selectStatement = Start.on(getNamespace(), "selectStatement");
    
    public ParserCombinator selectStatement() {
      if (selectStatementParser == null) {
        FutureParser future = scoped("selectStatement", PUBLIC, true);
        selectStatementParser = future;
        future.setParser(
          sequence(
            keyword("SELECT"),
            optional(
              keyword("OPTIONAL")
            ),
            fileName(),
            keyword("ASSIGN"),
            keyword("TO"),
            choice(
              assignmentName(),
              literal()
            )
          )
        );
      }
    
      return selectStatementParser;
    }
    
    // ========================================================
    // assignmentName
    // ........................................................
    
    private ParserCombinator assignmentNameParser = null;
    
    public final Start assignmentName = Start.on(getNamespace(), "assignmentName");
    
    public ParserCombinator assignmentName() {
      if (assignmentNameParser == null) {
        FutureParser future = scoped("assignmentName", PUBLIC, true);
        assignmentNameParser = future;
        future.setParser(
          cobolWord()
        );
      }
    
      return assignmentNameParser;
    }
    
    // ========================================================
    // fileDescriptionClause
    // ........................................................
    
    private ParserCombinator fileDescriptionClauseParser = null;
    
    public final Start fileDescriptionClause = Start.on(getNamespace(), "fileDescriptionClause");
    
    public ParserCombinator fileDescriptionClause() {
      if (fileDescriptionClauseParser == null) {
        FutureParser future = scoped("fileDescriptionClause", PUBLIC, true);
        fileDescriptionClauseParser = future;
        future.setParser(
          cobolWord()
        );
      }
    
      return fileDescriptionClauseParser;
    }
    
    // ========================================================
    // statement
    // ........................................................
    
    private ParserCombinator statementParser = null;
    
    public final Start statement = Start.on(getNamespace(), "statement");
    
    public ParserCombinator statement() {
      if (statementParser == null) {
        FutureParser future = scoped("statement", PUBLIC, true);
        statementParser = future;
        future.setParser(
          sequence(
            plus(
              choice(
                anyWord(),
                anyLiteral(),
                anySymbol(),
                anyWhitespace()
              )
            ),
            choice(
              optional(
                literal(".")
              ),
              optional(
                eol()
              )
            )
          )
        );
      }
    
      return statementParser;
    }
    
    // ========================================================
    // anyStatement
    // ........................................................
    
    private ParserCombinator anyStatementParser = null;
    
    public final Start anyStatement = Start.on(getNamespace(), "anyStatement");
    
    public ParserCombinator anyStatement() {
      if (anyStatementParser == null) {
        FutureParser future = scoped("anyStatement", PUBLIC, true);
        anyStatementParser = future;
        future.setParser(
          sequence(
            plus(
              choice(
                anyWord(),
                anyLiteral(),
                anySymbol(),
                anyWhitespace()
              )
            ),
            optional(
              literal(".")
            )
          )
        );
      }
    
      return anyStatementParser;
    }
    
    // ========================================================
    // anyParagraph
    // ........................................................
    
    private ParserCombinator anyParagraphParser = null;
    
    public final Start anyParagraph = Start.on(getNamespace(), "anyParagraph");
    
    public ParserCombinator anyParagraph() {
      if (anyParagraphParser == null) {
        FutureParser future = scoped("anyParagraph", PUBLIC, true);
        anyParagraphParser = future;
        future.setParser(
          sequence(
            plus(
              choice(
                anyWord(),
                anyLiteral(),
                anySymbol(),
                anyWhitespace()
              )
            ),
            optional(
              literal(".")
            )
          )
        );
      }
    
      return anyParagraphParser;
    }
    
    // ========================================================
    // anySection
    // ........................................................
    
    private ParserCombinator anySectionParser = null;
    
    public final Start anySection = Start.on(getNamespace(), "anySection");
    
    public ParserCombinator anySection() {
      if (anySectionParser == null) {
        FutureParser future = scoped("anySection", PUBLIC, true);
        anySectionParser = future;
        future.setParser(
          sequence(
            plus(
              choice(
                anyWord(),
                anyLiteral(),
                anySymbol(),
                anyWhitespace()
              )
            ),
            optional(
              literal(".")
            )
          )
        );
      }
    
      return anySectionParser;
    }
    
    // ========================================================
    // anyDivision
    // ........................................................
    
    private ParserCombinator anyDivisionParser = null;
    
    public final Start anyDivision = Start.on(getNamespace(), "anyDivision");
    
    public ParserCombinator anyDivision() {
      if (anyDivisionParser == null) {
        FutureParser future = scoped("anyDivision", PUBLIC, true);
        anyDivisionParser = future;
        future.setParser(
          sequence(
            plus(
              choice(
                anyWord(),
                anyLiteral(),
                anySymbol(),
                anyWhitespace()
              )
            ),
            optional(
              literal(".")
            )
          )
        );
      }
    
      return anyDivisionParser;
    }
    
    // ========================================================
    // anyLine
    // ........................................................
    
    private ParserCombinator anyLineParser = null;
    
    public final Start anyLine = Start.on(getNamespace(), "anyLine");
    
    public ParserCombinator anyLine() {
      if (anyLineParser == null) {
        FutureParser future = scoped("anyLine", PUBLIC, true);
        anyLineParser = future;
        future.setParser(
          sequence(
            plus(
              choice(
                anyWord(),
                anyLiteral(),
                anySymbol(),
                anyWhitespace()
              )
            ),
            optional(
              eol()
            )
          )
        );
      }
    
      return anyLineParser;
    }
    
    // ========================================================
    // anyWord
    // ........................................................
    
    private ParserCombinator anyWordParser = null;
    
    public final Start anyWord = Start.on(getNamespace(), "anyWord");
    
    public ParserCombinator anyWord() {
      if (anyWordParser == null) {
        FutureParser future = scoped("anyWord", PUBLIC, true);
        anyWordParser = future;
        future.setParser(
          sequence(
            tagged(WORD),
            any()
          )
        );
      }
    
      return anyWordParser;
    }
    
    // ========================================================
    // anyLiteral
    // ........................................................
    
    private ParserCombinator anyLiteralParser = null;
    
    public final Start anyLiteral = Start.on(getNamespace(), "anyLiteral");
    
    public ParserCombinator anyLiteral() {
      if (anyLiteralParser == null) {
        FutureParser future = scoped("anyLiteral", PUBLIC, true);
        anyLiteralParser = future;
        future.setParser(
          choice(
            literal(),
            str()
          )
        );
      }
    
      return anyLiteralParser;
    }
    
    // ========================================================
    // anySymbol
    // ........................................................
    
    private ParserCombinator anySymbolParser = null;
    
    public final Start anySymbol = Start.on(getNamespace(), "anySymbol");
    
    public ParserCombinator anySymbol() {
      if (anySymbolParser == null) {
        FutureParser future = scoped("anySymbol", PUBLIC, true);
        anySymbolParser = future;
        future.setParser(
          sequence(
            tagged(SYMBOL),
            any()
          )
        );
      }
    
      return anySymbolParser;
    }
    
    // ========================================================
    // anyWhitespace
    // ........................................................
    
    private ParserCombinator anyWhitespaceParser = null;
    
    public final Start anyWhitespace = Start.on(getNamespace(), "anyWhitespace");
    
    public ParserCombinator anyWhitespace() {
      if (anyWhitespaceParser == null) {
        FutureParser future = scoped("anyWhitespace", PUBLIC, true);
        anyWhitespaceParser = future;
        future.setParser(
          sequence(
            tagged(WHITESPACE),
            any()
          )
        );
      }
    
      return anyWhitespaceParser;
    }
    
}
