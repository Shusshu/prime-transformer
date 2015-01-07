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

package org.primeframework.transformer.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author Daniel DeGroff
 */
public abstract class BaseTagNode extends BaseNode {

  /**
   * Add a node as a child to this node.
   *
   * @param node the node to be added to this nodes children.
   *
   * @return
   */
  public abstract boolean addChild(Node node);

  /**
   * @return Return a {@link List} of {@link TagNode} objects.
   */
  // TODO Remove? Anyone using this?
  public List<TagNode> getChildTagNodes() {
    List<TagNode> tagNodes = new ArrayList<>(getChildren().size());
    if (this instanceof TagNode) {
      tagNodes.add((TagNode) this);
    }
    getChildren().forEach(n -> {
      if (n instanceof TagNode) {
        tagNodes.addAll(((TagNode) n).getChildTagNodes());
      }
    });
    return tagNodes;
  }

  /**
   * @return Return a {@link List} of TextNode objects.
   */
  // TODO Remove? Anyone using this?
  public List<TextNode> getChildTextNodes() {
    List<TextNode> textNodes = new ArrayList<>(getChildren().size());
    getChildren().forEach(n -> {
      if (n instanceof TextNode) {
        textNodes.add((TextNode) n);
      } else {
        TagNode tag = (TagNode) n;
        textNodes.addAll(tag.getChildTextNodes());
      }
    });
    return textNodes;
  }

  /**
   * Return a {@link List} of {@link Node} objects.
   *
   * @return Return the child nodes. An empty list indicates this node has no children.
   */
  public abstract List<Node> getChildren();

  /**
   * Walk the document nodes and apply the action to each node of the specified type..
   *
   * @param action the operation to be applied to each node.
   *
   * @return {@link Stream}
   */
  // TODO Remove? Anyone using this?
  public <T> Stream<Node> walk(Class<T> consumerType, Consumer<? super T> action) {
    getChildren().forEach(n -> {
      if (consumerType.isAssignableFrom(n.getClass())) {
        //noinspection unchecked
        action.accept((T) n);
        if (n instanceof TagNode) {
          ((TagNode) n).walk(consumerType, action);
        }
      }
    });
    return getChildren().stream();
  }

  /**
   * Walk the document nodes and apply the action to each node.
   *
   * @param action the operation to be applied to each node.
   *
   * @return {@link Stream}
   */
  // TODO Remove? Anyone using this?
  public Stream<Node> walk(Consumer<? super Node> action) {
    return walk(Node.class, action);
  }
}
