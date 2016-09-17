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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import se.llbit.json.JsonObject;
import se.llbit.util.Util;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class PluginDetailsController implements Initializable {
  @FXML Button saveButton;
  @FXML Button cancelButton;
  @FXML Button browseButton;
  @FXML CheckBox enabled;
  @FXML TextField mainClass;
  @FXML TextField jarPath;

  private Consumer<JsonObject> onAccept = result -> {};

  @Override public void initialize(URL location, ResourceBundle resources) {
    saveButton.setOnAction(event -> {
      JsonObject plugin = new JsonObject();
      plugin.add("jar", jarPath.getText());
      plugin.add("main", mainClass.getText());
      plugin.add("enabled", enabled.isSelected());
      File jarFile = new File(jarPath.getText());
      plugin.add("md5", Util.md5sum(jarFile));
      onAccept.accept(plugin);
      saveButton.getScene().getWindow().hide();
    });
    cancelButton.setOnAction(event -> cancelButton.getScene().getWindow().hide());
    browseButton.setOnAction(event -> {
      FileChooser chooser = new FileChooser();
      chooser.setTitle("Select Plugin Jar");
      chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Jar files", "*.jar"));
      File result = chooser.showOpenDialog(browseButton.getScene().getWindow());
      if (result != null) {
        jarPath.setText(result.getAbsolutePath());
      }
    });
  }

  public void setPluginDetails(JsonObject plugin) {
    jarPath.setText(plugin.get("jar").stringValue(""));
    mainClass.setText(plugin.get("main").stringValue(""));
    enabled.setSelected(plugin.get("enabled").boolValue(true));
  }

  public void setOnAccept(Consumer<JsonObject> onAccept) {
    this.onAccept = onAccept;
  }
}
