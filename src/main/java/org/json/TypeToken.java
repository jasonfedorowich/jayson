import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeToken<T>{

    private Type type;
    private Type rawType;

    protected TypeToken(){
        Type superClass = getClass().getGenericSuperclass();
        this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public TypeToken(Type type){
        this.type = type;
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type rawType = parameterizedType.getRawType();
        this.rawType = (Class<?>) rawType;
    }

    public static <T> TypeToken<T> get(Class<T> type) {
        return new TypeToken<>(type);
    }

    public Type getType(){
        return type;
    }
}
