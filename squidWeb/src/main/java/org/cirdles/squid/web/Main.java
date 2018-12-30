/*
 * Copyright 2016 CIRDLES
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cirdles.squid.web;

import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by johnzeringue on 7/27/16.
 * Adapted by James Bowring Dec 2018.
 */
public class Main {

    public static void main(String[] args) {
        URI baseUri = UriBuilder.fromUri("http://localhost/")
                .port(8080)
                .build();

        ResourceConfig config = new ResourceConfig(SquidReportingResource.class);
        config.register(MultiPartFeature.class);

        JettyHttpContainerFactory.createServer(baseUri, config);
    }

}
