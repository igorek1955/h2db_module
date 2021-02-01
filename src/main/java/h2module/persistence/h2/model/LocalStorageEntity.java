package h2module.persistence.h2.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



/**
 * class for storage of any object type and internal id as main db index
 * class used by h2 in-memory database
 */
@Slf4j
@Data
@Entity
@Table(name = "storage_entity")
public class LocalStorageEntity {

    @Id
    @Column(name="id")
    private String id; //Our own id

    @Column(name = "object")
    private byte[] object;
}
