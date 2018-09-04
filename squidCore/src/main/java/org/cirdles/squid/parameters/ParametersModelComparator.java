package org.cirdles.squid.parameters;

import org.cirdles.squid.parameters.parameterModels.ParametersModel;

import java.util.Comparator;

public class ParametersModelComparator implements Comparator<ParametersModel> {
    @Override
    public int compare(ParametersModel o1, ParametersModel o2) {
        return o1.compareTo(o2);
    }
}
