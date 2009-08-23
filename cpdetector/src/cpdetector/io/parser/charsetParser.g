header{
package cpdetector.io.parser;
}
options{
//		language="java";	 
}

class EncodingParser extends Parser;
options{
		k=1;
		/*
		If omitted, a RecognitionException of rule section will 
		be caught, reported (System.err) and advanced to the next 
		section... . If something is wrong, it makes no sense still 
		trying to parse (my opinion).
		*/
		
		defaultErrorHandler=false;
		
}

htmlDocument returns[String charset] 
	:
		(token1:META_CONTENT_TYPE) {charset=token1.getText();}
		| (token2:XML_ENCODING_DECL) {charset=token2.getText();}
		| {charset=null;}
	;
 

class EncodingLexer extends Lexer;
options{
	codeGenDebug=false; 
	k=10;
	charVocabulary='\u0000'..'\uFFFE';
	genHashLines=true;
	caseSensitive= false;
	/*
	With introduction of a 2nd token (XML) in version 1.01 the 
	underlying CharBuffer seems to skip when filtering: 
	"meta http-...=" was read as "me="
	*/
	filter = true;
}

// <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
// Clever: SPACING is newline or whitespaces and ignored here.
META_CONTENT_TYPE options{ignore=SPACING;} 
	:
	'<'!
	"meta"! 
	"http-equiv"! 
	'='! 
	('\"'!)? 
	"content-type"! 
	('\"'!)? 
	
	"content"! 
	'='! 
	('\"'!)? 
	(LETTER!|DIGIT!|'/'!)* 
	';'! 
	
	"charset"! 
	'='! 
	(LETTER|DIGIT|'-'|'_')* 
	('\"'!)? 
	;
/*
taken from:
http://www.w3.org/TR/2004/REC-xml-20040204/#NT-prolog
Not insisting on 1.1, as e.g. XMLDecl is mandatory there, which would 
disallow to parse xml 1.0. The optionality makes it work with both. 

[1]     document      ::=   prolog element Misc*
[22]   	prolog        ::=   XMLDecl? Misc* (doctypedecl Misc*)?
[23]   	XMLDecl       ::=   '<?xml' VersionInfo EncodingDecl? SDDecl? S? '?>'
[24]   	VersionInfo	  ::=   S 'version' Eq ("'" VersionNum "'" | '"' VersionNum '"')
[3]   	S             ::=   (#x20 | #x9 | #xD | #xA)+ // this is space, cr,lf,tab (not in that order).
[25]   	Eq            ::=   S? '=' S?
[26]   	VersionNum    ::=   '1.0' // I'll open up to digit'.'digit, as 1.1 is out quite a while.
[80]   	EncodingDecl  ::=   S 'encoding' Eq ('"' EncName '"' | "'" EncName "'" )
[81]   	EncName       ::=   [A-Za-z] ([A-Za-z0-9._] | '-')* // sounds familiar (java identifier), but look at the fuzzy range-notation in combination with the 'or'.

All these rules are compressed to the following one that searches for EncName (performance).
EncName is matched with IDENTIFIER, all others are ignored for token text '!'.
Note, that spacings are possible around all literals.

10/18/04:
External Parsed Entities may be found often in form of DTD's. 
They follow a stripped syntax of xmlDecl: TextDecl.  
http://www.w3.org/TR/2004/REC-xml-20040204/#TextEntities

[77]   	TextDecl	   ::=   	'<?xml' VersionInfo? EncodingDecl S? '?>'

While VersionInfo becomes optional, EncodingDecl is mandatory now (compare 
with XMLDecl above. This parser is modified in that rule: versionInfo
is made optional which allow to formulate only one production 
for XMLDecl and TextDecl, which is sufficient for our purpose: find the 
encoding. The encodingDecl (optional in TextDecl) is kept mandatory, as 
it is, what we search for. If not given, the whole token will not be matched 
and it is simply not found (easier to handle than checking for empty token text).
The other way: using two productions would introduce lexical 
ambiguity for the complete length of XMLDecl -> The parser would need 
a large lookahead. Unlimited lookahead, as S? may be unlimted spacing!!!
*/


XML_ENCODING_DECL options{ignore=SPACING;}
	:
	// -> prolog
	// -> XMLDecl?
	"<?xml"!
	(
    	// -> VersionInfo
    	"version"! 
    	// <-> Eq
    	"="!
    	// <- VersionInfo
    	// -> VersionNum
    	// <- VersionInfo
    	(
    	  "'"! DIGIT! '.'! DIGIT! "'"!
    	  |
    	  '"'! DIGIT! '.'! DIGIT! '"'!
    	 )
    )?
     // <- XMLDecl
     // -> EncodingDecl
     "encoding"! 
     // <-> Eq
     "="! 
     // <-> EncName
    (
      "'"! IDENTIFIER "'"!
      |
      '"'! IDENTIFIER '"'!
     )
     ;


protected 
IDENTIFIER
    :
    LETTER
    (LETTER|DIGIT|'_'|'.'|'-')*
    ;
         
protected 	
SPACING
	: 
	NEWLINE
	| SPACE
	;
// UNIX and Windows
protected
NEWLINE
	: '\n' { newline(); }
	| '\r' '\n' { newline(); }
	; 

protected
SPACE: 
	' '
	;

protected
DIGIT
	: '0'..'9'
	;
	
protected 
LETTER 
	: 'a'..'z'
	;

