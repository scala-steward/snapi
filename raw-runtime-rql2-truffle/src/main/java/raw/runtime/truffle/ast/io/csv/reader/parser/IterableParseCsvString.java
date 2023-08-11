/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.runtime.truffle.ast.io.csv.reader.parser;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.runtime.exceptions.csv.CsvParserRawTruffleException;
import raw.runtime.truffle.runtime.iterable.sources.CsvFromStringCollection;
import raw.runtime.truffle.runtime.list.StringList;
import raw.runtime.truffle.runtime.option.OptionLibrary;

@NodeInfo(shortName = "IterableParseCsvString")
public class IterableParseCsvString extends ExpressionNode {

    @Child
    private DirectCallNode childDirectCall;
    @Child
    private ExpressionNode strExp;
    @Child
    private ExpressionNode skip;
    @Child
    private ExpressionNode delimiterExp;
    @Child
    private ExpressionNode quoteExp;
    @Child
    private ExpressionNode nullsExp;
    @Child
    private ExpressionNode nansExp;
    @Child
    private ExpressionNode dateFormatExp;
    @Child
    private ExpressionNode timeFormatExp;
    @Child
    private ExpressionNode datetimeFormatExp;

    private final OptionLibrary options = OptionLibrary.getFactory().getUncached();

    public IterableParseCsvString(ExpressionNode strExp, ExpressionNode skipExp, ExpressionNode delimiterExp, ExpressionNode quoteExp, ProgramExpressionNode columnParser,
                                  ExpressionNode nullsExp, ExpressionNode nansExp, ExpressionNode dateFormatExp, ExpressionNode timeFormatExp, ExpressionNode datetimeFormatExp) {
        this.childDirectCall = DirectCallNode.create(columnParser.getCallTarget());
        this.strExp = strExp;
        this.skip = skipExp;
        this.delimiterExp = delimiterExp;
        this.quoteExp = quoteExp;
        this.nullsExp = nullsExp;
        this.nansExp = nansExp;
        this.dateFormatExp = dateFormatExp;
        this.timeFormatExp = timeFormatExp;
        this.datetimeFormatExp = datetimeFormatExp;
    }

    public Object executeGeneric(VirtualFrame frame) {
        try {
            String str = strExp.executeString(frame);
            int skipValue = skip.executeInt(frame);
            String delimiterValue = delimiterExp.executeString(frame);
            Object quoteValue = quoteExp.executeGeneric(frame);
            char quoteChar = 0;
            boolean useQuote = false;
            if (options.isDefined(quoteValue)) {
                String quoteCharString = (String) options.get(quoteValue);
                if (quoteCharString.length() > 0) {
                    useQuote = true;
                    quoteChar = quoteCharString.charAt(0);
                }
            }
            String[] nulls = ((StringList) nullsExp.executeGeneric(frame)).getInnerList();
            String[] nans = ((StringList) nansExp.executeGeneric(frame)).getInnerList();
            String dateFormat = dateFormatExp.executeString(frame);
            String timeFormat = timeFormatExp.executeString(frame);
            String datetimeFormat = datetimeFormatExp.executeString(frame);
            RawTruffleCsvParserSettings settings = new RawTruffleCsvParserSettings(
                    delimiterValue.charAt(0), useQuote, quoteChar, skipValue, nulls, nans, dateFormat, timeFormat, datetimeFormat
            );
            return new CsvFromStringCollection(str, childDirectCall, settings);
        } catch (UnexpectedResultException ex) {
            throw new CsvParserRawTruffleException(ex.getMessage(), 0, 0, ex, this);
        }
    }

}
