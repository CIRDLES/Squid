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

import java.io.File;
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * Created by johnzeringue on 7/27/16.
 * Adapted by James Bowring Dec 2018.
 */
@Path("squidReporting")
public class SquidReportingResource {

    private SquidReportingService squidReportingService;

    public SquidReportingResource() {
        squidReportingService = new SquidReportingService();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/zip")
    public Response generateReports(
            @FormDataParam("prawnFile") InputStream prawnFile,
            @FormDataParam("prawnFile") FormDataContentDisposition contentDispositionHeader,
            @DefaultValue("true") @FormDataParam("useSBM") boolean useSBM,
            @DefaultValue("false") @FormDataParam("userLinFits") boolean userLinFits,
            @DefaultValue("") @FormDataParam("refMatFilter") String refMatFilter,
            @DefaultValue("") @FormDataParam("concRefMatFilter") String concRefMatFilter)
            throws Exception {

        java.nio.file.Path pathToZip = squidReportingService.generateReports(
                contentDispositionHeader.getFileName(), prawnFile, useSBM, userLinFits, refMatFilter, concRefMatFilter);
        File zippedFile = pathToZip.toFile();        
        
        return Response
                .ok(zippedFile)
                .header("Content-Disposition",
                        "attachment; filename=squid-reports.zip")
                .build();
    }

}
