package org.cirdles.squid.parameters;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.ElementComparer;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ReferenceMaterialXMlConverterTest {

    @Test
    public void testReferenceMaterialXMLConverter() {
        try {
            ResourceExtractor extractor = new ResourceExtractor(ReferenceMaterialModel.class);

            File initialFile = extractor.extractResourceAsFile("z6266 ID-TIMS (559.0 Ma) v.1.0.xml");
            ParametersModel model = (ReferenceMaterialModel) (new ReferenceMaterialModel()).readXMLObject(initialFile.getAbsolutePath(), false);

            File convertedFile = new File("zirconCopy.xml");
            model.serializeXMLObject(convertedFile.getAbsolutePath());

            model = (ReferenceMaterialModel) model.readXMLObject(convertedFile.getAbsolutePath(), false);

            File convertedConvertedFile = new File("zirconCopyOfCopy.xml");
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