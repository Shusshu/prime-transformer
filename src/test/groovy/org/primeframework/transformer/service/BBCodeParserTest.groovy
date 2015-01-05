/*
 * Copyright (c) 2015, Inversoft Inc., All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.primeframework.transformer.service

import org.testng.Assert
import org.testng.annotations.Test

import static org.primeframework.transformer.service.ParserAssert.assertParse

/**
 * Tests the BBCode parser using the ParserAssert DSL.
 *
 * @author Brian Pontarelli
 */
class BBCodeParserTest {
  @Test
  void edgeCase_BadNesting() {
    Assert.fail("Need to figure out handling")
    assertParse("abc[list][[[def[/list]") {
      TextNode(body: "abc", start: 0, end: 3)
      TagNode(name: "list", start: 3, nameEnd: 8, bodyBegin: 9, bodyEnd: 15, end: 22) {
        TextNode(body: "[[[def", start: 9, end: 15)
      }
    }
  }

  @Test
  void edgeCase_Code() {
    assertParse("char[] ca = new char[1024]") {
      TextNode(body: "char", start: 0, end: 4)
      TextNode(body: "[] ca = new char", start: 4, end: 20)
      TextNode(body: "[1024]", start: 20, end: 26)
    }
  }

  @Test
  void edgeCase_SingleBracket() {
    assertParse("abc[def") {
      TextNode(body: "abc", start: 0, end: 3)
      TextNode(body: "[def", start: 3, end: 7)
    }
  }

  @Test
  void edgeCase_MultipleBrackets() {
    assertParse("abc[[[def") {
      TextNode(body: "abc", start: 0, end: 3)
      TextNode(body: "[[[def", start: 3, end: 9)
    }
  }

  @Test
  void edgeCase_GoodStart_ThenOpen() {
    Assert.fail("Need to figure out handling")
    assertParse("[foo][def") {
      TextNode(body: "[foo][def", start: 0, end: 9)
    }
  }

  @Test
  void edgeCase_GoodTag_ThenOpen() {
    assertParse("[b]foo[/b] abc[def") {
      TagNode(name: "b", start: 0, nameEnd: 2, bodyBegin: 3, bodyEnd: 6, end: 10) {
        TextNode(body: "foo", start: 3, end: 6)
      }
      TextNode(body: " abc", start: 10, end: 14)
      TextNode(body: "[def", start: 14, end: 18)
    }
  }

  @Test
  void edgeCase_OnlyClosingTag() {
    assertParse("[/b] foo") {
      TextNode(body: "[/b] foo", start: 0, end: 8)
    }
  }

  @Test
  void edgeCase_BadTagName() {
    Assert.fail("Need to figure out handling")
    assertParse("[b[b[b[b] foo") {
      TextNode(body: "[b[b[b[b] foo", start: 0, end: 13)
    }
  }

  @Test
  void edgeCase_TagOpeningEndBracketInAttributeWithQuote() {
    assertParse("[font size=']']foo[/font]") {
      TagNode(name: "font", start: 0, nameEnd: 5, attributesBegin: 6, bodyBegin: 15, bodyEnd: 18, end: 25,
              attributes: [size: "]"]) {
        TextNode(body: "foo", start: 15, end: 18)
      }
    }
  }

  @Test
  void edgeCase_TagOpeningEndBracketInAttributeWithoutQuote() {
    Assert.fail("Need to figure out handling")
    assertParse("[font size=]]foo[/font]") {
      TagNode(name: "font", start: 0, nameEnd: 5, attributesBegin: 6, bodyBegin: 13, bodyEnd: 16, end: 23,
              attributes: [size: ""]) {
        TextNode(body: "foo", start: 13, end: 16)
      }
    }
  }

  @Test
  void edgeCase_MismatchedOpenAndCloseTags() {
    Assert.fail("Need to figure out handling")
    assertParse("[font size=12]foo[/b]") {
      TagNode(name: "font", start: 0, nameEnd: 5, attributesBegin: 6, bodyBegin: 14, bodyEnd: 21, end: 21,
              attributes: [size: "12"]) {
        TextNode(body: "foo[/b]", start: 14, end: 21)
      }
    }
  }

  @Test
  void edgeCase_CloseTagNoName() {
    Assert.fail("Need to figure out handling")
    assertParse("[font size=12]foo[/]") {
      TagNode(name: "font", start: 0, nameEnd: 5, attributesBegin: 6, bodyBegin: 14, bodyEnd: 17, end: 20,
              attributes: [size: "12"]) {
        TextNode(body: "foo", start: 14, end: 17)
      }
    }
  }

  @Test
  void edgeCase_BadAttributeName() {
    Assert.fail("Need to figure out handling")
    assertParse("[font ,;_!=;_l;!]foo[/font]") {
      TagNode(name: "font", start: 0, nameEnd: 5, attributesBegin: 6, bodyBegin: 17, bodyEnd: 20, end: 27,
              attributes: [",;_!": ";_l;!"]) {
        TextNode(body: "foo", start: 17, end: 20)
      }
    }
  }

  @Test
  void edgeCase_BadAttributeNameWithQuote() {
    Assert.fail("Need to figure out handling")
    assertParse("[font ,;'!=;l;!]foo[/font]") {
      TagNode(name: "font", start: 0, nameEnd: 5, attributesBegin: 6, bodyBegin: 16, bodyEnd: 19, end: 26,
              attributes: [",;'!": ";l;!"]) {
        TextNode(body: "foo", start: 16, end: 19)
      }
    }
  }

