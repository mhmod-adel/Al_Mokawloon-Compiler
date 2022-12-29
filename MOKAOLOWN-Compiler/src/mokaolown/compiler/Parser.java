package mokaolown.compiler;

import java.io.IOException;

public class Parser {

    private Token currentToken = null;
    private Lexer lexer = null;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }
    private void Error(String msg) throws Exception {
        throw new Exception(msg);
    }
    public Token eat(TokenType type) throws Exception {

        Token current = null;
        if (currentToken.type == type) {
            current =  currentToken;
            try {
                currentToken = lexer.getNextToken();
            } catch (IOException e) {
                Error("Unexpected Token " );
            }
        } else {
            Error("Unexpected Token " );
        }
        return current;
    }



    public Token getNextToken() throws Exception {
        try {
            if (currentToken == null) {
                currentToken = lexer.getNextToken();
            }
            return currentToken;
        } catch (IOException e) {
            Error(e.getMessage());
            return null;
        }
    }

    // S -> TABLE $
    public AST.Exper S() throws Exception {
        AST.Exper root = null;
        Token token = getNextToken();
        switch (token.type) {
            case OT -> {
                root = TABLE();
                eat(TokenType.EOF);
            }
            default -> Error("Unexpected Token "  );
        }
        return root;
    }

    // TABLE -> OP ID CL ROWS OPS ID CL
    public AST.Exper TABLE() throws Exception {
        AST.Exper root = null, rows;
        String id;
        Token token = getNextToken();
        switch (token.type) {
            case OT -> {
                eat(TokenType.OT);
                id = eat(TokenType.IDENTIFIER).text;
                eat(TokenType.CT);
                rows = ROWS();
                eat(TokenType.OTS);
                eat(TokenType.IDENTIFIER);
                eat(TokenType.CT);
                root = new AST.TableExper(id, rows);
            }
            default -> Error("Unexpected Token "  );
        }
        return root;
    }

    // ROWS -> ROW ROW_PRIME
    public AST.Exper ROWS() throws Exception {
        AST.Exper root = null, left;
        Token token = getNextToken();
        switch (token.type) {
            case OT -> {
                left = ROW();
                root = ROW_PRIME(left);
            }
            default -> Error("Unexpected Token "  );
        }
        return root;
    }

    // ROW -> OT ID ATTRIBUTES CTS
    public AST.Exper ROW() throws Exception {
        AST.Exper root = null, att = null;
        Token token = getNextToken();
        String id;
        switch (token.type) {
            case OT -> {
                eat(TokenType.OT);
                id = eat(TokenType.IDENTIFIER).text;
                att = ATTRIBUTES();
                eat(TokenType.CTS);
                root = new AST.RowExper(id, att);
            }
            default -> Error("Unexpected Token "  );
        }
        return root;
    }

    // ROW_PRIME -> ROWS | λ
    // ROW_PRIME -> ROW ROW' | λ
    public AST.Exper ROW_PRIME(AST.Exper left) throws Exception {
        AST.Exper root = left, right;
        Token token = getNextToken();
        switch (token.type) {
            case OT -> {
                right = ROW();
                root = ROW_PRIME(right);
                root = new AST.RowPrimeExper(left, root);
            }
            case OTS -> {
            }
            default -> Error("Unexpected Token "  );
        }
        return root;
    }

    // ATTRIBUTES -> ATTRIBUTE ATTRIBUTE_PRIME
    public AST.Exper ATTRIBUTES() throws Exception {
        AST.Exper root = null;
        AST.Exper left;
        Token token = getNextToken();
        switch (token.type) {
            case IDENTIFIER -> {
                left = ATTRIBUTE();
                root = ATTRIBUTE_PRIME(left);
            }
            default -> Error("Unexpected Token "  );
        }
        return root;
    }

    // ATTRIBUTE -> ID EQ VAL
    public AST.Exper ATTRIBUTE() throws Exception {
        AST.Exper root = null;
        AST.Exper val;
        String id;
        Token token = getNextToken();
        switch (token.type) {
            case IDENTIFIER -> {
                id = eat(TokenType.IDENTIFIER).text;
                eat(TokenType.EQ);
                val = VAL();
                root = new AST.AttributeExper(id, val);
            }
            default -> Error("Unexpected Token "  );
        }
        return root;
    }

    // ATTRIBUTE_PRIME -> COMMA ATTRIBUTES | λ
    public AST.Exper ATTRIBUTE_PRIME(AST.Exper left) throws Exception {
        AST.Exper root = left;
        Token token = getNextToken();
        switch (token.type) {
            case COMMA -> {
                eat(TokenType.COMMA);
                root = ATTRIBUTES();
                root = new AST.AttributesExper(left, root);
            }
            case CTS -> {
            }
            default -> Error("Unexpected Token "  );
        }
        return root;
    }


    // VAL -> NUM | QUOTEDSTRING
    public AST.Exper VAL() throws Exception {
        AST.Exper root = null;
        Token token = getNextToken();
        switch (token.type) {
            case NUMBER -> {
                eat(TokenType.NUMBER);
                root = new AST.ValExper(token.text, "int");
            }
            case QUOTEDSTRING -> {
                eat(TokenType.QUOTEDSTRING);
                root = new AST.ValExper(token.text, "varchar(225)");
            }
            default -> Error("Unexpected Token "  );
        }
        return root;
    }

}
