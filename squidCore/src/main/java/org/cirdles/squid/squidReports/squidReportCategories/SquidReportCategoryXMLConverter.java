package org.cirdles.squid.squidReports.squidReportCategories;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumn;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnInterface;

import java.util.LinkedList;

public class SquidReportCategoryXMLConverter implements Converter {
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        SquidReportCategoryInterface cat = (SquidReportCategoryInterface) source;

        writer.startNode("displayName");
        writer.setValue(cat.getDisplayName());
        writer.endNode();

        writer.startNode("categoryColumns");
        LinkedList<SquidReportColumnInterface> cols = cat.getCategoryColumns();
        for (SquidReportColumnInterface col : cols) {
            writer.startNode("SquidReportColumn");
            context.convertAnother(col);
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("visible");
        writer.setValue(Boolean.toString(cat.isVisible()));
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        reader.moveDown();
        SquidReportCategoryInterface cat = SquidReportCategory.createReportCategory(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        LinkedList<SquidReportColumnInterface> cols = cat.getCategoryColumns();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            SquidReportColumnInterface col = SquidReportColumn.createSquidReportColumn("");
            col = (SquidReportColumnInterface) context.convertAnother(col, SquidReportColumn.class);
            cols.add(col);
            reader.moveUp();
        }
        reader.moveUp();

        reader.moveDown();
        cat.setVisible(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();

        return cat;
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(SquidReportCategory.class);
    }
}
