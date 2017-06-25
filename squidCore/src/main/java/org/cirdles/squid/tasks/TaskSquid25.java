/*
 * Copyright 2017 CIRDLES.org.
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
package org.cirdles.squid.tasks;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.extractor.ExcelExtractor;

/**
 *
 * @author James F. Bowring
 */
public class TaskSquid25 {
    
    

    public static void importSquidTaskFile() {
        
        try {
            InputStream inp = new FileInputStream("SquidTask_ILC Zircon 10pk exp=float.GA.xls");
            HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
            ExcelExtractor extractor = new org.apache.poi.hssf.extractor.ExcelExtractor(wb);
            
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);
            String text = extractor.getText();
            
            String[] lines = text.split("\n");
            for (int i = 0; i < lines.length; i ++){
            System.out.println(i + "     " + lines[i]);
            }
        } catch (IOException iOException) {
        }

    }
    public static void main(String[] args) {
        importSquidTaskFile();
    }
    
}
