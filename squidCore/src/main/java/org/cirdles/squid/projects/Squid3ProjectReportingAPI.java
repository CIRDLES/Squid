/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.projects;

import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author bowring
 */
public interface Squid3ProjectReportingAPI {

    public Path generateAllReports() throws IOException;
}
