package io.weaviate.integration.client.batch;

import io.weaviate.client.Config;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.batch.model.ObjectGetResponse;
import io.weaviate.client.v1.data.model.WeaviateObject;
import io.weaviate.client.v1.schema.model.DataType;
import io.weaviate.client.v1.schema.model.Property;
import io.weaviate.client.v1.schema.model.WeaviateClass;
import io.weaviate.integration.client.WeaviateContainer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.ARRAY;
import org.junit.Test;

public class ClientBatchGrpcVectorBytesTest {

  @Test
  public void shouldSendVectorWith_v1_22_5() {
    testWeaviate("semitechnologies/weaviate:1.22.5");
  }

  @Test
  public void shouldSendVectorWith_v1_22_6() {
    testWeaviate("semitechnologies/weaviate:1.22.6");
  }

  @Test
  public void shouldSendVectorWith_v1_23_0() {
    testWeaviate("semitechnologies/weaviate:1.23.0");
  }

  @Test
  public void shouldSendVectorWith_v1_23_1() {
    testWeaviate("semitechnologies/weaviate:1.23.1");
  }

  private void testWeaviate(String image) {
    WeaviateContainer.DockerContainer container = WeaviateContainer.create(image);
    try {
      container.start();
      // create client
      Config config = new Config("http", "localhost:" + container.getMappedPort(8080));
      config.setGRPCSecured(false);
      config.setGRPCHost("localhost:" + container.getMappedPort(50051));
      WeaviateClient client = new WeaviateClient(config);
      // create schema
      String className = "NoVectorizer";
      List<Property> properties = Collections.singletonList(
        Property.builder()
          .name("name")
          .dataType(Collections.singletonList(DataType.TEXT))
          .build());
      Result<Boolean> createResult = client.schema().classCreator()
        .withClass(WeaviateClass.builder()
          .className(className)
          .properties(properties)
          .build()
        )
        .run();
      assertThat(createResult).isNotNull()
        .returns(false, Result::hasErrors)
        .returns(true, Result::getResult);
      // create object
      String id = "00000000-0000-0000-0000-000000000001";
      Map<String, Object> props = new HashMap<>();
      props.put("name", "some name");
      Float[] vector = new Float[]{ 0.11f, 0.22f, 0.33f, 0.123f, -0.900009f, -0.0000000001f };
      WeaviateObject obj = WeaviateObject.builder()
        .id(id).className(className).properties(props).vector(vector)
        .build();
      Result<ObjectGetResponse[]> result = client.batch().objectsBatcher()
        .withObjects(obj)
        .run();
      assertThat(result).isNotNull()
        .returns(false, Result::hasErrors)
        .extracting(Result::getResult).asInstanceOf(ARRAY)
        .hasSize(1);
      // fetch that object
      Result<List<WeaviateObject>> resultObj = client.data().objectsGetter()
        .withID(obj.getId()).withClassName(obj.getClassName()).withVector()
        .run();
      assertThat(resultObj).isNotNull()
        .returns(false, Result::hasErrors)
        .extracting(Result::getResult).isNotNull()
        .extracting(r -> r.get(0)).isNotNull()
        .satisfies(o -> {
          assertThat(o.getId()).isEqualTo(obj.getId());
          assertThat(o.getVector()).isNotEmpty().isEqualTo(vector);
          assertThat(o.getProperties()).isNotNull()
            .extracting(Map::size).isEqualTo(obj.getProperties().size());
          obj.getProperties().keySet().forEach(propName -> {
            assertThat(o.getProperties().get(propName)).isNotNull();
          });
        });
      // clean up
      Result<Boolean> delete = client.schema().classDeleter().withClassName(className).run();
      assertThat(delete).isNotNull()
        .returns(false, Result::hasErrors)
        .extracting(Result::getResult).isEqualTo(Boolean.TRUE);
    } finally {
      container.stop();
    }
  }
}
