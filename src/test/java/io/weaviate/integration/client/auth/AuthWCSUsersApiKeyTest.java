package io.weaviate.integration.client.auth;

import io.weaviate.client.Config;
import io.weaviate.client.WeaviateAuthClient;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.base.WeaviateError;
import io.weaviate.client.v1.auth.exception.AuthException;
import io.weaviate.client.v1.batch.model.ObjectGetResponse;
import io.weaviate.client.v1.data.model.WeaviateObject;
import io.weaviate.client.v1.misc.model.Meta;
import io.weaviate.client.v1.schema.model.DataType;
import io.weaviate.client.v1.schema.model.Property;
import io.weaviate.client.v1.schema.model.WeaviateClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.InstanceOfAssertFactories.ARRAY;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;

import static io.weaviate.integration.client.WeaviateVersion.EXPECTED_WEAVIATE_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.annotation.JsonAppend;

public class AuthWCSUsersApiKeyTest {

  private static String host;
  private static Integer port;
  private static String grpcHost;
  private static Integer grpcPort;
  private static final String API_KEY = "my-secret-key";
  private static final String INVALID_API_KEY = "my-not-so-secret-key";

  @ClassRule
  public static DockerComposeContainer compose = new DockerComposeContainer(
    new File("src/test/resources/docker-compose-wcs.yaml")
  ).withExposedService("weaviate-auth-wcs_1", 8085, Wait.forListeningPorts(8085))
    .withExposedService("weaviate-auth-wcs_1", 50051, Wait.forListeningPorts(50051));

  @Before
  public void before() {
    host = compose.getServiceHost("weaviate-auth-wcs_1", 8085);
    port = compose.getServicePort("weaviate-auth-wcs_1", 8085);
    grpcHost = compose.getServiceHost("weaviate-auth-wcs_1", 50051);
    grpcPort = compose.getServicePort("weaviate-auth-wcs_1", 50051);
  }

  @Test
  public void shouldAuthenticateWithValidApiKey() throws AuthException {
    Config config = new Config("http", host + ":" + port);
    WeaviateClient client = WeaviateAuthClient.apiKey(config, API_KEY);
    Result<Meta> meta = client.misc().metaGetter().run();

    assertThat(meta).isNotNull()
      .returns(false, Result::hasErrors)
      .extracting(Result::getResult).isNotNull()
      .returns("http://[::]:8085", Meta::getHostname)
      .returns(EXPECTED_WEAVIATE_VERSION, Meta::getVersion);
  }

  @Test
  public void shouldNotAuthenticateWithInvalidApiKey() throws AuthException {
    Config config = new Config("http", host + ":" + port);
    WeaviateClient client = WeaviateAuthClient.apiKey(config, INVALID_API_KEY);
    Result<Meta> meta = client.misc().metaGetter().run();

    assertThat(meta).isNotNull()
      .returns(true, Result::hasErrors)
      .returns(null, Result::getResult)
      .extracting(Result::getError)
      .returns(401, WeaviateError::getStatusCode);
  }

  @Test
  public void shouldAuthenticateWithValidApiKeyUsingGRPC() throws AuthException {
    Config config = new Config("http", host + ":" + port);
    config.setGRPCHost(grpcHost + ":" + grpcPort);
    WeaviateClient client = WeaviateAuthClient.apiKey(config, API_KEY);

    Result<Boolean> deleteAll = client.schema().allDeleter().run();
    assertThat(deleteAll).isNotNull()
      .returns(false, Result::hasErrors)
      .extracting(Result::getResult).isEqualTo(Boolean.TRUE);

    String id = "00000000-0000-0000-0000-000000000001";
    String className = "TestGRPC";
    String propertyName = "name";
    List<Property> properties = new ArrayList<>();
    properties.add(Property.builder().name("name").dataType(Collections.singletonList(DataType.TEXT)).build());
    WeaviateClass clazz = WeaviateClass.builder().className(className).properties(properties).build();
    Result<Boolean> createClass = client.schema().classCreator().withClass(clazz).run();

    assertThat(createClass).isNotNull()
      .returns(false, Result::hasErrors)
      .returns(true, Result::getResult);

    Map<String, Object> props = new HashMap<>();
    props.put("name", "John Doe");

    WeaviateObject obj = WeaviateObject.builder().id(id).className(className).properties(props).build();

    Result<ObjectGetResponse[]> result = client.batch().objectsBatcher()
      .withObjects(obj)
      .run();
    assertThat(result).isNotNull()
      .returns(false, Result::hasErrors)
      .extracting(Result::getResult).asInstanceOf(ARRAY)
      .hasSize(1);

    Result<List<WeaviateObject>> resultObj = client.data().objectsGetter().withClassName(className).withID(id).run();
    assertThat(resultObj).isNotNull()
      .returns(false, Result::hasErrors)
      .extracting(Result::getResult).isNotNull()
      .extracting(r -> r.get(0)).isNotNull()
      .satisfies(o -> {
        assertThat(o.getId()).isEqualTo(obj.getId());
        assertThat(o.getProperties()).isNotNull()
          .extracting(Map::size).isEqualTo(obj.getProperties().size());
        assertThat(o.getProperties()).isNotEmpty().satisfies(p -> {
          assertThat(p.get(propertyName)).isNotNull();
        });
      });
  }
}
