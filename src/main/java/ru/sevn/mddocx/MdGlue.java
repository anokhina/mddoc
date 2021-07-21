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

import com.vladsch.flexmark.html.HtmlRenderer;

import com.vladsch.flexmark.docx.converter.DocxRenderer;

import com.vladsch.flexmark.formatter.Formatter;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import java.io.File;
import java.io.IOException;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import com.vladsch.flexmark.test.util.AstCollectingVisitor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

//https://github.com/vsch/flexmark-java/blob/master/flexmark-java-samples/src/com/vladsch/flexmark/java/samples/NodeInsertingPostProcessorSample.java
public class MdGlue {
    
    private static final String OUT_DIR_NAME = "target/out";
    private static final String FILE_NAME = "00";

    public static void main(String ... args) throws IOException, Docx4JException {
        final File outDir = new File(OUT_DIR_NAME);
        outDir.mkdirs();
        
        final File file;
        if (args.length == 0) {
            file = new File("sample/00.md");
        } else {
            file = new File(args[0]);
        }
        
        final FileParsingContext apc = new FileParsingContext(file);
        final Parser parser = Parser.builder (apc.getOptions()).build ();
        
        final Node document = parser.parse(apc.getSource());
        
        final HtmlRenderer htmlRenderer = HtmlRenderer.builder (apc.getOptions()).build();
        final DocxRenderer docxRenderer = DocxRenderer.builder (apc.getOptions()).build();
        final Formatter formatter = Formatter.builder (apc.getOptions()).build();
        
        System.out.println("\n---- AST ------------------------\n");
        System.out.println(new AstCollectingVisitor().collectAndGetAstText(document));
        
        
        {
            final File fileOut = new File(outDir, FILE_NAME + ".md");
            System.out.println("\n---- Markdown ---\n" + fileOut.getAbsolutePath());
            final String md = formatter.render(document);
            Files.write(fileOut.toPath(), md.getBytes(StandardCharsets.UTF_8));
        }
        
        {
            final File fileOut = new File(outDir, FILE_NAME + ".html");
            System.out.println("\n---- HTML ---\n" + fileOut.getAbsolutePath());
            final String html = htmlRenderer.render(document);
            Files.write(fileOut.toPath(), html.getBytes(StandardCharsets.UTF_8));
        }
        
        {
            final File fileOut = new File(outDir, FILE_NAME + ".docx");
            System.out.println("\n---- Docx ---\n" + fileOut.getAbsolutePath());
            final WordprocessingMLPackage template = DocxRenderer.getDefaultTemplate();
            docxRenderer.render(document, template);
            template.save(fileOut, Docx4J.FLAG_SAVE_ZIP_FILE);
        }
    }
    
}
