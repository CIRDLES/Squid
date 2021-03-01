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

    public default boolean generateReportsValid() {
        return (!((SquidProject) this).getTask().getNominalMasses().isEmpty())
                && ((SquidProject) this).hasReportsFolder()
                && ((SquidProject) this).prawnFileExists();
    }

    public default Path generateReferenceMaterialSummaryExpressionsReport() throws IOException {
        Path summaryFilePath = null;
        if (generateReportsValid()) {
            summaryFilePath
                    = ((SquidProject) this).getPrawnFileHandler().getReportsEngine().writeSummaryReportsForReferenceMaterials().toPath();
        }
        if (summaryFilePath == null) {
            throw new IOException("Squid3 unable to generateReferenceMaterialSummaryExpressionsReport");
        } else {
            return summaryFilePath;
        }
    }

    public default Path generateUnknownsSummaryExpressionsReport() throws IOException {
        Path summaryFilePath = null;
        if (generateReportsValid()) {
            summaryFilePath
                    = ((SquidProject) this).getPrawnFileHandler().getReportsEngine().writeSummaryReportsForUnknowns().toPath();
        }
        if (summaryFilePath == null) {
            throw new IOException("Squid3 unable to generateUnknownsSummaryExpressionsReport");
        } else {
            return summaryFilePath;
        }
    }

    public default Path generateTaskSummaryReport() throws IOException {
        Path taskAuditFilePath = null;
        if (generateReportsValid()) {
            taskAuditFilePath = ((SquidProject) this).getPrawnFileHandler().getReportsEngine().writeTaskAudit().toPath();
        }
        if (taskAuditFilePath == null) {
            throw new IOException("Squid3 unable to generateTaskSummaryReport");
        } else {
            return taskAuditFilePath;
        }
    }

    public default Path generateProjectAuditReport() throws IOException {
        Path projectAuditFilePath = null;
        if (generateReportsValid()) {
            projectAuditFilePath = ((SquidProject) this).getPrawnFileHandler().getReportsEngine().writeProjectAudit().toPath();
        }
        if (projectAuditFilePath == null) {
            throw new IOException("Squid3 unable to generateProjectAuditReport");
        } else {
            return projectAuditFilePath;
        }
    }

    public default Path generatePerScanReports() throws IOException {
        Path projectScanReportsPath = null;
        if (generateReportsValid()) {
            projectScanReportsPath = ((SquidProject) this).getTask().producePerScanReportsToFiles().toPath();
        }
        if (projectScanReportsPath == null) {
            throw new IOException("Squid3 unable to generatePerScanReports");
        } else {
            return projectScanReportsPath;
        }
    }

    public Path generateAllReports() throws IOException;

}
