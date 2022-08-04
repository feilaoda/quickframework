package com.quickpaas.framework.utils;

import com.quickpaas.framework.exception.QuickException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@UtilityClass
public class ObjectUtils {

    private final static MapperFactory mapperFactory = new DefaultMapperFactory.Builder().mapNulls(false).build();
    static {
        mapperFactory.getConverterFactory().registerConverter(new UtilDateConverter());
        mapperFactory.getConverterFactory().registerConverter(new BigDecimalConverter());
    }

    public static  <T,R> T map(T target, R source) {
        if(mapperFactory.existsRegisteredMapper(TypeFactory.<R> valueOf(source.getClass()), TypeFactory.valueOf(target.getClass()), false)){
            mapperFactory.getMapperFacade().map(source, target);
            return target;
        }
        ClassMapBuilder<?, ?> builder = mapperFactory.classMap(source.getClass(), target.getClass());
        builder.byDefault().register();
        mapperFactory.getMapperFacade().map(source, target);
        return target;
    }

    public static  <T,R> T copy(T target, R source) {
        if(mapperFactory.existsRegisteredMapper(TypeFactory.<R> valueOf(source.getClass()), TypeFactory.valueOf(target.getClass()), false)){
            mapperFactory.getMapperFacade().map(source, target);
            return target;
        }
        ClassMapBuilder<?, ?> builder = mapperFactory.classMap(source.getClass(), target.getClass());
        builder.byDefault().register();
        mapperFactory.getMapperFacade().map(source, target);
        return target;
    }

    public static  <T,R> T map(T target, R source, List<String> nullFields) {
        if(mapperFactory.existsRegisteredMapper(TypeFactory.<R> valueOf(source.getClass()), TypeFactory.valueOf(target.getClass()), false)){
            mapperFactory.getMapperFacade().map(source, target);
            return target;
        }
        ClassMapBuilder<?, ?> builder = mapperFactory.classMap(source.getClass(), target.getClass());
        if(CollectionUtils.isNotEmpty(nullFields)) {
            nullFields.forEach(e-> {
                builder.field(e,e).mapNulls(true);
            });
        }
        builder.byDefault().register();
        mapperFactory.getMapperFacade().map(source, target);
        return target;
    }

    public static  <T, R> List<T> map(List<R> source, Class<R> sourceClass, Class<T> targetClass) {
//        Class clz = (Class<?>) ((ParameterizedType) source.getClass()
//                .getGenericSuperclass()).getActualTypeArguments()[0];
        if(mapperFactory.existsRegisteredMapper(TypeFactory.<R> valueOf(sourceClass), TypeFactory.valueOf(targetClass), false)){
            List<T> list = new ArrayList<>();
            for(R r: source) {
                T d = mapperFactory.getMapperFacade().map(r, targetClass);
                list.add(d);
            }
            return list;
        }
        ClassMapBuilder<?, ?> builder = mapperFactory.classMap(sourceClass, targetClass);
        builder.byDefault().register();
        List<T> list = new ArrayList<>();
        for(R r: source) {
            T d = mapperFactory.getMapperFacade().map(r, targetClass);
            list.add(d);
        }
        return list;

    }

    public static  <T,R> T map(R source, Class<T> targetClass) {
        if(mapperFactory.existsRegisteredMapper(TypeFactory.<R> valueOf(source.getClass()), TypeFactory.valueOf(targetClass), false)){
            return mapperFactory.getMapperFacade().map(source, targetClass);
        }
        ClassMapBuilder<?, T> builder = mapperFactory.classMap(source.getClass(), targetClass);
        builder.byDefault().register();
        return mapperFactory.getMapperFacade().map(source, targetClass);
    }

    private static class UtilDateConverter extends BidirectionalConverter<Date, String> {
        public static final FastDateFormat DEFAULT_FAST_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

        @Override
        public String convertTo(Date source, Type<String> destinationType, MappingContext context) {
            return DEFAULT_FAST_DATE_FORMAT.format(source);
        }

        @Override
        public Date convertFrom(String source, Type<Date> destinationType, MappingContext context) {
            try {
                return DateUtils.parseDate(source, "yyyy-MM-dd HH:mm:ss");
            } catch (final ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class BigDecimalConverter extends BidirectionalConverter<BigDecimal, String> {

        @Override
        public String convertTo(BigDecimal source, Type<String> destinationType, MappingContext context) {
            return source.toString();
        }

        @Override
        public BigDecimal convertFrom(String source, Type<BigDecimal> destinationType, MappingContext context) {
            return new BigDecimal(source);
        }
    }

    public static Object getProperty(Object bean, String name) {
        try {
            return PropertyUtils.getProperty(bean, name);
        } catch (Exception e) {
            throw new QuickException(e);
        }
    }

    public static void setProperty(Object bean, String name, Object value) {
        try {
            PropertyUtils.setProperty(bean, name, value);
        } catch (Exception e) {
            throw new QuickException(e);
        }
    }



}
