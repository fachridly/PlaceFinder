package damasco.placefinderapp.entity;

import com.habibmustofa.simple.annotation.Column;
import com.habibmustofa.simple.annotation.Id;
import com.habibmustofa.simple.annotation.Table;
import com.habibmustofa.simple.annotation.Type;

/**
 * Created by Habib Mustofa on 09/11/2017.
 */

@Table
public class Fasilitas {

    @Id
    @Column(primaryKey = true)
    private String id;

    @Column
    private String kategori;

    @Column
    private String subKategori;

    @Column
    private String nama;

    @Column(type = Type.REAL)
    private Double latitude;

    @Column(type = Type.REAL)
    private Double longitude;

    public Fasilitas() {
    }

    public Fasilitas(String id, String kategori, String subKategori, String nama, Double latitude, Double longitude) {
        this.id = id;
        this.kategori = kategori;
        this.subKategori = subKategori;
        this.nama = nama;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getSubKategori() {
        return subKategori;
    }

    public void setSubKategori(String subKategori) {
        this.subKategori = subKategori;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Fasilitas{" +
                "nama='" + nama + '\'' +
                '}';
    }
}
