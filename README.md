![Build project](https://github.com/jasonfedorowich/jayson/actions/workflows/maven.yml/badge.svg)

Lightweight JSON Parser 

Example:

```
  public static class NestedPojo{
        Long v;
        Boolean t;

    }

    public static class NestedPojo2{
        Double v;
    }

    public static class Pojo{
        String name;
        String stuff;
        NestedPojo nested;

        List<NestedPojo2> list;

        String[] arr;

        Integer i;
    }

     String json = "{\n" +
                "    \"name\": \"jason\",\n" +
                "    \"stuff\": \"test\",\n" +
                "    \"nested\": {\n" +
                "        \"v\": 1,\n" +
                "        \"t\": true\n" +
                "    },\n" +
                "    \"list\": [{\"v\": 1.0}, {\"v\": 2.0}],\n" +
                "    \"arr\": [\"hello\", \"world\"],\n" +
                "    \"i\": 100\n" +
                "}";

      //From String To Object
      Pojo pojo = Jayson.fromString(json, Pojo.class);

      //From Object to String
      String newJson = Jayson.toString(pojo);
  
```
