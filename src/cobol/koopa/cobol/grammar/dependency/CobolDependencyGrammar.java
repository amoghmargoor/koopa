package koopa.cobol.grammar.dependency;

import koopa.core.data.markers.Start;
import koopa.core.parsers.ParserCombinator;
import koopa.core.parsers.FutureParser;

import static koopa.core.parsers.combinators.Opt.NOSKIP;
import static koopa.core.grammars.combinators.Scoped.Visibility.PUBLIC;
import static koopa.core.grammars.combinators.Scoped.Visibility.PRIVATE;
import static koopa.core.grammars.combinators.Scoped.Visibility.HIDING;

import static koopa.core.data.tags.SyntacticTag.END_OF_LINE;
import static koopa.core.data.tags.SyntacticTag.NUMBER;
import static koopa.core.data.tags.SyntacticTag.SEPARATOR;
import static koopa.core.data.tags.SyntacticTag.WHITESPACE;
import static koopa.core.data.tags.SyntacticTag.WORD;

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
            keyword("CALL"),
            choice(
              sequence(
                optional(
                  sequence(
                    callStatement$programName(),
                    keyword("AS")
                  )
                ),
                keyword("NESTED")
              ),
              sequence(
                callStatement$programName(),
                keyword("AS"),
                callStatement$programPrototypeName()
              ),
              sequence(
                as("mnemonicName",
                  identifier()
                ),
                callStatement$programName()
              ),
              callStatement$programName()
            ),
            optional(
              callStatement$using()
            ),
            optional(
              callStatement$giving()
            ),
            optional(
              as("unknown",
                skipto(
                  somethingFollowingAStatement()
                )
              )
            ),
            optional(
              choice(
                onOverflow(),
                permuted(
                  onException(),
                  notOnException()
                )
              )
            ),
            optional(
              as("end",
                keyword("END-CALL")
              )
            )
          )
        );
      }
    
      return callStatementParser;
    }
    
    // ========================================================
    // programName
    // ........................................................
    
    private ParserCombinator callStatement$programNameParser = null;
    
    public final Start callStatement$programName = Start.on(getNamespace(), "programName");
    
    public ParserCombinator callStatement$programName() {
      if (callStatement$programNameParser == null) {
        FutureParser future = scoped("programName", PUBLIC, true);
        callStatement$programNameParser = future;
        future.setParser(
          choice(
            alphanumericLiteral(),
            identifier()
          )
        );
      }
    
      return callStatement$programNameParser;
    }
    
    // ========================================================
    // programPrototypeName
    // ........................................................
    
    private ParserCombinator callStatement$programPrototypeNameParser = null;
    
    public final Start callStatement$programPrototypeName = Start.on(getNamespace(), "programPrototypeName");
    
    public ParserCombinator callStatement$programPrototypeName() {
      if (callStatement$programPrototypeNameParser == null) {
        FutureParser future = scoped("programPrototypeName", PUBLIC, true);
        callStatement$programPrototypeNameParser = future;
        future.setParser(
          justAName()
        );
      }
    
      return callStatement$programPrototypeNameParser;
    }
    
    // ========================================================
    // using
    // ........................................................
    
    private ParserCombinator callStatement$usingParser = null;
    
    public final Start callStatement$using = Start.on(getNamespace(), "using");
    
    public ParserCombinator callStatement$using() {
      if (callStatement$usingParser == null) {
        FutureParser future = scoped("using", PUBLIC, true);
        callStatement$usingParser = future;
        future.setParser(
          sequence(
            keyword("USING"),
            plus(
              choice(
                callStatement$using$byReference(),
                callStatement$using$byContent(),
                callStatement$using$byValue(),
                copyStatement()
              )
            )
          )
        );
      }
    
      return callStatement$usingParser;
    }
    
    // ========================================================
    // byReference
    // ........................................................
    
    private ParserCombinator callStatement$using$byReferenceParser = null;
    
    public final Start callStatement$using$byReference = Start.on(getNamespace(), "byReference");
    
    public ParserCombinator callStatement$using$byReference() {
      if (callStatement$using$byReferenceParser == null) {
        FutureParser future = scoped("byReference", PUBLIC, true);
        callStatement$using$byReferenceParser = future;
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
                callStatement$using$modifier(),
                callStatement$using$arg()
              )
            )
          )
        );
      }
    
      return callStatement$using$byReferenceParser;
    }
    
    // ========================================================
    // byContent
    // ........................................................
    
    private ParserCombinator callStatement$using$byContentParser = null;
    
    public final Start callStatement$using$byContent = Start.on(getNamespace(), "byContent");
    
    public ParserCombinator callStatement$using$byContent() {
      if (callStatement$using$byContentParser == null) {
        FutureParser future = scoped("byContent", PUBLIC, true);
        callStatement$using$byContentParser = future;
        future.setParser(
          sequence(
            optional(
              keyword("BY")
            ),
            keyword("CONTENT"),
            plus(
              choice(
                callStatement$using$modifier(),
                callStatement$using$arg()
              )
            )
          )
        );
      }
    
      return callStatement$using$byContentParser;
    }
    
    // ========================================================
    // byValue
    // ........................................................
    
    private ParserCombinator callStatement$using$byValueParser = null;
    
    public final Start callStatement$using$byValue = Start.on(getNamespace(), "byValue");
    
    public ParserCombinator callStatement$using$byValue() {
      if (callStatement$using$byValueParser == null) {
        FutureParser future = scoped("byValue", PUBLIC, true);
        callStatement$using$byValueParser = future;
        future.setParser(
          sequence(
            optional(
              keyword("BY")
            ),
            keyword("VALUE"),
            plus(
              choice(
                callStatement$using$modifier(),
                callStatement$using$arg()
              )
            )
          )
        );
      }
    
      return callStatement$using$byValueParser;
    }
    
    // ========================================================
    // modifier
    // ........................................................
    
    private ParserCombinator callStatement$using$modifierParser = null;
    
    public final Start callStatement$using$modifier = Start.on(getNamespace(), "modifier");
    
    public ParserCombinator callStatement$using$modifier() {
      if (callStatement$using$modifierParser == null) {
        FutureParser future = scoped("modifier", PUBLIC, true);
        callStatement$using$modifierParser = future;
        future.setParser(
          choice(
            as("unsigned",
              keyword("UNSIGNED")
            ),
            callStatement$using$modifier$sizeIs()
          )
        );
      }
    
      return callStatement$using$modifierParser;
    }
    
    // ========================================================
    // sizeIs
    // ........................................................
    
    private ParserCombinator callStatement$using$modifier$sizeIsParser = null;
    
    public final Start callStatement$using$modifier$sizeIs = Start.on(getNamespace(), "sizeIs");
    
    public ParserCombinator callStatement$using$modifier$sizeIs() {
      if (callStatement$using$modifier$sizeIsParser == null) {
        FutureParser future = scoped("sizeIs", PUBLIC, true);
        callStatement$using$modifier$sizeIsParser = future;
        future.setParser(
          sequence(
            keyword("SIZE"),
            optional(
              keyword("IS")
            ),
            choice(
              keyword("AUTO"),
              keyword("DEFAULT"),
              integer()
            )
          )
        );
      }
    
      return callStatement$using$modifier$sizeIsParser;
    }
    
    // ========================================================
    // arg
    // ........................................................
    
    private ParserCombinator callStatement$using$argParser = null;
    
    public final Start callStatement$using$arg = Start.on(getNamespace(), "arg");
    
    public ParserCombinator callStatement$using$arg() {
      if (callStatement$using$argParser == null) {
        FutureParser future = scoped("arg", PUBLIC, true);
        callStatement$using$argParser = future;
        future.setParser(
          choice(
            addressOf(),
            lengthOf(),
            as("omitted",
              keyword("OMITTED")
            ),
            sequence(
              identifier(),
              not(
                moreArithmeticOp()
              )
            ),
            sequence(
              literal(),
              not(
                moreArithmeticOp()
              )
            ),
            arithmeticExpression()
          )
        );
      }
    
      return callStatement$using$argParser;
    }
    
    // ========================================================
    // giving
    // ........................................................
    
    private ParserCombinator callStatement$givingParser = null;
    
    public final Start callStatement$giving = Start.on(getNamespace(), "giving");
    
    public ParserCombinator callStatement$giving() {
      if (callStatement$givingParser == null) {
        FutureParser future = scoped("giving", PUBLIC, true);
        callStatement$givingParser = future;
        future.setParser(
          sequence(
            choice(
              keyword("GIVING"),
              keyword("RETURNING")
            ),
            choice(
              addressOf(),
              sequence(
                optional(
                  keyword("INTO")
                ),
                identifier()
              )
            )
          )
        );
      }
    
      return callStatement$givingParser;
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
            notEmpty(
              sequence(
                optional(
                  as("identificationDivision",
                    notEmpty(
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
                        optional(
                          programIdParagraph()
                        ),
                        optional(
                          replaceStatement()
                        ),
                        optional(
                          optionsParagraph()
                        ),
                        optional(
                          metadata()
                        )
                      )
                    )
                  )
                ),
                optional(
                  environmentDivision()
                ),
                optional(
                  dataDivision()
                ),
                optional(
                  sequence(
                    procedureDivision(),
                    star(
                      as("sourceUnit",
                        programDefinition()
                      )
                    )
                  )
                )
              )
            ),
            optional(
              sequence(
                keyword("END"),
                keyword("PROGRAM"),
                optional(
                  programName()
                ),
                literal(".")
              )
            )
          )
        );
      }
    
      return programDefinitionParser;
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
                functionIdParagraph(),
                optional(
                  optionsParagraph()
                )
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
            functionName(),
            literal(".")
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
            optional(
              literal(".")
            ),
            functionName(),
            optional(
              sequence(
                keyword("AS"),
                literal()
              )
            ),
            optional(
              literal(".")
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
    // moreArithmeticOp
    // ........................................................
    
    private ParserCombinator moreArithmeticOpParser = null;
    
    protected final Start moreArithmeticOp = Start.on(getNamespace(), "moreArithmeticOp");
    
    protected ParserCombinator moreArithmeticOp() {
      if (moreArithmeticOpParser == null) {
        FutureParser future = scoped("moreArithmeticOp", PRIVATE, true);
        moreArithmeticOpParser = future;
        future.setParser(
          choice(
            keyword("B-AND"),
            keyword("B-OR"),
            keyword("B-XOR"),
            keyword("B-EXOR"),
            literal("+"),
            literal("-"),
            sequence(
              literal("*"),
              opt(NOSKIP,
                literal("*")
              )
            ),
            literal("*"),
            literal("/")
          )
        );
      }
    
      return moreArithmeticOpParser;
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
                usingClause$byReference(),
                usingClause$byContent(),
                usingClause$byValue()
              )
            )
          )
        );
      }
    
      return usingClauseParser;
    }
    
    // ========================================================
    // byReference
    // ........................................................
    
    private ParserCombinator usingClause$byReferenceParser = null;
    
    public final Start usingClause$byReference = Start.on(getNamespace(), "byReference");
    
    public ParserCombinator usingClause$byReference() {
      if (usingClause$byReferenceParser == null) {
        FutureParser future = scoped("byReference", PUBLIC, true);
        usingClause$byReferenceParser = future;
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
    
      return usingClause$byReferenceParser;
    }
    
    // ========================================================
    // byContent
    // ........................................................
    
    private ParserCombinator usingClause$byContentParser = null;
    
    public final Start usingClause$byContent = Start.on(getNamespace(), "byContent");
    
    public ParserCombinator usingClause$byContent() {
      if (usingClause$byContentParser == null) {
        FutureParser future = scoped("byContent", PUBLIC, true);
        usingClause$byContentParser = future;
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
    
      return usingClause$byContentParser;
    }
    
    // ========================================================
    // byValue
    // ........................................................
    
    private ParserCombinator usingClause$byValueParser = null;
    
    public final Start usingClause$byValue = Start.on(getNamespace(), "byValue");
    
    public ParserCombinator usingClause$byValue() {
      if (usingClause$byValueParser == null) {
        FutureParser future = scoped("byValue", PUBLIC, true);
        usingClause$byValueParser = future;
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
    
      return usingClause$byValueParser;
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
                anySeparator(),
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
                anySeparator(),
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
                anySeparator(),
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
                anySeparator(),
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
                anySeparator(),
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
                anySeparator(),
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
    // anySeparator
    // ........................................................
    
    private ParserCombinator anySeparatorParser = null;
    
    public final Start anySeparator = Start.on(getNamespace(), "anySeparator");
    
    public ParserCombinator anySeparator() {
      if (anySeparatorParser == null) {
        FutureParser future = scoped("anySeparator", PUBLIC, true);
        anySeparatorParser = future;
        future.setParser(
          sequence(
            tagged(SEPARATOR),
            any()
          )
        );
      }
    
      return anySeparatorParser;
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
    
    // ========================================================
    // eol
    // ........................................................
    
    private ParserCombinator eolParser = null;
    
    public final Start eol = Start.on(getNamespace(), "eol");
    
    public ParserCombinator eol() {
      if (eolParser == null) {
        FutureParser future = scoped("eol", PUBLIC, true);
        eolParser = future;
        future.setParser(
          sequence(
            tagged(END_OF_LINE),
            any()
          )
        );
      }
    
      return eolParser;
    }
    
    // ========================================================
    // somethingFollowingAStatement
    // ........................................................
    
    private ParserCombinator somethingFollowingAStatementParser = null;
    
    public final Start somethingFollowingAStatement = Start.on(getNamespace(), "somethingFollowingAStatement");
    
    public ParserCombinator somethingFollowingAStatement() {
      if (somethingFollowingAStatementParser == null) {
        FutureParser future = scoped("somethingFollowingAStatement", PUBLIC, true);
        somethingFollowingAStatementParser = future;
        future.setParser(
          choice(
            literal("."),
            anyStatement(),
            anyParagraph(),
            anySection(),
            anyDivision(),
            anyLine()
          )
        );
      }
    
      return somethingFollowingAStatementParser;
    }
    
    // ========================================================
    // identifier
    // ........................................................
    
    private ParserCombinator identifierParser = null;
    
    public final Start identifier = Start.on(getNamespace(), "identifier");
    
    public ParserCombinator identifier() {
      if (identifierParser == null) {
        FutureParser future = scoped("identifier", PUBLIC, true);
        identifierParser = future;
        future.setParser(
          choice(
            qualifiedDataName(),
            justAName()
          )
        );
      }
    
      return identifierParser;
    }
    
    // ========================================================
    // qualifiedDataName
    // ........................................................
    
    private ParserCombinator qualifiedDataNameParser = null;
    
    public final Start qualifiedDataName = Start.on(getNamespace(), "qualifiedDataName");
    
    public ParserCombinator qualifiedDataName() {
      if (qualifiedDataNameParser == null) {
        FutureParser future = scoped("qualifiedDataName", PUBLIC, true);
        qualifiedDataNameParser = future;
        future.setParser(
          sequence(
            dataName(),
            optional(
              qualifier()
            ),
            optional(
              sequence(
                literal("("),
                plus(
                  subscript()
                ),
                literal(")")
              )
            )
          )
        );
      }
    
      return qualifiedDataNameParser;
    }
    
    // ========================================================
    // qualifier
    // ........................................................
    
    private ParserCombinator qualifierParser = null;
    
    public final Start qualifier = Start.on(getNamespace(), "qualifier");
    
    public ParserCombinator qualifier() {
      if (qualifierParser == null) {
        FutureParser future = scoped("qualifier", PUBLIC, true);
        qualifierParser = future;
        future.setParser(
          plus(
            sequence(
              choice(
                keyword("IN"),
                keyword("OF")
              ),
              dataName()
            )
          )
        );
      }
    
      return qualifierParser;
    }
    
    // ========================================================
    // subscript
    // ........................................................
    
    private ParserCombinator subscriptParser = null;
    
    public final Start subscript = Start.on(getNamespace(), "subscript");
    
    public ParserCombinator subscript() {
      if (subscriptParser == null) {
        FutureParser future = scoped("subscript", PUBLIC, true);
        subscriptParser = future;
        future.setParser(
          choice(
            literal(),
            dataName(),
            cobolWord()
          )
        );
      }
    
      return subscriptParser;
    }
    
    // ========================================================
    // mnemonicName
    // ........................................................
    
    private ParserCombinator mnemonicNameParser = null;
    
    public final Start mnemonicName = Start.on(getNamespace(), "mnemonicName");
    
    public ParserCombinator mnemonicName() {
      if (mnemonicNameParser == null) {
        FutureParser future = scoped("mnemonicName", PUBLIC, true);
        mnemonicNameParser = future;
        future.setParser(
          identifier()
        );
      }
    
      return mnemonicNameParser;
    }
    
    // ========================================================
    // addressOf
    // ........................................................
    
    private ParserCombinator addressOfParser = null;
    
    public final Start addressOf = Start.on(getNamespace(), "addressOf");
    
    public ParserCombinator addressOf() {
      if (addressOfParser == null) {
        FutureParser future = scoped("addressOf", PUBLIC, true);
        addressOfParser = future;
        future.setParser(
          sequence(
            keyword("ADDRESS"),
            keyword("OF"),
            choice(
              identifier(),
              literal()
            )
          )
        );
      }
    
      return addressOfParser;
    }
    
    // ========================================================
    // lengthOf
    // ........................................................
    
    private ParserCombinator lengthOfParser = null;
    
    public final Start lengthOf = Start.on(getNamespace(), "lengthOf");
    
    public ParserCombinator lengthOf() {
      if (lengthOfParser == null) {
        FutureParser future = scoped("lengthOf", PUBLIC, true);
        lengthOfParser = future;
        future.setParser(
          sequence(
            keyword("LENGTH"),
            keyword("OF"),
            choice(
              identifier(),
              literal()
            )
          )
        );
      }
    
      return lengthOfParser;
    }
    
    // ========================================================
    // arithmeticExpression
    // ........................................................
    
    private ParserCombinator arithmeticExpressionParser = null;
    
    public final Start arithmeticExpression = Start.on(getNamespace(), "arithmeticExpression");
    
    public ParserCombinator arithmeticExpression() {
      if (arithmeticExpressionParser == null) {
        FutureParser future = scoped("arithmeticExpression", PUBLIC, true);
        arithmeticExpressionParser = future;
        future.setParser(
          choice(
            literal(),
            dataName(),
            cobolWord()
          )
        );
      }
    
      return arithmeticExpressionParser;
    }
    
    // ========================================================
    // integer
    // ........................................................
    
    private ParserCombinator integerParser = null;
    
    public final Start integer = Start.on(getNamespace(), "integer");
    
    public ParserCombinator integer() {
      if (integerParser == null) {
        FutureParser future = scoped("integer", PUBLIC, true);
        integerParser = future;
        future.setParser(
          uintgr()
        );
      }
    
      return integerParser;
    }
    
    // ========================================================
    // onOverflow
    // ........................................................
    
    private ParserCombinator onOverflowParser = null;
    
    public final Start onOverflow = Start.on(getNamespace(), "onOverflow");
    
    public ParserCombinator onOverflow() {
      if (onOverflowParser == null) {
        FutureParser future = scoped("onOverflow", PUBLIC, true);
        onOverflowParser = future;
        future.setParser(
          sequence(
            optional(
              keyword("ON")
            ),
            keyword("OVERFLOW"),
            star(
              statement()
            )
          )
        );
      }
    
      return onOverflowParser;
    }
    
    // ========================================================
    // onException
    // ........................................................
    
    private ParserCombinator onExceptionParser = null;
    
    public final Start onException = Start.on(getNamespace(), "onException");
    
    public ParserCombinator onException() {
      if (onExceptionParser == null) {
        FutureParser future = scoped("onException", PUBLIC, true);
        onExceptionParser = future;
        future.setParser(
          sequence(
            optional(
              keyword("ON")
            ),
            keyword("EXCEPTION"),
            star(
              statement()
            )
          )
        );
      }
    
      return onExceptionParser;
    }
    
    // ========================================================
    // notOnException
    // ........................................................
    
    private ParserCombinator notOnExceptionParser = null;
    
    public final Start notOnException = Start.on(getNamespace(), "notOnException");
    
    public ParserCombinator notOnException() {
      if (notOnExceptionParser == null) {
        FutureParser future = scoped("notOnException", PUBLIC, true);
        notOnExceptionParser = future;
        future.setParser(
          sequence(
            keyword("NOT"),
            optional(
              keyword("ON")
            ),
            keyword("EXCEPTION"),
            star(
              statement()
            )
          )
        );
      }
    
      return notOnExceptionParser;
    }
    
    // ========================================================
    // replaceStatement
    // ........................................................
    
    private ParserCombinator replaceStatementParser = null;
    
    public final Start replaceStatement = Start.on(getNamespace(), "replaceStatement");
    
    public ParserCombinator replaceStatement() {
      if (replaceStatementParser == null) {
        FutureParser future = scoped("replaceStatement", PUBLIC, true);
        replaceStatementParser = future;
        future.setParser(
          sequence(
            keyword("REPLACE"),
            choice(
              replaceStatement$replacing(),
              replaceStatement$off()
            ),
            literal(".")
          )
        );
      }
    
      return replaceStatementParser;
    }
    
    // ========================================================
    // replacing
    // ........................................................
    
    private ParserCombinator replaceStatement$replacingParser = null;
    
    public final Start replaceStatement$replacing = Start.on(getNamespace(), "replacing");
    
    public ParserCombinator replaceStatement$replacing() {
      if (replaceStatement$replacingParser == null) {
        FutureParser future = scoped("replacing", PUBLIC, true);
        replaceStatement$replacingParser = future;
        future.setParser(
          sequence(
            optional(
              as("also",
                keyword("ALSO")
              )
            ),
            plus(
              replacementInstruction()
            )
          )
        );
      }
    
      return replaceStatement$replacingParser;
    }
    
    // ========================================================
    // off
    // ........................................................
    
    private ParserCombinator replaceStatement$offParser = null;
    
    public final Start replaceStatement$off = Start.on(getNamespace(), "off");
    
    public ParserCombinator replaceStatement$off() {
      if (replaceStatement$offParser == null) {
        FutureParser future = scoped("off", PUBLIC, true);
        replaceStatement$offParser = future;
        future.setParser(
          sequence(
            optional(
              as("last",
                keyword("LAST")
              )
            ),
            keyword("OFF")
          )
        );
      }
    
      return replaceStatement$offParser;
    }
    
    // ========================================================
    // optionsParagraph
    // ........................................................
    
    private ParserCombinator optionsParagraphParser = null;
    
    public final Start optionsParagraph = Start.on(getNamespace(), "optionsParagraph");
    
    public ParserCombinator optionsParagraph() {
      if (optionsParagraphParser == null) {
        FutureParser future = scoped("optionsParagraph", PUBLIC, true);
        optionsParagraphParser = future;
        future.setParser(
          sequence(
            keyword("OPTIONS"),
            literal("."),
            star(
              choice(
                optionStatement(),
                copyStatement()
              )
            ),
            optional(
              literal(".")
            )
          )
        );
      }
    
      return optionsParagraphParser;
    }
    
    // ========================================================
    // optionStatement
    // ........................................................
    
    private ParserCombinator optionStatementParser = null;
    
    public final Start optionStatement = Start.on(getNamespace(), "optionStatement");
    
    public ParserCombinator optionStatement() {
      if (optionStatementParser == null) {
        FutureParser future = scoped("optionStatement", PUBLIC, true);
        optionStatementParser = future;
        future.setParser(
          plus(
            choice(
              anyWord(),
              anyLiteral(),
              anySeparator(),
              anyWhitespace()
            )
          )
        );
      }
    
      return optionStatementParser;
    }
    
    // ========================================================
    // metadata
    // ........................................................
    
    private ParserCombinator metadataParser = null;
    
    public final Start metadata = Start.on(getNamespace(), "metadata");
    
    public ParserCombinator metadata() {
      if (metadataParser == null) {
        FutureParser future = scoped("metadata", PUBLIC, true);
        metadataParser = future;
        future.setParser(
          sequence(
            keyword("METADATA"),
            literal("."),
            star(
              choice(
                metadataStatement(),
                copyStatement()
              )
            ),
            optional(
              literal(".")
            )
          )
        );
      }
    
      return metadataParser;
    }
    
    // ========================================================
    // metadataStatement
    // ........................................................
    
    private ParserCombinator metadataStatementParser = null;
    
    public final Start metadataStatement = Start.on(getNamespace(), "metadataStatement");
    
    public ParserCombinator metadataStatement() {
      if (metadataStatementParser == null) {
        FutureParser future = scoped("metadataStatement", PUBLIC, true);
        metadataStatementParser = future;
        future.setParser(
          plus(
            choice(
              anyWord(),
              anyLiteral(),
              anySeparator(),
              anyWhitespace()
            )
          )
        );
      }
    
      return metadataStatementParser;
    }
    
    // ========================================================
    // sourceUnit
    // ........................................................
    
    private ParserCombinator sourceUnitParser = null;
    
    public final Start sourceUnit = Start.on(getNamespace(), "sourceUnit");
    
    public ParserCombinator sourceUnit() {
      if (sourceUnitParser == null) {
        FutureParser future = scoped("sourceUnit", PUBLIC, true);
        sourceUnitParser = future;
        future.setParser(
          choice(
            programDefinition(),
            functionDefinition(),
            statement(),
            anyStatement(),
            anyParagraph(),
            anySection(),
            anyDivision(),
            anyLine()
          )
        );
      }
    
      return sourceUnitParser;
    }
    
}
