# jackson-merge-patch
A simple plugin for JacksonXML, which attempts to solve the problem of distinguishing (after serializing JSON to a java class) whether nodes in the JSON payload were:
- present with a non-null value
    ```
    { "hello": "world", "foo": "bar" }
    ```
- present with a null value
    ```
    { "hello": "world", "foo": null }
    ```
- or completely absent
    ```
    { "hello": "world" }
    ```

The classes in this project can be used to enable unique support for the RFC-7396 merge-patch standard.

 
## lombok
With older versions of lombok, the `@AllArgsConstructor` creates a ctor with the `java.beans.ConstructorProperties` annotation on it. 
When a ctor with this anno is present, jackson will use it to construct the instance of the object instead of using the default ctor and 
then individual getters and setters for fields. When this happens, Jackson is forced to come up with values for all fields declared in that 
ctor.
 
## Getting Started
Register the `PatchMergeModule` with your `ObjectMapper`
 
```
ObjectMapper om = new ObjectMapper();
om.registerModule(new PatchMergeModule());
```
Or you may use the customer serializer manually on a `PatchField` attribute

```
@JsonDeserialize(using = PatchFieldDeserializer.class)
private PatchField<String> foo = PatchField.<String> builder().shouldPatch(false).build();
```

Notice in the above example that the `PatchField` was initialized with `shouldPatch=false`. This is very important.

**The general idea is that this default value declared in code is the "absent" JSON node value. With everything setup properly,
jackson will only call setters for fields that were actually present in the JSON payload.**
