package com.ach.nifi.core;


import org.apache.commons.io.IOUtils;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.*;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.OutputStreamCallback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Tags({ "example", "resources" })
@CapabilityDescription("This example processor loads a resource from the nar and writes it to the FlowFile content")
public class NifiProcStreamWriter extends AbstractProcessor {

    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("files that were successfully processed").build();
    public static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("failure")
            .description("files that were not successfully processed").build();

    private Set<Relationship> relationships;

    private String resourceData;

    @Override
    protected void init(final ProcessorInitializationContext context) {

        final Set<Relationship> relationships = new HashSet<Relationship>();
        relationships.add(REL_SUCCESS);
        relationships.add(REL_FAILURE);
        this.relationships = Collections.unmodifiableSet(relationships);
        final InputStream resourceStream = getClass()
                .getClassLoader().getResourceAsStream("/home/pawan/IdeaProjects/achook/achook-ei-parent/nifi-int/src/main/java/com/ach/nifi/core/file.txt");
        try {
            this.resourceData = IOUtils.toString(resourceStream, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Unable to load resources", e);
        } finally {
            IOUtils.closeQuietly(resourceStream);
        }

    }

    @Override
    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    @OnScheduled
    public void onScheduled(final ProcessContext context) {

    }

    @Override
    public void onTrigger(final ProcessContext context,
                          final ProcessSession session) throws ProcessException {
        FlowFile flowFile = session.get();
        if (flowFile == null) {
            return;
        }

        try {
            flowFile = session.write(flowFile, new OutputStreamCallback() {


                public void process(OutputStream out) throws IOException {
                    IOUtils.write(resourceData, out, Charset.defaultCharset());

                }
            });
            session.transfer(flowFile, REL_SUCCESS);
        } catch (ProcessException ex) {
            getLogger().error("Unable to process", ex);
            session.transfer(flowFile, REL_FAILURE);
        }
    }
}

