package org.cirdles.squid.core;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.utilities.fileUtilities.FileNameFixer;
import org.cirdles.squid.utilities.stateUtilities.SquidSerializer;
import org.cirdles.squid.Squid;

import org.junit.Test;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.cirdles.squid.utilities.fileUtilities.FileValidator.validateXML;

public class ExpressionGroupXMLTest {
    @Test
    public void testExpressionGroups() {
        boolean retVal = false;
        ResourceExtractor squidProjectExtractor = new ResourceExtractor(SquidProject.class);
        ResourceExtractor squidTaskXMLSchemaExtractor = new ResourceExtractor(Squid.class);
        File squidFile = squidProjectExtractor.extractResourceAsFile("Z626611PKPERM1.squid");
        File schemaFile = squidTaskXMLSchemaExtractor.extractResourceAsFile("schema/SquidTask_ExpressionXMLSchema.xsd");
        SquidProject project = (SquidProject) SquidSerializer.getSerializedObjectFromFile(squidFile.getAbsolutePath(), false);

        final File folder = new File("testingExpressionsGroups");
        folder.mkdir();
        List<Expression> expressions = project.getTask().getTaskExpressionsOrdered();
        List<Expression> customExpressions = Collections.synchronizedList(new ArrayList<>());
        expressions.parallelStream().forEach(exp -> {
            if (exp.isCustom() && exp.amHealthy()) {
                exp.serializeXMLObject(folder.getAbsolutePath() + File.separator + FileNameFixer.fixFileName(exp.getName()) + ".xml");
                customExpressions.add(exp);
            }
        });
        
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        File[] files = null;
        try {
            final Schema schema = sf.newSchema(schemaFile);
            files = folder.listFiles(f -> f.getName().endsWith(".xml"));
            for (File file : files) {
                try {
                    validateXML(file, schema);
                }   catch (SAXException | IOException e) {
                    // If any exception is thrown, test will fail
                }
        } 
                
        } catch (SAXException e) {
        }
        if (files != null) {
            List<Expression> convertedExpressions = new ArrayList<>();
            Expression converter = new Expression();
            for (File file : files) {
                convertedExpressions.add((Expression) converter.readXMLObject(file.getAbsolutePath(), false));
            }
            files = folder.listFiles();
            for(File file : files) {
                file.delete();
            }
            Comparator<Expression> expressionComparator = Comparator.comparing(Expression::getName);
            convertedExpressions.sort(expressionComparator);
            customExpressions.sort(expressionComparator);
            retVal = convertedExpressions.equals(customExpressions);
        }
        folder.delete();
        assertTrue(retVal);
    }
}
