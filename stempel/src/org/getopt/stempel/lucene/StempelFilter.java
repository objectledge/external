/**
 * Copyright 2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.getopt.stempel.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.getopt.stempel.Stemmer;

/**
 * Transforms the token stream as per the stemming algorithm. Note: the input to
 * the stemming filter must already be in lower case, so you will need to use
 * LowerCaseFilter or LowerCaseTokenizer farther down the Tokenizer chain in
 * order for this to work properly!
 * 
 * @author Andrzej Bialecki &lt;ab@getopt.org&gt;
 */
public class StempelFilter extends TokenFilter {

    private Stemmer stemmer = null;

    /** Create filter using the default stemming table.
     *
     * @param in input token stream
     */
    public StempelFilter(TokenStream in) {
        super(in);
        stemmer = new Stemmer();
    }

    /** Create filter using the supplied stemming table.
     * 
     * @param stemmer stemmer
     * @param in input token stream
     */
    public StempelFilter(Stemmer stemmer, TokenStream in) {
        super(in);
        this.stemmer = stemmer;
    }

    /** Returns the next input Token, after being stemmed */
    public final Token next() throws IOException {
        Token token = input.next();
        if (token == null)
            return null;
        else {
            String s = stemmer.stem(token.termText(), true);
            if (!s.equals(token.termText())) {
                // reconstruct the input token. This is silly...
                Token res = new Token(s, token.startOffset(),
                        token.endOffset(), token.type());
                res.setPositionIncrement(token.getPositionIncrement());
                return res;
            }
            return token;
        }
    }
}