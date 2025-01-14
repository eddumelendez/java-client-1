package io.weaviate.client.v1.graphql.query.argument;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class NearAudioArgumentTest {

  @Test
  public void shouldBuildFromFile() throws IOException {
    String nearAudio = NearAudioArgument.builder()
      .audioFile(NearMediaArgumentHelperTest.exampleMediaFile())
      .build().build();

    assertThat(nearAudio).isEqualTo(String.format("nearAudio:{audio:\"%s\"}",
      NearMediaArgumentHelperTest.exampleMediaFileAsBase64()));
  }

  @Test
  public void shouldBuildFromFileWithCertainty() throws IOException {
    Float certainty = 0.5f;

    String nearAudio = NearAudioArgument.builder()
      .audioFile(NearMediaArgumentHelperTest.exampleMediaFile())
      .certainty(certainty)
      .build().build();

    assertThat(nearAudio).isEqualTo(String.format("nearAudio:{audio:\"%s\" certainty:%s}",
      NearMediaArgumentHelperTest.exampleMediaFileAsBase64(), certainty));
  }

  @Test
  public void shouldBuildFromFileWithDistance() throws IOException {
    Float distance = 0.5f;

    String nearAudio = NearAudioArgument.builder()
      .audioFile(NearMediaArgumentHelperTest.exampleMediaFile())
      .distance(distance)
      .build().build();

    assertThat(nearAudio).isEqualTo(String.format("nearAudio:{audio:\"%s\" distance:%s}",
      NearMediaArgumentHelperTest.exampleMediaFileAsBase64(), distance));
  }

  @Test
  public void shouldBuildFromBase64() {
    String audioBase64 = "iVBORw0KGgoAAAANS";

    String nearAudio = NearAudioArgument.builder()
      .audio(audioBase64)
      .build().build();

    assertThat(nearAudio).isEqualTo(String.format("nearAudio:{audio:\"%s\"}", audioBase64));
  }

  @Test
  public void shouldBuildFromBase64WithHeader() {
    String audioBase64 = "data:audio/mp4;base64,iVBORw0KGgoAAAANS";

    String nearAudio = NearAudioArgument.builder()
      .audio(audioBase64)
      .build().build();

    assertThat(nearAudio).isEqualTo("nearAudio:{audio:\"iVBORw0KGgoAAAANS\"}");
  }

  @Test
  public void shouldBuildFromBase64WithCertainty() {
    String audioBase64 = "iVBORw0KGgoAAAANS";
    Float certainty = 0.5f;

    String nearAudio = NearAudioArgument.builder()
      .audio(audioBase64)
      .certainty(certainty)
      .build().build();

    assertThat(nearAudio).isEqualTo(String.format("nearAudio:{audio:\"%s\" certainty:%s}", audioBase64, certainty));
  }

  @Test
  public void shouldBuildFromBase64WithDistance() {
    String audioBase64 = "iVBORw0KGgoAAAANS";
    Float distance = 0.5f;

    String nearAudio = NearAudioArgument.builder()
      .audio(audioBase64)
      .distance(distance)
      .build().build();

    assertThat(nearAudio).isEqualTo(String.format("nearAudio:{audio:\"%s\" distance:%s}", audioBase64, distance));
  }

  @Test
  public void shouldBuildEmptyDueToBadFile() {
    File badFile = new File("");

    String nearAudio = NearAudioArgument.builder()
      .audioFile(badFile)
      .build().build();

    assertThat(nearAudio).isEqualTo("nearAudio:{}");
  }

  @Test
  public void shouldBuildEmptyDueToNotSet() {
    String nearAudio = NearAudioArgument.builder()
      .build().build();

    assertThat(nearAudio).isEqualTo("nearAudio:{}");
  }
}
