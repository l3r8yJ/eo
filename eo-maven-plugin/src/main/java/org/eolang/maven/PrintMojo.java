/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.maven;

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.cactoos.text.TextOf;
import org.eolang.maven.util.HmBase;
import org.eolang.maven.util.Home;
import org.eolang.maven.util.Walk;
import org.eolang.parser.XMIR;

/**
 * Print XMIR to EO.
 * @since 0.33.0
 */
@Mojo(
    name = "print",
    defaultPhase = LifecyclePhase.PROCESS_SOURCES,
    threadSafe = true
)
public final class PrintMojo extends SafeMojo {
    /**
     * Directory with XMIR sources to print.
     * @checkstyle MemberNameCheck (10 lines)
     */
    @Parameter(
        property = "eo.printSourcesDir",
        required = true,
        defaultValue = "${project.basedir}/src/main/xmir"
    )
    private File printSourcesDir;

    /**
     * Directory where printed EO files are placed.
     * @checkstyle MemberNameCheck (10 lines)
     */
    @Parameter(
        property = "eo.printOutputDir",
        required = true,
        defaultValue = "${project.build.directory}/generated-sources/eo"
    )
    private File printOutputDir;

    @Override
    void exec() throws IOException {
        final Collection<Path> sources = new Walk(this.printSourcesDir.toPath());
        final Home home = new HmBase(this.printOutputDir);
        for (final Path source : sources) {
            final Path relative = Paths.get(
                this.printSourcesDir.toPath().relativize(source).toString()
                    .replace(".xmir", ".eo")
            );
            home.save(new XMIR(new TextOf(source)).toEO(), relative);
            Logger.info(
                this,
                "Printed: %s => %s", source, this.printOutputDir.toPath().resolve(relative)
            );
        }
        if (sources.isEmpty()) {
            Logger.info(this, "No XMIR sources found");
        } else {
            Logger.info(this, "Printed %d XMIR sources into EO", sources.size());
        }
    }
}
