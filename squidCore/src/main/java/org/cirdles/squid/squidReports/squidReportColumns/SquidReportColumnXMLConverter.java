package org.cirdles.squid.squidReports.squidReportColumns;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class SquidReportColumnXMLConverter implements Converter {

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        SquidReportColumnInterface col = (SquidReportColumnInterface) source;

        writer.startNode("expressionName");
        writer.setValue(col.getExpressionName());
        writer.endNode();

        writer.startNode("units");
        writer.setValue(col.getUnits());
        writer.endNode();

        writer.startNode("uncertaintyColumn");
        if (col.getUncertaintyColumn() != null) {
            context.convertAnother(col.getUncertaintyColumn());
        } else {
            writer.setValue("");
        }
        writer.endNode();

        writer.startNode("amUncertaintyColumn");
        writer.setValue(Boolean.toString(col.isAmUncertaintyColumn()));
        writer.endNode();

        writer.startNode("uncertaintyDirective");
        writer.setValue(col.getUncertaintyDirective());
        writer.endNode();

        writer.startNode("countOfSignificantDigits");
        writer.setValue(Integer.toString(col.getCountOfSignificantDigits()));
        writer.endNode();

        writer.startNode("visible");
        writer.setValue(Boolean.toString(col.isVisible()));
        writer.endNode();

        writer.startNode("footnoteSpec");
        writer.setValue(col.getFootnoteSpec());
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        reader.moveDown();
        SquidReportColumnInterface col = SquidReportColumn.createSquidReportColumn(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        col.setUnits(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        if ((reader.getValue().length() > 0) && (reader.getValue().compareToIgnoreCase("null") != 0)) {
            SquidReportColumnInterface uncertaintyColumn = SquidReportColumn.createSquidReportColumn("");
            uncertaintyColumn = (SquidReportColumnInterface) context.convertAnother(uncertaintyColumn, SquidReportColumn.class);
            col.setUncertaintyColumn(uncertaintyColumn);
        }
        reader.moveUp();

        reader.moveDown();
        col.setAmUncertaintyColumn(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        col.setUncertaintyDirective(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        col.setCountOfSignificantDigits(Integer.parseInt(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        col.setVisible(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        col.setFootnoteSpec(reader.getValue());
        reader.moveUp();

        return col;
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(SquidReportColumn.class);
    }
}