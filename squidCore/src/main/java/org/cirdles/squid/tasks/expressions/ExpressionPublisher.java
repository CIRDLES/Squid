package org.cirdles.squid.tasks.expressions;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.utilities.stateUtilities.SquidSerializer;
import org.scilab.forge.jlatexmath.TeXFormula;
import java.util.List;

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
import org.cirdles.squid.utilities.fileUtilities.FileNameFixer;

public class ExpressionPublisher {

    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    public static String removeMathMLStyling(String input) {
        String output = input.replaceAll("(<.*(mstyle|mspace).*>|&nbsp;)", "");
        output = output.replaceAll("&times;", "*");
        output = output.replaceAll("&ExponentialE;", "e");
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
            System.out.println("Input:" + input);
        }
        return retVal;
    }

    public static Image createImageFromMathML(String input, boolean hasStyling) {
        String converted = getLatexFromMathML(input, hasStyling);
        TeXFormula formula = new TeXFormula(converted);
        return formula.createBufferedImage(1, 60, Color.BLUE, Color.WHITE);
    }

    public static boolean createHTMLDocumentFromExpressions(File file, List<Expression> expressions) {
        StringBuilder string = new StringBuilder();
        string.append("<!DOCTYPE html>\n");
        string.append("<HTML lang=\"en\">\n<head>\n<meta charset=\"utf-8\"/>\n");
        string.append("<title>Squid Expressions</title>\n");
        string.append("<style>\n"
                + "body {background-color: white;}\n"
                + "h1   {color: blue;}\n"
                + "h2   {color: blue}\n"
                + "p    {color: blue;}\n"
                + "</style>\n");
        string.append("</head>\n<body>\n");
        string.append("<h1>Expressions</h1>\n");

        File imageFolder = new File("expressionsImages");
        imageFolder.mkdir();

        for (Expression exp : expressions) {
            BufferedImage image = (BufferedImage) createImageFromMathML(exp.getExpressionTree().toStringMathML(), true);
            File imageFile = new File(imageFolder.getPath() + File.separator + FileNameFixer.fixFileName(exp.getName()) + ".png");
            boolean imageCreated;
            try {
                ImageIO.write(image, "png", imageFile);
                imageCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
                imageCreated = false;
            }
            string.append("<h2>" + exp.getName() + "</h2>\n");
            if (imageCreated) {
                string.append("<img src=\"" + imageFile.getPath().replaceAll("\\\\", "/") + "\" alt=\"Image not available\" width=\"" + ((int) (image.getWidth() / 2.5 + .5)) + "\" "
                        + "height=\"" + ((int) (image.getHeight() / 2.5 + .5)) + "\"" + "/>\n");
            }
            string.append("<p>" + exp.getName() + " is " + (exp.isReferenceMaterialValue() ? "" : "not ") + "a reference material value\n");
            string.append("<br/>" + exp.getName() + " is " + (exp.isParameterValue() ? "" : "not ") + "a parameter value\n");
            string.append("<br/>" + exp.getName() + " is " + (exp.amHealthy() ? "" : "not ") + "healthy\n");
            string.append("<br/>" + exp.getName() + " is " + (exp.isSquidSwitchNU() ? "" : "not ") + "Squid Switch NU</p>\n");
            string.append("<p>Excel Expression String: " + exp.getExcelExpressionString() + "</p>\n");
            string.append("<p>Notes:<br/>" + (exp.getNotes().trim().isEmpty() ? "No notes" : exp.getNotes()).replaceAll("\n", "<br/>") + "</p>\n");
            //string.append("<p>Expression Tree Audit:<br/>" + exp.produceExpressionTreeAudit().replaceAll("\n", "<br/>") + "</p>\n");
        }
        string.append("</body>\n");
        string.append("</HTML>");

        boolean worked;
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(file));
            writer.write(string.toString());
            writer.flush();
            worked = true;
        } catch (IOException e) {
            worked = false;
        }
        return worked;
    }

    public static boolean createHTMLDocumentFromExpression(File file, Expression exp) {
        BufferedImage image = (BufferedImage) createImageFromMathML(exp.getExpressionTree().toStringMathML(), true);
        File imageFile = new File(FileNameFixer.fixFileName(exp.getName()) + ".png");
        boolean imageCreated;
        try {
            ImageIO.write(image, "png", imageFile);
            imageCreated = true;
        } catch (IOException e) {
            e.printStackTrace();
            imageCreated = false;
        }
        StringBuilder string = new StringBuilder();
        string.append("<!DOCTYPE html>\n");
        string.append("<HTML lang=\"en\">\n<head>\n<meta charset=\"utf-8\"/>\n");
        string.append("<title>Squid Expressions</title>\n");
        string.append("<style>\n"
                + "body {background-color: white;}\n"
                + "h1   {color: blue;}\n"
                + "h2   {color: blue}\n"
                + "p    {color: blue;}\n"
                + "</style>\n");
        string.append("</head>\n<body>\n");
        string.append("<h1>" + exp.getName() + "</h1>\n");
        if (imageCreated) {
            string.append("<img src=\"" + imageFile.getPath() + "\" alt=\"Image not available\" width=\"" + ((int) (image.getWidth() / 2.5 + .5)) + "\" "
                    + "height=\"" + ((int) (image.getHeight() / 2.5 + .5)) + "\"" + "/>\n");
        }
        string.append("<p>" + exp.getName() + " is " + (exp.isReferenceMaterialValue() ? "" : "not ") + "a reference material value\n");
        string.append("<br/>" + exp.getName() + " is " + (exp.isParameterValue() ? "" : "not ") + "a parameter value\n");
        string.append("<br/>" + exp.getName() + " is " + (exp.amHealthy() ? "" : "not ") + "healthy\n");
        string.append("<br/>" + exp.getName() + " is " + (exp.isSquidSwitchNU() ? "" : "not ") + "Squid Switch NU</p>\n");
        string.append("<p>Excel Expression String: " + exp.getExcelExpressionString() + "</p>\n");
        string.append("<p>Notes:<br/>" + (exp.getNotes().trim().isEmpty() ? "No notes" : exp.getNotes()) + "</p>\n");
        //string.append("<p>Expression Tree Audit:<br/>" + exp.produceExpressionTreeAudit().replaceAll("\n", "<br/>") + "</p>\n");
        string.append("</body>\n");
        string.append("</HTML>\n");

        boolean worked;
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(file));
            writer.write(string.toString());
            writer.flush();
            worked = true;
        } catch (IOException e) {
            worked = false;
        }
        return worked;
    }

    public static boolean createPDFFromExpressions(File file, List<Expression> expressions) {
        boolean retVal;

        File htmlFile = new File("ExpressionsHTMLToPDFConversionFile.html");
        retVal = createHTMLDocumentFromExpressions(htmlFile, expressions);

        if (retVal) {
            try {
                OutputStream os = new FileOutputStream(file);
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withFile(htmlFile);
                builder.toStream(os);
                builder.run();
                retVal = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        htmlFile.delete();
        return retVal;
    }

    public static boolean createPDFFromExpression(File file, Expression exp) {
        boolean retVal;

        File htmlFile = new File("ExpressionHTMLToPDFConversionFile.html");
        retVal = createHTMLDocumentFromExpression(htmlFile, exp);

        if (retVal) {
            try {
                OutputStream os = new FileOutputStream(file);
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withFile(htmlFile);
                builder.toStream(os);
                builder.run();
                retVal = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        htmlFile.delete();
        return retVal;
    }

    public static void main(String[] argv) {
        ResourceExtractor squidProjectExtractor = new ResourceExtractor(SquidProject.class);
        File squidFile = squidProjectExtractor.extractResourceAsFile("Z626611PKPERM1.squid");
        SquidProject project = (SquidProject) SquidSerializer.getSerializedObjectFromFile(squidFile.getAbsolutePath(), false);
        Expression exp = project.getTask().getExpressionByName("Hf1sabserr");
        createHTMLDocumentFromExpression(new File("test.html"), exp);
        createHTMLDocumentFromExpressions(new File("testgroup.html"), project.getTask().getTaskExpressionsOrdered());
        createPDFFromExpression(new File("test.pdf"), exp);
        createPDFFromExpressions(new File("testgroup.pdf"), project.getTask().getTaskExpressionsOrdered());
    }

}
