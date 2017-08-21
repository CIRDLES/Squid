/*
 * Copyright 2017 cirdles.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.squid.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ListCell;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.PrawnFile.Run;

/**
 *
 * @author James F. Bowring
 */
public class RunsViewModel {

    public RunsViewModel() {
    }

    private final ObservableList<PrawnFile.Run> shrimpRuns
            = FXCollections.observableArrayList();

    public ReadOnlyObjectProperty<ObservableList<PrawnFile.Run>> shrimpRunsProperty() {
        return new SimpleObjectProperty<>(shrimpRuns);
    }

    private final FilteredList<PrawnFile.Run> viewableShrimpRuns = new FilteredList<>(shrimpRuns);

    public ReadOnlyObjectProperty<ObservableList<PrawnFile.Run>> viewableShrimpRunsProperty() {
        return new SimpleObjectProperty<>(viewableShrimpRuns);
    }

    public ObjectProperty<Predicate<? super PrawnFile.Run>> filterProperty() {
        return viewableShrimpRuns.predicateProperty();
    }

    public void addRunsList(List<PrawnFile.Run> myShrimpRuns) {
        shrimpRuns.clear();
        viewableShrimpRuns.clear();
        this.shrimpRuns.addAll(myShrimpRuns);
    }

    /**
     * Remove a run (ShrimpRun or spot) from the model
     *
     * Will also affect viewableShrimpRuns if the Player being removed adheres
     * to the filter
     *
     * @param run - the run to remove
     */
    public void remove(Run run) {
        shrimpRuns.remove(run);
    }

    public String showFilteredOverAllCount() {
        return " : " + viewableShrimpRuns.size() + " / " + shrimpRuns.size() + " shown";
    }

    static class SpotNameMatcher implements Predicate<PrawnFile.Run> {

        private final String spotName;

        public SpotNameMatcher(String spotName) {
            this.spotName = spotName;
        }

        @Override
        public boolean test(PrawnFile.Run run) {
            return run.getPar().get(0).getValue().startsWith(spotName);
        }
    }

    static class ShrimpFractionListCell extends ListCell<PrawnFile.Run> {

        @Override
        protected void updateItem(PrawnFile.Run run, boolean empty) {
            super.updateItem(run, empty);
            if (run == null || empty) {
                setText(null);
            } else {
                setText(
                        String.format("%1$-" + 20 + "s", run.getPar().get(0).getValue()) // name
                        + String.format("%1$-" + 12 + "s", run.getSet().getPar().get(0).getValue())//date
                        + String.format("%1$-" + 12 + "s", run.getSet().getPar().get(1).getValue()) //time
                        + String.format("%1$-" + 6 + "s", run.getPar().get(2).getValue()) //peaks
                        + String.format("%1$-" + 6 + "s", run.getPar().get(3).getValue())); //scans
            }
        }
    };

    /**
     * @return the viewableShrimpRuns
     */
    public ObservableList<PrawnFile.Run> getViewableShrimpRuns() {
        List<PrawnFile.Run> viewableShrimpRunsCopy = new ArrayList<>();
        for (int i = 0; i < viewableShrimpRuns.size(); i++) {
            viewableShrimpRunsCopy.add(viewableShrimpRuns.get(i));
        }

        return FXCollections.observableArrayList(viewableShrimpRunsCopy);
    }
}
