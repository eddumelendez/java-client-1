package io.weaviate.client.v1.graphql.query.argument;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class NearThermalArgumentTest {

  @Test
  public void shouldBuildFromFile() throws IOException {
    String nearThermal = NearThermalArgument.builder()
      .thermalFile(NearMediaArgumentHelperTest.exampleMediaFile())
      .build().build();

    assertThat(nearThermal).isEqualTo(String.format("nearThermal:{thermal:\"%s\"}",
      NearMediaArgumentHelperTest.exampleMediaFileAsBase64()));
  }

  @Test
  public void shouldBuildFromFileWithCertainty() throws IOException {
    Float certainty = 0.5f;

    String nearThermal = NearThermalArgument.builder()
      .thermalFile(NearMediaArgumentHelperTest.exampleMediaFile())
      .certainty(certainty)
      .build().build();

    assertThat(nearThermal).isEqualTo(String.format("nearThermal:{thermal:\"%s\" certainty:%s}",
      NearMediaArgumentHelperTest.exampleMediaFileAsBase64(), certainty));
  }

  @Test
  public void shouldBuildFromFileWithDistance() throws IOException {
    Float distance = 0.5f;

    String nearThermal = NearThermalArgument.builder()
      .thermalFile(NearMediaArgumentHelperTest.exampleMediaFile())
      .distance(distance)
      .build().build();

    assertThat(nearThermal).isEqualTo(String.format("nearThermal:{thermal:\"%s\" distance:%s}",
      NearMediaArgumentHelperTest.exampleMediaFileAsBase64(), distance));
  }

  @Test
  public void shouldBuildFromBase64() {
    String thermalBase64 = "iVBORw0KGgoAAAANS";

    String nearThermal = NearThermalArgument.builder()
      .thermal(thermalBase64)
      .build().build();

    assertThat(nearThermal).isEqualTo(String.format("nearThermal:{thermal:\"%s\"}", thermalBase64));
  }

  @Test
  public void shouldBuildFromBase64WithHeader() {
    String thermalBase64 = "data:image/png;base64,iVBORw0KGgoAAAANS";

    String nearThermal = NearThermalArgument.builder()
      .thermal(thermalBase64)
      .build().build();

    assertThat(nearThermal).isEqualTo("nearThermal:{thermal:\"iVBORw0KGgoAAAANS\"}");
  }

  @Test
  public void shouldBuildFromBase64WithCertainty() {
    String thermalBase64 = "iVBORw0KGgoAAAANS";
    Float certainty = 0.5f;

    String nearThermal = NearThermalArgument.builder()
      .thermal(thermalBase64)
      .certainty(certainty)
      .build().build();

    assertThat(nearThermal).isEqualTo(String.format("nearThermal:{thermal:\"%s\" certainty:%s}", thermalBase64, certainty));
  }

  @Test
  public void shouldBuildFromBase64WithDistance() {
    String thermalBase64 = "iVBORw0KGgoAAAANS";
    Float distance = 0.5f;

    String nearThermal = NearThermalArgument.builder()
      .thermal(thermalBase64)
      .distance(distance)
      .build().build();

    assertThat(nearThermal).isEqualTo(String.format("nearThermal:{thermal:\"%s\" distance:%s}", thermalBase64, distance));
  }

  @Test
  public void shouldBuildEmptyDueToBadFile() {
    File badFile = new File("");

    String nearThermal = NearThermalArgument.builder()
      .thermalFile(badFile)
      .build().build();

    assertThat(nearThermal).isEqualTo("nearThermal:{}");
  }

  @Test
  public void shouldBuildEmptyDueToNotSet() {
    String nearThermal = NearThermalArgument.builder()
      .build().build();

    assertThat(nearThermal).isEqualTo("nearThermal:{}");
  }
}
