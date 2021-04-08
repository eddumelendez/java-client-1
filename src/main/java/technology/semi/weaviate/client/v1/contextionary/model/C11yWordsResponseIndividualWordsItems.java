package technology.semi.weaviate.client.v1.contextionary.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class C11yWordsResponseIndividualWordsItems {
  C11yWordsResponseIndividualWordsItems0Info info;
  Boolean present;
  String word;
}
