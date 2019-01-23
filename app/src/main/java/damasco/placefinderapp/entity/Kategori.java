package damasco.placefinderapp.entity;

import com.habibmustofa.simple.annotation.Column;
import com.habibmustofa.simple.annotation.Id;
import com.habibmustofa.simple.annotation.Table;

/**
 * Created by Habib Mustofa on 09/11/2017.
 */
@Table
public class Kategori {

    @Id
    @Column(primaryKey = true)
    private String id;

    @Column(unique = true)
    private String name;

    public Kategori() {
    }

    public Kategori(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
