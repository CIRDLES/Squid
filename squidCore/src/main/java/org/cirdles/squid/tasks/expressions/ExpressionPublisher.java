package org.cirdles.squid.tasks.expressions;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import com.google.common.io.CharStreams;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.utilities.stateUtilities.SquidSerializer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import javax.imageio.ImageIO;
import org.scilab.forge.jlatexmath.TeXFormula;

public class ExpressionPublisher {

    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    public static String removeMathMLStyling(String input) {
       // System.out.println("Input:\n" + input);
        String output = input.replaceAll("(<.*(mstyle|mspace).*>|&nbsp;)", "");
        //output = output.replaceAll("&nbsp;", "&#160;");
        output = output.replaceAll("&times;", "*");
       // System.out.println("Output:\n" + output);
        return output;
    }

    public static String getLatexFromMathML(String input, boolean hasStyling) {
        String retVal;
        if (hasStyling) {
            input = removeMathMLStyling(input);
        }
        try { /*StringWriter resultWriter = new StringWriter();
            StreamResult result = new StreamResult(resultWriter);
            StringReader inputReader = new StringReader(input);
            Source inputSource = new StreamSource(inputReader);*/

            File inFile = new File("infile.xml");            
            File outFile = new File("outfile.txt");

            PrintWriter writer = new PrintWriter(new FileOutputStream(inFile));
            writer.write("<math xmlns=\"http://www.w3.org/1998/Math/MathML\" display=\"block\">" + input + "</math>");
            writer.flush();

            Transformer transformer = transformerFactory.newTransformer(new StreamSource("xsltml_2.0/mmltex.xsl"));
            transformer.transform(new StreamSource(inFile), new StreamResult(outFile));            

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(outFile)));

            retVal = CharStreams.toString(reader);
            retVal = retVal.replaceAll("\\%", "\\\\%");
            //retVal = retVal.replaceAll("\\\\", "\\\\\\\\");
            System.out.println("OutFile:" + retVal);
        } catch (TransformerException | IOException e) {
            retVal = "Transformer Exception";
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return retVal;
    }

    public static Image createImageFromMathML(String input, boolean hasStyling) {
        String converted = getLatexFromMathML(input, hasStyling);
        TeXFormula formula = new TeXFormula(converted);
        //System.out.println(converted.compareTo("$\\sqrt{{\\left(\\frac{\\text{Hfsens1sigabs[0]}}{\\text{Hfsens[0]}}\\right)}^{2}+{\\left(\\frac{{\\left(\\frac{{\\text{195.9}}_{\\text{HfO}}^{\\text{}}}{{\\text{195.8}}_{\\text{Zr2O}}^{\\text{}}}\\right)}^{\\text{\\%}}}{100}\\right)}^{2}}*\\text{Hfppm[0]}$"));
        return formula.createBufferedImage(1, 50, Color.BLUE, Color.LIGHT_GRAY);
    }

    public static void main(String[] argv) {

        /*String xmlExpressionFileName = "Hf1sabserr.xml";
        String xsltExpressionTransformer = "mmltex.xsl";
        // Create a transform factory instance.
        TransformerFactory tfactory = TransformerFactory.newInstance();
        try {
            // Create a transformer for the stylesheet.
            Transformer transformer
                    = tfactory.newTransformer(new StreamSource(xsltExpressionTransformer));
            // Transform the source XML to System.out.
            transformer.transform(new StreamSource(xmlExpressionFileName),
                    new StreamResult(System.out));
            transformer.transform(new StreamSource(xmlExpressionFileName),
                    new StreamResult("LATEX.txt"));
        } catch (TransformerException transformerException) {
        }
        String converted = "$\\sqrt{{\\left(\\frac{\\text{Hfsens1sigabs[0]}}{\\text{Hfsens[0]}}\\right)}^{2}+{\\left(\\frac{{\\left(\\frac{{\\text{195.9}}_{\\text{HfO}}^{\\text{}}}{{\\text{195.8}}_{\\text{Zr2O}}^{\\text{}}}\\right)}^{\\text{\\%}}}{100}\\right)}^{2}}*\\text{Hfppm[0]}$";
        TeXFormula formula = new TeXFormula(converted);
        formula.createJPEG(1, 50, "LATEX.jpeg", Color.white, Color.black);*/
        ResourceExtractor squidProjectExtractor = new ResourceExtractor(SquidProject.class);
        File squidFile = squidProjectExtractor.extractResourceAsFile("Z626611PKPERM1.squid");
        SquidProject project = (SquidProject) SquidSerializer.getSerializedObjectFromFile(squidFile.getAbsolutePath(), false);
        Expression exp = project.getTask().getExpressionByName("Hf1sabserr");
        //Expression exp = (Expression) (new Expression()).readXMLObject("Hf1sabserr.xml", false);

        BufferedImage image = (BufferedImage) createImageFromMathML(exp.getExpressionTree().toStringMathML(), true);
        try {
        ImageIO.write(image, "jpeg", new File("test.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}