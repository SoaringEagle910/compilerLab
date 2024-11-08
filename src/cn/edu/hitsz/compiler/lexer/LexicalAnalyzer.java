package cn.edu.hitsz.compiler.lexer;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * TODO: 实验一: 实现词法分析
 * <br>
 * 你可能需要参考的框架代码如下:
 *
 * @see Token 词法单元的实现
 * @see TokenKind 词法单元类型的实现
 */
public class LexicalAnalyzer {
    private final SymbolTable symbolTable;
    private String inputCode;
    private List<Token> tokens = new ArrayList<>();

    public LexicalAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }


    /**
     * 从给予的路径中读取并加载文件内容
     *
     * @param path 路径
     */
    public void loadFile(String path) {
        // TODO: 词法分析前的缓冲区实现
        // 可自由实现各类缓冲区
        // 或直接采用完整读入方法
        inputCode = FileUtils.readFile(path);
        return;
    }

    /**
     * 执行词法分析, 准备好用于返回的 token 列表 <br>
     * 需要维护实验一所需的符号表条目, 而得在语法分析中才能确定的符号表条目的成员可以先设置为 null
     */
    public void run() {
        // TODO: 自动机实现的词法分析过程
        int pos = 0, q = 0;
        StringBuilder word = new StringBuilder();
        for(pos = 0; pos < inputCode.length(); pos++){
            char ch = inputCode.charAt(pos);
            switch (q){
                case 0 :{
                    if(ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r'){
                        continue;
                    }
                    else if(ch >= 'A' && ch <= 'z'){
                        word.append(ch);
                        q = 1;
                    }
                    else if(ch >= '0' && ch <= '9'){
                        word.append(ch);
                        q = 2;
                    }
                    else if(ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '(' || ch == ')' || ch == '{' || ch == '}' || ch == '[' || ch == ']' || ch == ',' || ch == ';' || ch == '.' || ch == ':' || ch == '<' || ch == '>' || ch == '=' || ch == '!' || ch == '&' || ch == '|' || ch == '^' || ch == '%' || ch == '#' || ch == '@' || ch == '?'){
                        if(ch ==';')word.append("Semicolon");
                        else word.append(ch);
                        q = 3;
                        pos--;
                    }
                    else {
                        System.out.println("Lexical error: " + ch);
                    }
                    break;
                }
                case 1: {
                    if ((ch >= 'A' && ch <= 'z') || (ch >= '0' && ch <= '9')) {
                        word.append(ch);
                    } else {
                        // System.out.println(word.toString());
                        if (TokenKind.isAllowed(word.toString())) {
                            //tokens.add(Token.normal(TokenKind.fromString(word.toString()), word.toString()));
                            tokens.add(Token.simple(word.toString()));
                        } else {
                            tokens.add(Token.normal("id",word.toString()));
                            if(!symbolTable.has(word.toString()))symbolTable.add(word.toString());
                        }
                        word.setLength(0);
                        pos--;
                        q = 0;
                    }
                    break;
                }
                case 2: {
                    if (ch >= '0' && ch <= '9') {
                        word.append(ch);
                    } else {
                        // System.out.println(word.toString());
                        tokens.add(Token.normal("IntConst", word.toString()));
                        word.setLength(0);
                        pos--;
                        q = 0;
                    }
                    break;
                }
                case 3: {
                    // System.out.println(word.toString());
                    if(word.toString().charAt(0) == '=' || word.toString().charAt(0) == ',' || word.toString().equals( "Semicolon")
                            || word.toString().charAt(0) == '+' || word.toString().charAt(0) == '-' || word.toString().charAt(0) == '*'
                            || word.toString().charAt(0) == '/' || word.toString().charAt(0) == '(' || word.toString().charAt(0) == ')')
                        tokens.add(Token.simple(word.toString()));
                    else {
                        System.out.println("Lexical error: " + word.toString());
                    }
                    word.setLength(0);
                    q = 0;
                    break;
                }
            }
        }
        tokens.add(Token.eof());
    }

    /**
     * 获得词法分析的结果, 保证在调用了 run 方法之后调用
     *
     * @return Token 列表
     */
    public Iterable<Token> getTokens() {
        // TODO: 从词法分析过程中获取 Token 列表
        // 词法分析过程可以使用 Stream 或 Iterator 实现按需分析
        // 亦可以直接分析完整个文件
        // 总之实现过程能转化为一列表即可
        return tokens;
    }

    public void dumpTokens(String path) {
        FileUtils.writeLines(
            path,
            StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList()
        );
    }


}
