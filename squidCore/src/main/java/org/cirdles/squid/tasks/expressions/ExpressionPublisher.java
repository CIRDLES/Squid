package org.cirdles.squid.tasks.expressions;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.antlr.v4.misc.MutableInt;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.tasks.TaskInterface;
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
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

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

            Transformer transformer = transformerFactory.newTransformer(new StreamSource("XSLTML/mmltex.xsl"));
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
        return formula.createBufferedImage(1, 20, Color.BLACK, Color.WHITE);
    }

    private static String getStartHTML() {
        return "<!DOCTYPE html>\n"
                + "<HTML lang=\"en\">\n<head>\n<meta charset=\"utf-8\"/>\n"
                + "<title>Squid Expressions</title>\n"
                + "<style>\n"
                + "h1   {color: black;}\n"
                + "h2   {color: black}\n"
                + "p    {color: black;}\n"
                + ".header {\n"
                + "    display: table;\n"
                + "    table-layout: auto;\n"
                + "}\n"
                + ".header > div {\n"
                + "    display: table-cell;\n"
                + "}"
                + ".left {\n"
                + "    font-size: 24px;\n"
                + "    font-family: arial;\n"
                + "    font-weight: bold;\n"
                + "    padding-right: 200px\n"
                + "}\n"
                + ".right {\n"
                + "    font-size: 20px;\n"
                + "    font-family: arial;\n"
                + "    font-weight: normal;\n"
                + "}\n"
                + "</style>\n"
                + "</head>\n<body>\n"
                + "<h1>Expressions</h1>\n";
    }

    private static String getEndHTML() {
        return "</body>\n</HTML>";
    }

    private static String getExpressionTopHTML(Expression exp) {
        return "<div class=\"header\">\n"
                + "<div class=\"left\">" + exp.getName() + "</div>\n"
                + "<div class=\"right\">\n" + (exp.isCustom() ? "Custom" : "Built-in") + "</div>\n"
                + "</div>\n"
                + "<label style=\"font: 18px arial\">Target: </label>\n"
                + "<input type=\"checkbox\" " + ((exp.isReferenceMaterialValue()) ? "checked=\"\"" : "") + " onClick=\"return false\"/>\n"
                + "<label>RefMat</label>"
                + "<input type=\"checkbox\" " + ((exp.getExpressionTree().isSquidSwitchSAUnknownCalculation()) ? "checked=\"\"" : "") + " onClick=\"return false\"/>\n"
                + "<label>Unknown</label>"
                + "<input type=\"checkbox\" " + ((exp.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()) ? "checked=\"\"" : "") + " onClick=\"return false\"/>\n"
                + "<label>Conc RefMat</label>"
                + "<label style=\"font: 18px arial\">Target:</label>"
                + "<input type=\"checkbox\" " + ((exp.isSquidSwitchNU()) ? "checked=\"\"" : "") + " onClick=\"return false\"/>\n"
                + "<label>NU</label>"
                + "<input type=\"checkbox\" " + ((exp.getExpressionTree().isSquidSwitchSCSummaryCalculation()) ? "checked=\"\"" : "") + " onClick=\"return false\"/>\n"
                + "<label>Summary<br/><br/></label>\n";
    }

    private static String getExpressionBottomHTML(Expression exp) {
        return getExpressionBottomHTML(exp, null);
    }

    private static String getExpressionBottomHTML(Expression exp, TaskInterface task) {
        return "<h3>Excel Expression String:</h3><p>" + exp.getExcelExpressionString() + "</p>\n"
                + "<h3>Notes:</h3><p>" + (exp.getNotes().trim().isEmpty() ? "No notes" : exp.getNotes()).replaceAll("\n", "<br/>") + "</p>\n"
                + ((task != null) ? "<pre>" + task.printExpressionRequiresGraph(exp)
                + "</pre>\n" + "<pre>" + task.printExpressionProvidesGraph(exp) + "</pre>" : "").replaceAll("\n", "<br/>");
    }

    public static boolean createHTMLDocumentFromExpressions(File file, List<Expression> expressions) {
        return createHTMLDocumentFromExpressions(file, expressions, null);
    }

    public static boolean createHTMLDocumentFromExpressions(File file, List<Expression> expressions, TaskInterface task) {
        return createHTMLDocumentFromExpressions(file, expressions, task, null);
    }

    public static boolean createHTMLDocumentFromExpressions(File file, List<Expression> expressions, TaskInterface task, Integer[] widths) {
        if (file != null && expressions != null) {
            boolean enterWidths = widths != null;

            StringBuilder string = new StringBuilder();
            string.append(getStartHTML());

            // File imageFolder = new File(file.getAbsolutePath() + "ExpressionsImages");
            //imageFolder.mkdir();

            for (int i = 0; i < expressions.size(); i++) {
                Expression exp = expressions.get(i);
                /*BufferedImage image = (BufferedImage) createImageFromMathML(exp.getExpressionTree().toStringMathML(), true);
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
                string.append(getExpressionTopHTML(exp));
                if (imageCreated) {
                    string.append("<img src=\"" + imageFile.getPath().replaceAll("\\\\", "/") + "\" alt=\"Image not available\" "
                            + "width=\"" + (int) (image.getWidth() * SIZE_MULTIPLIER + .5) + "\" "
                            + "height=\"" + (int) (image.getHeight() * SIZE_MULTIPLIER + .5) + "\"" + "/>\n");
                }
                string.append(getExpressionBottomHTML(exp, task));*/
                BufferedImage image = (BufferedImage) createImageFromMathML(exp.getExpressionTree().toStringMathML(), true);
                if (enterWidths) {
                    widths[i] = image.getWidth();
                }
                string.append(getFullExpressionHTML(image, exp, task));
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
        return false;
    }

    public static boolean createHTMLDocumentFromExpression(File file, Expression exp) {
        return createHTMLDocumentFromExpression(file, exp, null);
    }

    public static boolean createHTMLDocumentFromExpression(File file, Expression exp, TaskInterface task) {
        return createHTMLDocumentFromExpression(file, exp, task, null);
    }

    public static byte[] imageToByteArray(BufferedImage image) {
        byte[] retVal = null;
        if (image != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "png", baos);
                baos.flush();
                retVal = baos.toByteArray();
                baos.close();
            } catch (IOException e) {
            }
        }
        return retVal;
    }

    public static String getBase64Image(BufferedImage image) {
        byte[] bytes = imageToByteArray(image);
        return bytes == null ? "" : Base64.getEncoder().encodeToString(bytes);
    }

    public static String getSourceImageHTML(BufferedImage image) {
        return "data:image/png;base64, " + getBase64Image(image);
    }

    public static String getFullExpressionHTML(BufferedImage image, Expression exp, TaskInterface task) {
        return getExpressionTopHTML(exp) + "<img src=\"" + getSourceImageHTML(image) + "\" alt=\"Image not available\" "
                + "width=\"" + (int) (image.getWidth() * SIZE_MULTIPLIER + .5) + "\" "
                + "height=\"" + (int) (image.getHeight() * SIZE_MULTIPLIER + .5) + "\"" + "/>\n"
                + getExpressionBottomHTML(exp, task);
    }


    public static boolean createHTMLDocumentFromExpression(File file, Expression exp, TaskInterface task, MutableInt width) {
        if (file != null && exp != null) {
            /*BufferedImage image = (BufferedImage) createImageFromMathML(exp.getExpressionTree().toStringMathML(), true);
            File imageFile = new File(file.getAbsolutePath() + FileNameFixer.fixFileName(exp.getName()) + ".png");
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

            string.append(getExpressionTopHTML(exp));
            if (imageCreated) {
                string.append("<img src=\"" + imageFile.getPath() + "\" alt=\"Image not available\" "
                        + "width=\"" + (int) (image.getWidth() * SIZE_MULTIPLIER + .5) + "\" "
                        + "height=\"" + (int) (image.getHeight() * SIZE_MULTIPLIER + .5) + "\"" + "/>\n");
            }
            string.append(getExpressionBottomHTML(exp, task));*/
            StringBuilder string = new StringBuilder();

            string.append(getStartHTML());
            BufferedImage image = (BufferedImage) createImageFromMathML(exp.getExpressionTree().toStringMathML(), true);
            if (width != null) {
                width.v = image.getWidth();
            }
            string.append(getFullExpressionHTML(image, exp, task));
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
        return false;
    }

    public static boolean createPDFFromExpressions(File file, List<Expression> expressions) {
        boolean retVal;
        Integer[] widths = new Integer[expressions.size()];

        File htmlFile = new File("ExpressionsHTMLToPDFConversionFile.html");
        retVal = createHTMLDocumentFromExpressions(htmlFile, expressions, null, widths);

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
        retVal = createHTMLDocumentFromExpression(htmlFile, exp, null, width);

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
        //createHTMLDocumentFromExpressions(new File("testgroup.html"), project.getTask().getTaskExpressionsOrdered(), null);
        createPDFFromExpression(new File("test.pdf"), exp);
        //createPDFFromExpressions(new File("testgroup.pdf"), project.getTask().getTaskExpressionsOrdered());
    }

}
