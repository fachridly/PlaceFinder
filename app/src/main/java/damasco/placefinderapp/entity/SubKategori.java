package damasco.placefinderapp.entity;

import com.habibmustofa.simple.annotation.Column;
import com.habibmustofa.simple.annotation.Id;
import com.habibmustofa.simple.annotation.Table;

/**
 * Created by Habib Mustofa on 09/11/2017.
 */
@Table
public class SubKategori {

    @Id
    @Column(primaryKey = true)
    private String id;

    @Column
    private String name;

    @Column
    private String kategori;

    public SubKategori() {
    }

    public SubKategori(String id, String name, String kategori) {
        this.id = id;
        this.name = name;
        this.kategori = kategori;
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

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }
}
