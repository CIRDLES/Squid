package org.cirdles.squid.parameters;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.ElementComparer;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterials.ReferenceMaterial;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PhysicalConstantsModelXMLConverterTest {

    @Test
    public void testPhysicalConstantsXMLConverter() {
        try {
            ResourceExtractor extractor = new ResourceExtractor(PhysicalConstantsModel.class);

            File initialFile = extractor.extractResourceAsFile("EARTHTIME Physical Constants Model v.1.1.xml");
            PhysicalConstantsModel model = PhysicalConstantsModel.getPhysicalConstantsModelFromETReduxXML(initialFile);

            File convertedFile = new File("physicalConstantsCopy.xml");
            model.serializeXMLObject(convertedFile.getAbsolutePath());

            model = (PhysicalConstantsModel) model.readXMLObject(convertedFile.getAbsolutePath(), false);

            File convertedConvertedFile = new File("physicalConstantsCopyOfCopy.xml");
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
