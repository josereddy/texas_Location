package com.example.Location.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "database_sequences")
public class DatabaseSequence_MDB {
    @Id
    private String id;

    private long seq;

}
