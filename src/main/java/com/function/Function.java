package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.HttpResponseMessage.Builder;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.preproc.Defines;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.ArrayList;


/**
 * Azure Functions with HTTP Trigger.
 * 
 * MIT License
 * 
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("convert")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        
        final String url = request.getQueryParameters().get("url");
        if (url == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("url query parameter is required").build();
        }
        String umltext;
        try {
            String[] segments = url.split("/", -1);
            String encoded = segments[segments.length - 1];
            Transcoder transcoder = TranscoderUtil.getDefaultTranscoder();
            umltext = transcoder.decode(encoded);
        } catch (IOException ioe) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("unable to decode encoded string").build();
        }
        FileFormat format;
        if (url.contains("/svg/")) {
            format = FileFormat.SVG;
        } else if (url.contains("/xmi_argo/")) {
            format = FileFormat.XMI_ARGO;
        } else if (url.contains("/xmi/")) {
            format = FileFormat.XMI_STANDARD;
        } else if (url.contains("/xmi_star/")) {
            format = FileFormat.XMI_STAR;
        } else if (url.contains("/png/")) {
            format = FileFormat.PNG;
        } else if (url.contains("/img/")) {
            format = FileFormat.PNG;
        } else {
            // svg as default
            format = FileFormat.SVG;
        }
        try {
            SourceStringReader reader = new SourceStringReader(Defines.createEmpty(), umltext, new ArrayList<String>());
            final Diagram diagram = reader.getBlocks().get(0).getDiagram();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            diagram.exportDiagram(os, 0, new FileFormatOption(format));
            Builder response = request.createResponseBuilder(HttpStatus.OK);
            response.header("Content-Type", format.getMimeType());
            response.header("Access-Control-Allow-Origin", "*");
            response.body(os.toByteArray());
            return response.build();
        } catch (IOException e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
