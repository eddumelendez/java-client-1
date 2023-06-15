package io.weaviate.client.v1.batch.model;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import io.weaviate.client.v1.filters.WhereFilter;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BatchDeleteResponse {

    Match match;
    String output;
    Boolean dryRun;
    Results results;


    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Match {

        @SerializedName("class")
        String className;
        @SerializedName("where")
        WhereFilter whereFilter;
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Results {

        Long matches;
        Long limit;
        Long successful;
        Long failed;
        ResultObject[] objects;
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ResultObject {

        String id;
        String status;
        Errors errors;
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Errors {

        Error[] error;
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Error {

        String message;
    }
}
