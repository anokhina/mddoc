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

import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughSubscriptExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class FileParsingContext implements ParsingContext<FileParsingContext> {

    private DataHolder options;
    private final File dir;
    private final String source;

    public FileParsingContext(final File file) throws IOException {
        this.source = readFile(file.toPath());
        this.dir = file.getParentFile();
        this.options = getMarkdownOptions(this).toImmutable();
    }

    @Override
    public DataHolder getOptions() {
        return options;
    }

    private File getDir() {
        return dir;
    }

    @Override
    public FileParsingContext getParsingContext(String linkUrl) {
        String linkPath;
        try {
            linkPath = java.net.URLDecoder.decode(linkUrl, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            linkPath = java.net.URLDecoder.decode(linkUrl);
        }
        final File f = new File(getDir(), linkPath);
        if (f.exists() && f.canRead() && f.isFile()) {
            try {
                return new FileParsingContext(f);
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static String readFile(final Path p) throws IOException {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Files.copy(p, baos);
            final String str = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            return str;
        }
    }

    private static MutableDataSet getMarkdownOptions(final ParsingContext parsingContext) {
        final MutableDataSet options = new MutableDataSet();
        //https://github.com/vsch/flexmark-java/wiki/Extensions
        options.set(Parser.EXTENSIONS, Arrays.asList( //AbbreviationExtension
        //AbbreviationExtension
        //AnchorLinkExtension.create(),
        //AsideExtension
        //DefinitionExtension
        //EmojiExtension
        //EnumeratedReferenceExtension
        //FootnoteExtension
        //GfmIssuesExtension
        //GfmUsersExtension
        //GitLabExtension
        //JekyllTagExtension
        //JiraConverterExtension
        //MacroExtension
        //SuperscriptExtension
        //TocExtension
        //SimTocExtension
        //WikiLinkExtension
        //MacroExtension
        //YamlFrontMatterExtension
        //YouTrackConverterExtension
        //YouTubeLinkExtension
        AttributesExtension.create(), AutolinkExtension.create(), //StrikethroughExtension.create() //or
        StrikethroughSubscriptExtension.create(), TablesExtension.create(), TaskListExtension.create(), TypographicExtension.create(), NodeInsertByLinkParserExtension.create(parsingContext)));
        // uncomment to convert soft-breaks to hard breaks
        //options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
        return options;
    }

    @Override
    public String getSource() {
        return source;
    }

}
