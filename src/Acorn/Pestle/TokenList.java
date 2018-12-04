package Acorn.Pestle;

import Acorn.AcornError;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.function.Predicate;

public class TokenList extends ArrayList<Token> {
  static final long serialVersionUID = 0xbe4dead;
  Predicate<Token> isWhiteSpace = (Token t) -> t.type == TokenTypes.space;
  public void verifyParens() {
    int lParenCount = 0;
    int rParenCount = 0;
    for (Token currentToken : this) {
      if (currentToken.type == TokenTypes.parenL) {
        lParenCount++;
      } else if (currentToken.type == TokenTypes.parenR) {
        rParenCount++;
      }
    }
    if (lParenCount > rParenCount) {
      throw new AcornError("Too many left parens!");
    }
    if (rParenCount > lParenCount) {
      throw new AcornError("Too many right parens!");
    }
  }
  public int findMatchingParen(int indexOfCurrentParen) {
    int l = this.size();
    int parenScope = 0;
    int matchingParenIndex = -1;
    for (int i = indexOfCurrentParen; i < l; i++) {
      Token currentToken = this.get(i);
      if (currentToken.type == TokenTypes.parenL) {
        parenScope++;
        continue;
      }
      if (currentToken.type == TokenTypes.parenR) {
        parenScope--;
      }
      if (parenScope == 0) {
        matchingParenIndex = i;
        break;
      }
    }
    return matchingParenIndex;
  }
  public TokenAndPosition findFromIndex(int start, Predicate<Token> test) {
    int l = this.size();
    for (int i = start; i < l; i++) {
      Token current = this.get(i);
      if (test.test(current)) {
        return new TokenAndPosition(current, i);
      }
      if (current.type == TokenTypes.parenL) {
        i = this.findMatchingParen(i);
        continue;
      }
    }
    return new TokenAndPosition(Token.NA, -1);
  }
  public TokenList slice(int start) {
    return this.slice(start, this.size());
  }
  public TokenList slice(int start, int end) {
    TokenList a = new TokenList();
    a.addAll(this.subList(start, end));
    return a;
  }
  public BinopCollection binopSplit(int binopIndex) {
    return new BinopCollection(
      this.slice(0, binopIndex),
      this.get(binopIndex),
      this.slice(binopIndex + 1)
    );
  }
  public void stripWhiteSpace() {
    this.removeIf(this.isWhiteSpace);
  }
  public void shiftSOF() {
    if (this.get(0).type == TokenTypes.sof) {
      this.remove(0);
    }
  }
  @Override
  public String toString() {
    String res = "";
    int s = this.size();
    for (int i = 0; i < s; i++) {
      Token current = this.get(i);
      res += i + ": " + current + ",\n";
    }
    return res;
  }
};
