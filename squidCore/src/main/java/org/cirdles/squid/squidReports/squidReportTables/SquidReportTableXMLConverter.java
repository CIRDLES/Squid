package org.cirdles.squid.squidReports.squidReportTables;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategory;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategoryInterface;
import org.cirdles.squid.tasks.Task;

import java.util.LinkedList;

public class SquidReportTableXMLConverter implements Converter {
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        SquidReportTableInterface table = (SquidReportTableInterface) source;

        writer.startNode("reportTableName");
        writer.setValue(table.getReportTableName());
        writer.endNode();

        writer.startNode("reportCategories");
        LinkedList<SquidReportCategoryInterface> reportCategories = table.getReportCategories();
        for (SquidReportCategoryInterface cat : reportCategories) {
            writer.startNode("reportCategory");
            context.convertAnother(cat);
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("isDefault");
        writer.setValue(Boolean.toString(table.isDefault()));
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        SquidReportTableInterface table = SquidReportTable.createEmptySquidReportTable("");

        reader.moveDown();
        table.setReportTableName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            SquidReportCategoryInterface cat = SquidReportCategory.createReportCategory("");
            cat = (SquidReportCategoryInterface) context.convertAnother(cat, SquidReportCategory.class);
            table.getReportCategories().add(cat);
            reader.moveUp();
        }
        reader.moveUp();

        reader.moveDown();
        table.setIsBuiltInSquidDefault(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();

        return table;
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(SquidReportTable.class);
    }
}
