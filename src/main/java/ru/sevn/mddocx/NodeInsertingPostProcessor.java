/*
 * Copyright 2021 Veronica Anokhina.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.sevn.mddocx;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeTracker;

class NodeInsertingPostProcessor extends NodePostProcessor {

    private final ParsingContext parsingContext;

    NodeInsertingPostProcessor(final ParsingContext parsingContext) {
        this.parsingContext = parsingContext;
    }

    static class NodeInsertingFactory extends NodePostProcessorFactory {

        private final ParsingContext parsingContext;

        NodeInsertingFactory(final ParsingContext parsingContext) {
            super(false);
            this.parsingContext = parsingContext;
            addNodes(Link.class);
        }

        @Override
        public NodePostProcessor apply(Document document) {
            return new NodeInsertingPostProcessor(parsingContext);
        }
    }

    @Override
    public void process(NodeTracker state, Node node) {
        if (node instanceof Link) {
            final Link l = (Link) node;
            if (l.getParent() instanceof Heading) {
                final Heading h = (Heading) l.getParent();
                final String url = l.getUrl().toStringOrNull();
                final ParsingContext pc = this.parsingContext.getParsingContext(url);
                if (pc != null) {
                    final Node pnode = h.getParent();
                    h.unlink();
                    state.nodeRemovedWithChildren(h);
                    final String str = pc.getSource();
                    try {
                        final Parser parser = Parser.builder(pc.getOptions()).build();
                        final Document doc = parser.parse(str);
                        final Node fnode = doc.getFirstChild();
                        pnode.appendChain(fnode);
                        state.nodeAddedWithChildren(fnode);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                System.err.println("##import###>" + url);
            } else {
                System.err.println("##link#####>" + node + ":" + l.getParent());
            }
        }
    }

}
