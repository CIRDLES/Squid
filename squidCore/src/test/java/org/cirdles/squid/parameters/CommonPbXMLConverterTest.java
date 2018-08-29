package org.cirdles.squid.parameters;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.ElementComparer;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CommonPbXMLConverterTest {

    @Test
    public void testCommonPbXMLConverter() {
        try {
            ResourceExtractor extractor = new ResourceExtractor(CommonPbModel.class);

            File initialFile = extractor.extractResourceAsFile("GA Common Lead 2018 v.1.0.xml");
            CommonPbModel model = new CommonPbModel();
            model = (CommonPbModel) model.readXMLObject(initialFile.getAbsolutePath(), false);

            File convertedFile = new File("commonPbCopy.xml");
            model.serializeXMLObject(convertedFile.getAbsolutePath());

            model = (CommonPbModel) model.readXMLObject(convertedFile.getAbsolutePath(), false);

            File convertedConvertedFile = new File("commonPbCopyOfCopy.xml");
            model.serializeXMLObject(convertedConvertedFile.getAbsolutePath());

            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(convertedFile);
            Element initialElement = doc.getRootElement();
            doc = builder.build(convertedConvertedFile);
            Element convertedElement = doc.getRootElement();

            convertedFile.delete();
            convertedConvertedFile.delete();

            assertTrue(ElementComparer.compareElements(initialElement, convertedElement));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
