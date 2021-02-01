package h2module.persistence.h2.converter;

import h2module.persistence.h2.model.LocalStorageEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.MessagePack;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * utility class for class conversion for use with H2 in-memory database and other databases.
 * h2 accepts any type of object if we mark it by @Message annotation and register it with MessagePack
 *
 */
@Slf4j
@Service
public class ObjectConverter {

    private final MessagePack msgPack = new MessagePack();
    private boolean registered = false;
    private final Class<?> clazz;

    @SneakyThrows
    public ObjectConverter(@Value("${serializationClassName}")String className){
        clazz = Class.forName(className);
    }

    /**
     * getting LocalStorageEntity instance from any object , and setting its id as primary entity id
     *
     * @param object any type of object, can be set in application.properties
     * @param id objects internal id
     * @return local storage entity instance
     */
    public LocalStorageEntity convertToLocalEntity(Object object, String id){
        LocalStorageEntity entity = new LocalStorageEntity();

        if(!registered){
            msgPack.register(clazz);
            registered = true;
        }

        try{
            entity.setObject(msgPack.write(object));
            entity.setId(id);
        } catch (Exception e){
            log.error("error occurred when converting to local entity " + e.getMessage());
        }

        return entity;
    }



    public Object convertFromLocalEntity(LocalStorageEntity entity){

        Object object = new Object();

        if(!registered){
            log.error(clazz.toString());
            msgPack.register(clazz);
            registered = true;
        }

        try{
            object = msgPack.read(entity.getObject(), clazz);
        } catch (Exception e){
            log.error("error occurred when converting from local entity " + e.getMessage());
        }

        return object;
    }
}
