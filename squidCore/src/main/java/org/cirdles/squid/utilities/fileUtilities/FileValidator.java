package org.cirdles.squid.utilities.fileUtilities;

import com.thoughtworks.xstream.XStream;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import org.cirdles.commons.util.ResourceExtractor;
import org.jdom2.Content;
import org.jdom2.Element;

public class FileValidator {

    public static boolean validateFileIsXMLSerializedEntity(File serializedFile, Class entityClass) {
        boolean retVal = true;
        try {
          /*  XStream stream = new XStream();
            stream.allowTypeHierarchy(entityClass);
            stream.useAttributeFor(entityClass);
            stream.getReflectionProvider().newInstance(entityClass);
            Object ob = stream.fromXML(serializedFile);
            retVal = entityClass.isInstance(ob);*/

           /* SAXBuilder builder = new SAXBuilder();
            Document doc = (Document) builder.build(serializedFile);
            Element root = doc.getRootElement();
            Content content = root.getContent(0);
            System.out.println(content.getClass()); */
            
            /*XStream xstream = new XStream();
            xstream.setClassLoader(entityClass.getClassLoader());
            Object conversion = xstream.fromXML(serializedFile.getAbsoluteFile());
            retVal = entityClass.isInstance(conversion);*/
        } catch (Exception e) {
            retVal = false;
            e.printStackTrace();
        }
        return retVal;
    }

    public static void main(String[] args) {
        ResourceExtractor extractor = new ResourceExtractor(CommonPbModel.class);
        File file = extractor.extractResourceAsFile("GA Common Lead 2018 v.1.0.xml");
        validateFileIsXMLSerializedEntity(file, CommonPbModel.class);
    }
}
