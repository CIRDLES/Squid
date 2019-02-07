package org.cirdles.squid.tasks.expressions;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.utilities.stateUtilities.SquidSerializer;
import org.scilab.forge.jlatexmath.TeXFormula;

import javax.imageio.ImageIO;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;

public class ExpressionPublisher {

    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    public static String removeMathMLStyling(String input) {
        String output = input.replaceAll("(<.*(mstyle|mspace).*>|&nbsp;)", "");
        output = output.replaceAll("&times;", "*");
        return output;
    }

    public static String getLatexFromMathML(String input, boolean hasStyling) {
        String retVal;
        if (hasStyling) {
            input = removeMathMLStyling(input);
        }
        try {
            input = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\" display=\"block\">" + input + "</math>";
            StringWriter resultWriter = new StringWriter();
            StreamResult result = new StreamResult(resultWriter);
            StringReader inputReader = new StringReader(input);
            Source inputSource = new StreamSource(inputReader);

            Transformer transformer = transformerFactory.newTransformer(new StreamSource("xsltml_2.0/mmltex.xsl"));
            transformer.transform(inputSource, result);

            retVal = resultWriter.toString().replaceAll("\\%", "\\\\%");
        } catch (TransformerException e) {
            retVal = "Transformer Exception";
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return retVal;
    }

    public static Image createImageFromMathML(String input, boolean hasStyling) {
        String converted = getLatexFromMathML(input, hasStyling);
        TeXFormula formula = new TeXFormula(converted);
        return formula.createBufferedImage(1, 50, Color.BLUE, Color.LIGHT_GRAY);
    }

    public static boolean createHTMLDocumentFromExpression(File file, Expression exp) {
        BufferedImage image = (BufferedImage) createImageFromMathML(exp.getExpressionTree().toStringMathML(), true);
        File imageFile = new File(exp.getName()+ ".jpeg");
        boolean imageCreated;
        try {
            ImageIO.write(image, "jpeg", imageFile);
            imageCreated = true;
        } catch(IOException e) {
            e.printStackTrace();
            imageCreated = false;
        }
        StringBuilder string = new StringBuilder();
        string.append("<HTML>");
        string.append("<h1>" + exp.getName() + "</h1>");
        if(imageCreated) {
            string.append("<img src=" + imageFile.getAbsolutePath() + "/>");
        }
        string.append("<body>");
        string.append("<p>");
        string.append("Expression Tree Audit: " + exp.produceExpressionTreeAudit() + "</p>");
        string.append("<p>Excel Expression String: " + exp.getExcelExpressionString() + "</p>");
        string.append("<p>Notes:\n" + (exp.getNotes().trim().isEmpty()? "No notes" : exp.getNotes()) + "</p>");
        string.append("</body>");
        string.append("</HTML>");

        boolean worked;
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(file));
            writer.write(string.toString());
            writer.flush();
            worked = true;
        } catch(IOException e) {
            worked = false;
        }
        return worked;
    }

    public static void main(String[] argv) {
        ResourceExtractor squidProjectExtractor = new ResourceExtractor(SquidProject.class);
        File squidFile = squidProjectExtractor.extractResourceAsFile("Z626611PKPERM1.squid");
        SquidProject project = (SquidProject) SquidSerializer.getSerializedObjectFromFile(squidFile.getAbsolutePath(), false);
        Expression exp = project.getTask().getExpressionByName("Hf1sabserr");
        createHTMLDocumentFromExpression(new File("test.html"), exp);
        /*BufferedImage image = (BufferedImage) createImageFromMathML(exp.getExpressionTree().toStringMathML(), true);
        try {
            ImageIO.write(image, "jpeg", new File("test.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}