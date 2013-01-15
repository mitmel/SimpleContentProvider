yacc=bison

all: src/edu/mit/mobile/android/content/query/QuerystringParser.java
%.java: %.y
	$(yacc) -o $@ $<
