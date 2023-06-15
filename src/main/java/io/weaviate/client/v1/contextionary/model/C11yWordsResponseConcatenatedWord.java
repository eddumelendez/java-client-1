package io.weaviate.client.v1.contextionary.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class C11yWordsResponseConcatenatedWord {
  C11yNearestNeighbor[] concatenatedNearestNeighbors;
  Float[] concatenatedVector;
  String concatenatedWord;
  String[] singleWords;
}
