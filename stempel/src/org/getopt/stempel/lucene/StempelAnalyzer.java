/**
 * Copyright 2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.getopt.stempel.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.getopt.stempel.Stemmer;

/**
 * Analyzer that uses Stemmer for stemming. It also uses LowerCaseTokenizer to
 * lowercase input tokens.
 * 
 * @author Andrzej Bialecki &lt;ab@getopt.org&gt;
 */
public class StempelAnalyzer extends Analyzer {

  private Stemmer stemmer = null;

  /** Creates the Analyzer that uses the default stemming table */
  public StempelAnalyzer() {
    stemmer = new Stemmer();
  }

  /** Creates the Analyzer that loads stemming table from the resource path. */
  public StempelAnalyzer(String stemmerTable) {
    stemmer = new Stemmer(stemmerTable);
  }

  /** Creates the Analyzer that uses the supplied Stemmer. */
  public StempelAnalyzer(Stemmer stemmer) {
    this.stemmer = stemmer;
  }

  /**
   * @see org.apache.lucene.analysis.Analyzer#tokenStream(java.lang.String,
   *      java.io.Reader)
   */
  public final TokenStream tokenStream(String fieldName, Reader reader) {
    return new StempelFilter(stemmer, new LowerCaseTokenizer(reader));
  }
}