  @Test
  void edgeCase_BadAttributeValueWithQuote() {
    Assert.fail("Need to figure out handling")
    assertParse("[font foo=values{'bar'}]foo[/font]") {
      TagNode(name: "font", start: 0, nameEnd: 5, attributesBegin: 6, bodyBegin: 24, bodyEnd: 27, end: 34,
              attributes: ["foo": "values{'bar'}"]) {
        TextNode(body: "foo", start: 24, end: 27)
      }
    }
  }

  @Test
  void edgeCase_BadAttributeValueWithMismatchedQuotes() {
    Assert.fail("Need to figure out handling")
    assertParse("[font foo='bar\"]foo[/font]") {
      TagNode(name: "font", start: 0, nameEnd: 5, attributesBegin: 6, bodyBegin: 16, bodyEnd: 19, end: 26,
              attributes: ["foo": "bar"]) {
        TextNode(body: "foo", start: 16, end: 19)
      }
    }
  }

  @Test
  void edgeCase_AttributeValueWithQuotes() {
    assertParse("[font foo='\"bar\"']foo[/font]") {
      TagNode(name: "font", start: 0, nameEnd: 5, attributesBegin: 6, bodyBegin: 18, bodyEnd: 21, end: 28,
              attributes: ["foo": "\"bar\""]) {
        TextNode(body: "foo", start: 18, end: 21)
      }
    }
  }

  @Test
  void edgeCase_AttributeNameWithQuotes() {
    assertParse("[font attr{'foo'}='bar']foo[/font]") {
      TagNode(name: "font", start: 0, nameEnd: 5, attributesBegin: 6, bodyBegin: 24, bodyEnd: 27, end: 34,
              attributes: ["attr{'foo'}": "bar"]) {
        TextNode(body: "foo", start: 24, end: 27)
      }
    }
  }

  @Test
  void edgeCase_SimpleAttributeNoQuotes() {
    assertParse("[font=12]foo[/font]") {
      TagNode(name: "font", start: 0, nameEnd: 5, attributesBegin: 6, bodyBegin: 9, bodyEnd: 12, end: 19,
              attribute: "12") {
        TextNode(body: "foo", start: 9, end: 12)
      }
    }
  }

  @Test
  void edgeCase_SimpleAttributeQuotesContainsBracket() {
    Assert.fail("Need to figure out handling")
    assertParse("[font='values[\"size\"]']foo[/font]") {
      TagNode(name: "font", start: 0, nameEnd: 5, attributesBegin: 6, bodyBegin: 23, bodyEnd: 26, end: 33,
              attribute: "values[\"size\"]") {
        TextNode(body: "foo", start: 23, end: 26)
      }
    }
  }

  @Test
  void edgeCase_SimpleAttributeQuotesInside() {
    Assert.fail("Need to figure out handling")
    assertParse("[font=values{'12'}]foo[/font]") {
      TagNode(name: "font", start: 0, nameEnd: 5, attributesBegin: 6, bodyBegin: 19, bodyEnd: 22, end: 29,
              attribute: "values{'12'}") {
        TextNode(body: "foo", start: 19, end: 22)
      }
    }
  }

  @Test
  void edgeCase_noparseEmbeddedNoParse() {
    assertParse("[noparse]Example: [noparse]foo[/noparse][/noparse]", [[0, 9], [40, 10]]) {
      TagNode(name: "noparse", start: 0, nameEnd: 8, attributesBegin: -1, bodyBegin: 9, bodyEnd: 40, end: 48) {
        TextNode(body: "Example: [noparse]foo[/noparse]", start: 9, end: 40)
      }
    }
  }

  @Test
  public void offsets() {
    assertParse("z [b]abc defg [/b]hijk [ul][*]lmn opqr[*][/ul]",
                [[2, 3], [14, 4], [23, 4], [27, 3], [38, 3], [41, 5]]) {
      TextNode(body: "z ", start: 0, end: 2)
      TagNode(name: "b", start: 2, nameEnd: 4, bodyBegin: 5, bodyEnd: 14, end: 18) {
        TextNode(body: "abc defg ", start: 5, end: 14)
      }
      TextNode(body: "hijk ", start: 18, end: 23)
      TagNode(name: "ul", start: 23, nameEnd: 26, bodyBegin: 27, bodyEnd: 41, end: 46) {
        TagNode(name: "*", start: 27, nameEnd: 29, bodyBegin: 30, bodyEnd: 38, end: 38) {
          TextNode(body: "lmn opqr", start: 30, end: 38)
        }
        TagNode(name: "*", start: 38, nameEnd: 40, bodyBegin: 41, bodyEnd: 41, end: 41)
      }
    }

    assertParse(
        "Example [code type=\"see the java oo\" syntax=\"java\"] System.out.println(\"Hello World!\"); [/code] ",
        [[8, 43], [88, 7]]) {
      TextNode(body: "Example ", start: 0, end: 8)
      TagNode(name: "code", start: 8, nameEnd: 13, attributesBegin: 14, bodyBegin: 51, bodyEnd: 88, end: 95,
              attributes: ["type": "see the java oo", "syntax": "java"]) {
        TextNode(body: " System.out.println(\"Hello World!\"); ", start: 51, end: 88)
      }
      TextNode(body: " ", start: 95, end: 96)
    }
  }
}
