/*
 * ReportSettingsXMLConverter.java
 *
 * Created 23 October 2009
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cirdles.squid.reports.reportSettings;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.cirdles.squid.reports.reportCategories.ReportCategory;

/**
 * A <code>ReportSettingsXMLConverter</code> is used to marshal and unmarshal
 * data between <code>reportSettings</code> and XML files.
 *
 * @author James F. Bowring, javaDocs by Stan Gasque
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/Converter.html>
 * com.thoughtworks.xstream.converters.Converter</a>
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/MarshallingContext.html>
 * com.thoughtworks.xstream.converters.MarhsallingContext</a>
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/UnmarshallingContext.html>
 * com.thoughtworks.xstream.converters.UnmarshallingContext</a>
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/HierarchicalStreamReader.html>
 * com.thoughtworks.xstream.io.HierachicalSreamReader</a>
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/HierarchicalStreamWriter.html>
 * com.thoughtworks.xstream.io.HierarchicalStreamWriter</a>
 */
public class ReportSettingsXMLConverter implements Converter {

    /**
     * checks the argument <code>clazz</code> against
     * <code>reportSettings</code>'s <code>Class</code>. Used to ensure that the
     * object about to be marshalled/unmarshalled is of the correct type.
     *
     * @param clazz <code>Class</code> of the <code>Object</code> you wish to
     *              convert to/from XML
     * @return <code>boolean</code> - <code>true</code> if <code>clazz</code>
     * matches <code>reportSettings</code>'s <code>Class</code>; else
     * <code>false</code>.
     * @pre argument <code>clazz</code> is a valid <code>Class</code>
     * @post <code>boolean</code> is returned comparing <code>clazz</code>
     * against <code>reportSettings.class</code>
     */
    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(ReportSettings.class);
    }

    /**
     * writes the argument <code>value</code> to the XML file specified through
     * <code>writer</code>
     *
     * @param value   <code>reportSettings</code> that you wish to write to a file
     * @param writer  stream to write through
     * @param context <code>MarshallingContext</code> used to store generic data
     * @pre <code>value</code> is a valid <code>reportSettings</code>, <code>
     * writer</code> is a valid <code>HierarchicalStreamWriter</code>, and
     * <code>context</code> is a valid <code>MarshallingContext</code>
     * @post <code>value</code> is written to the XML file specified via
     * <code>writer</code>
     */
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
                        MarshallingContext context) {

        ReportSettings reportSettings = (ReportSettings) value;

        writer.startNode("name");
        writer.setValue(reportSettings.getName());
        writer.endNode();

        writer.startNode("version");
        writer.setValue(Integer.toString(reportSettings.getVersion()));
        writer.endNode();

        writer.startNode("fractionCategory");
        context.convertAnother(reportSettings.getFractionCategory());
        writer.endNode();

        writer.startNode("correctionIndependentCategory");
        context.convertAnother(reportSettings.getCorrectionIndependentCategory());
        writer.endNode();

        writer.startNode("XXXXXX");
        context.convertAnother(reportSettings.getPb204CorrectedCategory());
        writer.endNode();

        writer.startNode("XXXXX");
        context.convertAnother(reportSettings.getPb207CorrectedCategory());
        writer.endNode();

        writer.startNode("XXXX");
        context.convertAnother(reportSettings.getPb208CorrectedCategory());
        writer.endNode();

        writer.startNode("fractionCategory2");
        context.convertAnother(reportSettings.getFractionCategory2());
        writer.endNode();

        writer.startNode("reportSettingsComment");
        writer.setValue(reportSettings.getReportSettingsComment());
        writer.endNode();

    }

    /**
     * reads a <code>reportSettings</code> from the XML file specified through
     * <code>reader</code>
     *
     * @param reader  stream to read through
     * @param context <code>UnmarshallingContext</code> used to store generic
     *                data
     * @return <code>Object</code> - <code>reportSettings</code> read from file
     * specified by <code>reader</code>
     * @pre <code>reader</code> leads to a valid <code>reportSettings</code>
     * @post the <code>reportSettings</code> is read from the XML file and
     * returned
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context) {

        ReportSettings reportSettings = new ReportSettings();

        reader.moveDown();
        reportSettings.setName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        reportSettings.setVersion(Integer.parseInt(reader.getValue()));
        reader.moveUp();

        ReportCategory reportCategory = new ReportCategory();
        reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
        reportSettings.setFractionCategory(reportCategory);
        reader.moveUp();

        reader.moveDown();
        reportCategory = new ReportCategory();
        reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
        reportSettings.setCorrectionIndependentCategory(reportCategory);
        reader.moveUp();

        reader.moveDown();
        reportCategory = new ReportCategory();
        reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
        reportSettings.setPb204CorrectedCategory(reportCategory);
        reader.moveUp();

        reader.moveDown();
        reportCategory = new ReportCategory();
        reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
        reportSettings.setPb207CorrectedCategory(reportCategory);
        reader.moveUp();

        reader.moveDown();
        reportCategory = new ReportCategory();
        reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
        reportSettings.setPb208CorrectedCategory(reportCategory);
        reader.moveUp();

        reader.moveDown();
        reportCategory = new ReportCategory();
        reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
        reportSettings.setFractionCategory2(reportCategory);
        reader.moveUp();

        reader.moveDown();
        reportSettings.setReportSettingsComment(reader.getValue());
        reader.moveUp();

        reportSettings.assembleReportCategories(true);
        return reportSettings;
    }
}