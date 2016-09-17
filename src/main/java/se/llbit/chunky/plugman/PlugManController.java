/* Copyright (c) 2016, Jesper Öqvist <jesper@llbit.se>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *
 *   3. The name of the author may not be used to endorse or promote
 *      products derived from this software without specific prior
 *      written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package se.llbit.chunky.plugman;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import se.llbit.chunky.PersistentSettings;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PlugManController implements Initializable {
  @FXML protected Button saveButton;
  @FXML protected Button addButton;
  @FXML protected Button removeButton;
  @FXML protected Button editButton;
  @FXML protected Button upButton;
  @FXML protected Button downButton;
  @FXML protected TableView<JsonObject> tableView;
  @FXML protected TableColumn<JsonObject, String> pluginColumn;
  @FXML protected TableColumn<JsonObject, Boolean> enabledColumn;

  @Override public void initialize(URL location, ResourceBundle resources) {
    saveButton.setOnAction(e -> {
      JsonArray array = new JsonArray();
      tableView.getItems().forEach(array::addElement);
      PersistentSettings.setPlugins(array);
      saveButton.getScene().getWindow().hide();
    });
    addButton.setOnAction(e -> {
      try {
        PluginDetails pluginDetails = new PluginDetails(new JsonObject(),
            result -> tableView.getItems().add(result));
        pluginDetails.show();
      } catch (IOException e1) {
        throw new Error(e1);
      }
    });
    editButton.setOnAction(e -> {
      try {
        JsonObject selectedPlugin = tableView.getSelectionModel().getSelectedItem();
        if (selectedPlugin != null) {
          PluginDetails pluginDetails = new PluginDetails(selectedPlugin,
              result -> {
                int prevIndex = tableView.getItems().indexOf(selectedPlugin);
                tableView.getItems().remove(prevIndex);
                tableView.getItems().add(prevIndex, result);
                tableView.getSelectionModel().select(result);
              });
          pluginDetails.show();
        }
      } catch (IOException e1) {
        throw new Error(e1);
      }
    });
    removeButton.setOnAction(
        event -> tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem()));
    upButton.setOnAction(event -> {
      int selected = tableView.getSelectionModel().getSelectedIndex();
      if (selected > 0) {
        JsonObject item = tableView.getItems().remove(selected);
        tableView.getItems().add(selected - 1, item);
        tableView.getSelectionModel().select(selected - 1);
      }
    });
    downButton.setOnAction(event -> {
      int selected = tableView.getSelectionModel().getSelectedIndex();
      if (selected >= 0 && (selected + 1) < tableView.getItems().size()) {
        JsonObject item = tableView.getItems().remove(selected);
        tableView.getItems().add(selected + 1, item);
        tableView.getSelectionModel().select(selected + 1);
      }
    });

    pluginColumn.setCellValueFactory(
        data -> new ReadOnlyStringWrapper(data.getValue().get("jar").stringValue("")));
    enabledColumn.setCellValueFactory(
        data -> new ReadOnlyBooleanWrapper(data.getValue().get("enabled").boolValue(true)));

    JsonValue plugins = PersistentSettings.getPlugins();
    for (JsonValue pluginValue : plugins.array().getElementList()) {
      JsonObject plugin = pluginValue.object();
      tableView.getItems().add(plugin);
    }
  }
}
