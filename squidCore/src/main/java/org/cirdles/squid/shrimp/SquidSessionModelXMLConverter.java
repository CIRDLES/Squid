/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.shrimp;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.ArrayList;
import java.util.List;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSessionModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;

/**
 *
 * @author ryanb
 */
public class SquidSessionModelXMLConverter implements Converter {

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
        SquidSessionModel model = (SquidSessionModel) o;

        writer.startNode("squidSpeciesModelList");
        context.convertAnother(model.getSquidSpeciesModelList());
        writer.endNode();

        writer.startNode("squidRatiosModelList");
        context.convertAnother(model.getSquidRatiosModelList());
        writer.endNode();

        writer.startNode("useSBM");
        writer.setValue(Boolean.toString(model.isUseSBM()));
        writer.endNode();

        writer.startNode("userLinFits");
        writer.setValue(Boolean.toString(model.isUserLinFits()));
        writer.endNode();

        writer.startNode("indexOfBackgroundSpecies");
        writer.setValue(Integer.toString(model.getIndexOfBackgroundSpecies()));
        writer.endNode();

        writer.startNode("referenceMaterialNameFilter");
        writer.setValue(model.getReferenceMaterialNameFilter());
        writer.endNode();

        writer.startNode("concentrationReferenceMaterialNameFilter");
        writer.setValue(model.getConcentrationReferenceMaterialNameFilter());
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        List<SquidSpeciesModel> squidSpeciesModels = new ArrayList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            SquidSpeciesModel squidSpeciesModel = new SquidSpeciesModel();
            squidSpeciesModel = (SquidSpeciesModel) context.convertAnother(squidSpeciesModel, SquidSpeciesModel.class);
            squidSpeciesModels.add(squidSpeciesModel);
            reader.moveUp();
        }
        reader.moveUp();

        List<SquidRatiosModel> squidRatiosModels = new ArrayList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            Object squidRatiosModel = new Object();
            squidRatiosModel = (SquidRatiosModel) context.convertAnother(squidRatiosModel, SquidRatiosModel.class);
            squidRatiosModels.add((SquidRatiosModel) squidRatiosModel);
            reader.moveUp();
        }
        reader.moveUp();

        reader.moveDown();
        boolean useSBM = Boolean.parseBoolean(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        boolean userLinFits = Boolean.parseBoolean(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        int indexOfBackgroundSpecies = Integer.parseInt(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        String referenceMaterialNameFilter = reader.getValue();
        reader.moveUp();

        reader.moveDown();
        String concentrationReferenceMaterialNameFilter = reader.getValue();
        reader.moveUp();

        return new SquidSessionModel(squidSpeciesModels, squidRatiosModels, useSBM, userLinFits, indexOfBackgroundSpecies,
                referenceMaterialNameFilter, concentrationReferenceMaterialNameFilter);
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(SquidSessionModel.class);
    }

}
