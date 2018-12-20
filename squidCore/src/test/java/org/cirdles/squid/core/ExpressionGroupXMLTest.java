package org.cirdles.squid.core;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.utilities.fileUtilities.FileNameFixer;
import org.cirdles.squid.utilities.fileUtilities.FileValidator;
import org.cirdles.squid.utilities.stateUtilities.SquidSerializer;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class ExpressionGroupXMLTest {
    @Test
    public void testExpressionGroups() {
        boolean retVal = false;
        ResourceExtractor squidProjectExtractor = new ResourceExtractor(SquidProject.class);
        File squidFile = squidProjectExtractor.extractResourceAsFile("Z626611PKPERM1.squid");
        SquidProject project = (SquidProject) SquidSerializer.getSerializedObjectFromFile(squidFile.getAbsolutePath(), false);

        final File folder = new File("testingExpressionsGroups");
        folder.mkdir();
        List<Expression> expressions = project.getTask().getTaskExpressionsOrdered();
        List<Expression> customExpressions = new ArrayList<>();
        expressions.forEach(exp -> {
            if (exp.isCustom()) {
                exp.serializeXMLObject(folder.getAbsolutePath() + File.separator + FileNameFixer.fixFileName(exp.getName()) + ".xml");
                customExpressions.add(exp);
            }
        });

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        File[] files = null;
        try {
            final Schema schema = sf.newSchema(new File("src/main/resources/org/cirdles/squid/schema/SquidTask_ExpressionXMLSchema.xsd"));
            files = folder.listFiles(f -> f.getName().endsWith(".xml") &&
                    FileValidator.validateFileIsXMLSerializedEntity(f, schema));
        } catch (SAXException e) {
        }
        if (files != null) {
            List<Expression> convertedExpressions = new ArrayList<>();
            Expression converter = new Expression();
            for (File file : files) {
                convertedExpressions.add((Expression) converter.readXMLObject(file.getAbsolutePath(), false));
            }

            Comparator<Expression> expressionComparator = new Comparator<Expression>() {
                @Override
                public int compare(Expression o1, Expression o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            };
            convertedExpressions.sort(expressionComparator);
            customExpressions.sort(expressionComparator);
            retVal = convertedExpressions.equals(customExpressions);
        }
        folder.delete();
        assertTrue(retVal);
    }
}
