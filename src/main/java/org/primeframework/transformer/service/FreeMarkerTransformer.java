/*
 * Copyright (c) 2014, Inversoft Inc., All Rights Reserved
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

package org.primeframework.transformer.service;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.primeframework.transformer.domain.Document;
import org.primeframework.transformer.domain.Node;
import org.primeframework.transformer.domain.TagNode;
import org.primeframework.transformer.domain.TextNode;
import org.primeframework.transformer.domain.TransformerException;

import freemarker.template.Template;

/**
 * FreeMarker transformer implementation.
 */
public class FreeMarkerTransformer implements Transformer {

    public Map<String, Template> templates = new HashMap<>();

    private String newLine;

    public FreeMarkerTransformer(Map<String, Template> templates, String newLine) {
        this.templates.putAll(templates);
        this.newLine = newLine;
    }
    public FreeMarkerTransformer(Map<String, Template> templates) {
        this(templates, "<br>");
    }

    @Override
    public String transform(Document document) {
        StringBuilder sb = new StringBuilder();
        for (Node node : document.children) {
            transformNode(sb, node);
        }
        return sb.toString().trim();
    }

    private void transformNode(StringBuilder sb, Node node) throws TransformerException {
        if (node instanceof TagNode) {
            TagNode tag = (TagNode) node;
            if(!tag.transform) {
              sb.append(tag.getRawString());
              return;
            }
            StringBuilder childSB = new StringBuilder();
            for (Node child : tag.children) {
                transformNode(childSB, child);
            }
            Map<String, Object> data = new HashMap<>(3);
            data.put("body", childSB.toString());
            data.put("attributes", tag.attributes);
            data.put("attribute", tag.attribute);

            Template template = templates.get(tag.getName());
            if (template == null) {
                sb.append(tag.getRawString());
            } else {
                try {
                    Writer out = new StringWriter();
                    template.process(data, out);
                    sb.append(out.toString());
                } catch (Exception e) {
                    throw new TransformerException("FreeMarker processing failed for template " + template.getName() + " \n\t Data model: " + data.get("body"), e);
                }
            }
        } else { // TextNode
            sb.append(((TextNode) node).getBody().replaceAll("\n", newLine));
        }
    }
}
