package org.cirdles.squid.tasks.expressions;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.antlr.v4.misc.MutableInt;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.parameters.util.DateHelper;
import org.cirdles.squid.projects.SquidProject;
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
import static org.cirdles.squid.utilities.fileUtilities.CalamariFileUtilities.XSLTMLFolder;

public class ExpressionPublisher {

    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private static final double IMAGE_DPI = 200.0;
    private static final double SIZE_MULTIPLIER = .4;

    private static String removeMathMLStyling(String input) {
        String output = input.replaceAll("(<.*(mstyle|mspace).*>|&nbsp;)", "");
        output = output.replaceAll("&times;", "*");
        output = output.replaceAll("&ExponentialE;", "e");
        return output;
    }

    private static String getLatexFromMathML(String input, boolean hasStyling) {
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

            Transformer transformer = transformerFactory.newTransformer(
                    new StreamSource(XSLTMLFolder.getAbsolutePath() + File.separator + "mmltex.xsl"));
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

    private static String getStartHTML(boolean isGroup) {
        return "<!DOCTYPE html>\n"
                + "<HTML lang=\"en\">\n<head>\n" +
                "    <meta charset=\"utf-8\"/>\n" +
                "    <title>Squid Expressions</title>\n" +
                "    <style>\n" +
                "        h1 {\n" +
                "            padding-left: 30px;\n" +
                "        }\n" +
                "\n" +
                "        h2 {\n" +
                "            padding-left: 30px;\n" +
                "        }\n" +
                "\n" +
                "        p {\n" +
                "            padding-left: 40px;\n" +
                "        }\n" +
                "\n" +
                "        h3 {\n" +
                "            padding-left: 30px;\n" +
                "        }\n" +
                "\n" +
                "        pre {\n" +
                "            padding-left: 40px;\n" +
                "        }\n" +
                "\n" +
                "        footer {\n" +
                "            padding-left: 40px;\n" +
                "        }\n" +
                "\n" +
                "        .header {\n" +
                "            display: table;\n" +
                "            table-layout: auto;\n" +
                "            padding-left: 30px;\n" +
                "            padding-bottom: 15px;\n" +
                "        }\n" +
                "\n" +
                "        .header > div {\n" +
                "            display: table-cell;\n" +
                "        }\n" +
                "\n" +
                "        .left {\n" +
                "            font-size: 24px;\n" +
                "            font-family: arial;\n" +
                "            font-weight: bold;\n" +
                "            padding-right: 200px;\n" +
                "        }\n" +
                "\n" +
                "        .right {\n" +
                "            font-size: 20px;\n" +
                "            font-family: arial;\n" +
                "            font-weight: normal;\n" +
                "        }\n" +
                "\n" +
                "        img {\n" +
                "            border: 1px solid black;\n" +
                "            padding: 5px;\n" +
                "            margin-left: 40px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                ((isGroup) ? "<h1>Expressions</h1>\n" : "");
    }

    private static String getEndHTML() {
        return "</body>\n</HTML>";
    }

    private static String getExpressionTopHTML(Expression exp) {
        return "<div class=\"header\">\n"
                + "    <div class=\"left\">" + exp.getName() + "</div>\n"
                + "    <div class=\"right\">" + (exp.isCustom() ? "Custom" : "Built-in") + "</div>\n"
                + "</div>\n"
                + "<label style=\"font: 18px arial; margin-left: 30px;\">Target:</label>\n"
                + "<input type=\"checkbox\" " + ((exp.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()) ? "checked=\"\"" : "") + " onClick=\"return false\"/>\n"
                + "<label>RefMat</label>\n"
                + "<input type=\"checkbox\" " + ((exp.getExpressionTree().isSquidSwitchSAUnknownCalculation()) ? "checked=\"\"" : "") + " onClick=\"return false\"/>\n"
                + "<label>" + ((exp.isCustom()) ? "sample: " + exp.getExpressionTree().getUnknownsGroupSampleName().replaceAll(Squid3Constants.SpotTypes.UNKNOWN.getSpotTypeName(), "all") : "Unknown") + "</label>\n"
                + "<input type=\"checkbox\" " + ((exp.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()) ? "checked=\"\"" : "") + " onClick=\"return false\"/>\n"
                + "<label>Conc RefMat</label>\n"
                + "<label style=\"font: 18px arial; padding-left: 5px;\">Type:</label>\n"
                + "<input type=\"checkbox\" " + ((exp.isSquidSwitchNU()) ? "checked=\"\"" : "") + " onClick=\"return false\"/>\n"
                + "<label>NU</label>\n"
                + "<input type=\"checkbox\" " + ((exp.getExpressionTree().isSquidSwitchSCSummaryCalculation()) ? "checked=\"\"" : "") + " onClick=\"return false\"/>\n"
                + "<label>Summary<br/><br/></label>\n";
    }

    private static String getExpressionBottomHTML(Expression exp) {
        return getExpressionBottomHTML(exp, null);
    }

    private static String getExpressionBottomHTML(Expression exp, SquidProject project) {
        return "<h3>Excel Expression String:</h3>\n<p>" + exp.getExcelExpressionString() + "</p>\n"
                + "<h3>Notes:</h3>\n<p>" + (exp.getNotes().trim().isEmpty() ? "No notes" : exp.getNotes()).replaceAll("\n", "<br/>") + "</p>\n"
                + ((project != null && project.getTask() != null) ? "<h3>Dependencies:</h3>\n" + "<pre>" + project.getTask().printExpressionRequiresGraph(exp).replaceAll("\n", "<br/>")
                + "</pre>\n" + "<pre>" + project.getTask().printExpressionProvidesGraph(exp).replaceAll("\n", "<br/>") + "</pre>\n" : "");
    }

    private static String getExpressionFooterHTML(Expression exp, SquidProject project) {
        return "<footer>\n"
                + "    <label>Generated on: " + DateHelper.getCurrentDate() + "</label>\n" +
                ((project != null) ?
                        ((project.getTask() != null) ?
                                ((project.getTask().getName() != null && !project.getTask().getName().isEmpty()) ? "    <label>| Task Name: " + project.getTask().getName() + "</label>\n" : "")
                                        + ((project.getTask().getLabName() != null && !project.getTask().getLabName().isEmpty()) ? "    <label>| Lab Name: " + project.getTask().getLabName() + "</label>\n" : "")
                                        + ((project.getTask().getAuthorName() != null && !project.getTask().getAuthorName().isEmpty()) ? "    <label>| Author: " + project.getTask().getAuthorName() + "</label>\n" : "") : "")
                                + ((project.getProjectName() != null && !project.getProjectName().isEmpty()) ? "    <label>| Project Name: " + project.getProjectName() + "</label>\n" : "")
                                + ((project.getAnalystName() != null && !project.getAnalystName().isEmpty()) ? "    <label>| Analyst Name: " + project.getAnalystName() + "</label>\n" : "") : "")
                + "</footer>\n";

    }

    public static boolean createHTMLDocumentFromExpressions(File file, List<Expression> expressions, SquidProject project, Integer[] widths) {
        if (file != null && expressions != null) {
            boolean enterWidths = widths != null;

            StringBuilder string = new StringBuilder();
            string.append(getStartHTML(true));

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
                string.append(getFullExpressionHTML(image, exp, project));
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

    private static byte[] imageToByteArray(BufferedImage image) {
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

    private static String getBase64Image(BufferedImage image) {
        byte[] bytes = imageToByteArray(image);
        return bytes == null ? "" : Base64.getEncoder().encodeToString(bytes);
    }

    private static String getSourceImageHTML(BufferedImage image) {
        return "data:image/png;base64," + getBase64Image(image);
    }

    private static String getFullExpressionHTML(BufferedImage image, Expression exp, SquidProject project) {
        return getExpressionTopHTML(exp) + "<img src=\"" + getSourceImageHTML(image) + "\" alt=\"Image not available\" "
                + "width=\"" + (int) (image.getWidth() * SIZE_MULTIPLIER + .5) + "\" "
                + "height=\"" + (int) (image.getHeight() * SIZE_MULTIPLIER + .5) + "\"" + "/>\n"
                + getExpressionBottomHTML(exp, project)
                + getExpressionFooterHTML(exp, project);
    }

    public static boolean createHTMLDocumentFromExpression(File file, Expression exp, SquidProject project) {
        return createHTMLDocumentFromExpression(file, exp, project, null);
    }

    public static boolean createHTMLDocumentFromExpression(File file, Expression exp, SquidProject project, MutableInt width) {
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

            string.append(getStartHTML(false));
            BufferedImage image = (BufferedImage) createImageFromMathML(exp.getExpressionTree().toStringMathML(), true);
            if (width != null) {
                width.v = image.getWidth();
            }
            string.append(getFullExpressionHTML(image, exp, project));
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

}
