package org.cirdles.squid.tasks.expressions;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder.PageSizeUnits;
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
import java.util.Arrays;
import java.util.Collections;
import org.antlr.v4.misc.MutableInt;
import org.cirdles.squid.utilities.fileUtilities.FileNameFixer;

public class ExpressionPublisher {

    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private static final double IMAGE_DPI = 200.0;
    private static final double SIZE_MULTIPLIER = .4;

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
        TeXFormula.setDPITarget((float) IMAGE_DPI);
        String converted = getLatexFromMathML(input, hasStyling);
        TeXFormula formula = new TeXFormula(converted);
        return formula.createBufferedImage(1, 20, Color.BLUE, Color.WHITE);
    }

    private static String getStartHTML() {
        return "<!DOCTYPE html>\n"
                + "<HTML lang=\"en\">\n<head>\n<meta charset=\"utf-8\"/>\n"
                + "<title>Squid Expressions</title>\n"
                + "<style>\n"
                + "body {background-color: white;}\n"
                + "h1   {color: blue;}\n"
                + "h2   {color: blue}\n"
                + "p    {color: blue;}\n"
                + "</style>\n"
                + "</head>\n<body>\n"
                + "<h1>Expressions</h1>\n";
    }

    private static String getEndHTML() {
        return "</body>\n</HTML>";
    }

    private static String getExpressionInfoHTML(Expression exp) {
        return "<p>" + exp.getName() + " is " + (exp.isReferenceMaterialValue() ? "" : "not ") + "a reference material value\n"
                + "<br/>" + exp.getName() + " is " + (exp.isParameterValue() ? "" : "not ") + "a parameter value\n"
                + "<br/>" + exp.getName() + " is " + (exp.amHealthy() ? "" : "not ") + "healthy\n"
                + "<br/>" + exp.getName() + " is " + (exp.isSquidSwitchNU() ? "" : "not ") + "Squid Switch NU</p>\n"
                + "<p>Excel Expression String: " + exp.getExcelExpressionString() + "</p>\n"
                + "<p>Notes:<br/>" + (exp.getNotes().trim().isEmpty() ? "No notes" : exp.getNotes()).replaceAll("\n", "<br/>") + "</p>\n";
    }

    public static boolean createHTMLDocumentFromExpressions(File file, List<Expression> expressions, Integer[] widths) {
        boolean enterWidths = widths != null;

        StringBuilder string = new StringBuilder();
        string.append(getStartHTML());

        File imageFolder = new File("expressionsImages");
        imageFolder.mkdir();

        for (int i = 0; i < expressions.size(); i++) {
            Expression exp = expressions.get(i);
            BufferedImage image = (BufferedImage) createImageFromMathML(exp.getExpressionTree().toStringMathML(), true);
            File imageFile = new File(imageFolder.getPath() + File.separator + FileNameFixer.fixFileName(exp.getName()) + ".png");
            boolean imageCreated;
            try {
                ImageIO.write(image, "png", imageFile);
                if (enterWidths) {
                    widths[i] = image.getWidth();
                }
                imageCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
                imageCreated = false;
            }
            string.append("<h2>" + exp.getName() + "</h2>\n");
            if (imageCreated) {
                string.append("<img src=\"" + imageFile.getPath().replaceAll("\\\\", "/") + "\" alt=\"Image not available\" "
                        + "width=\"" + (int) (image.getWidth() * SIZE_MULTIPLIER + .5) + "\" "
                        + "height=\"" + (int) (image.getHeight() * SIZE_MULTIPLIER + .5) + "\"" + "/>\n");
            }
            string.append(getExpressionInfoHTML(exp));
        }
        string.append(getEndHTML());

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

    public static boolean createHTMLDocumentFromExpression(File file, Expression exp, MutableInt width) {
        BufferedImage image = (BufferedImage) createImageFromMathML(exp.getExpressionTree().toStringMathML(), true);
        File imageFile = new File(FileNameFixer.fixFileName(exp.getName()) + ".png");
        boolean imageCreated;
        try {
            ImageIO.write(image, "png", imageFile);
            if (width != null) {
                width.v = image.getWidth();
            }
            imageCreated = true;
        } catch (IOException e) {
            e.printStackTrace();
            imageCreated = false;
        }
        StringBuilder string = new StringBuilder();
        string.append(getStartHTML());

        string.append("<h1>" + exp.getName() + "</h1>\n");
        if (imageCreated) {
            string.append("<img src=\"" + imageFile.getPath() + "\" alt=\"Image not available\" "
                    + "width=\"" + (int) (image.getWidth() * SIZE_MULTIPLIER + .5) + "\" "
                    + "height=\"" + (int) (image.getHeight() * SIZE_MULTIPLIER + .5) + "\"" + "/>\n");
        }
        string.append(getExpressionInfoHTML(exp));

        string.append(getEndHTML());

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
        Integer[] widths = new Integer[expressions.size()];

        File htmlFile = new File("ExpressionsHTMLToPDFConversionFile.html");
        retVal = createHTMLDocumentFromExpressions(htmlFile, expressions, widths);

        if (retVal) {
            try {
                OutputStream os = new FileOutputStream(file);
                PdfRendererBuilder builder = new PdfRendererBuilder();
                double max = Collections.max(Arrays.asList(widths)) / IMAGE_DPI + .5;
                double width = (max > PdfRendererBuilder.PAGE_SIZE_LETTER_WIDTH) ? max : PdfRendererBuilder.PAGE_SIZE_LETTER_WIDTH;
                builder.useDefaultPageSize((float) width, PdfRendererBuilder.PAGE_SIZE_LETTER_HEIGHT, PdfRendererBuilder.PageSizeUnits.INCHES);
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
        MutableInt width = new MutableInt(0);

        File htmlFile = new File("ExpressionHTMLToPDFConversionFile.html");
        retVal = createHTMLDocumentFromExpression(htmlFile, exp, width);

        if (retVal) {
            try {
                OutputStream os = new FileOutputStream(file);
                PdfRendererBuilder builder = new PdfRendererBuilder();
                double actualWidth = width.v / IMAGE_DPI + .5;
                builder.useDefaultPageSize((actualWidth > PdfRendererBuilder.PAGE_SIZE_LETTER_WIDTH)
                        ? (float) actualWidth : PdfRendererBuilder.PAGE_SIZE_LETTER_WIDTH,
                        PdfRendererBuilder.PAGE_SIZE_LETTER_HEIGHT, PdfRendererBuilder.PageSizeUnits.INCHES);
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
        createHTMLDocumentFromExpression(new File("test.html"), exp, null);
        createHTMLDocumentFromExpressions(new File("testgroup.html"), project.getTask().getTaskExpressionsOrdered(), null);
        createPDFFromExpression(new File("test.pdf"), exp);
        createPDFFromExpressions(new File("testgroup.pdf"), project.getTask().getTaskExpressionsOrdered());
    }

}
