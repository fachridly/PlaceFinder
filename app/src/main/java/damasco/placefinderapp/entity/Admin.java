package damasco.placefinderapp.entity;

import com.habibmustofa.simple.annotation.Column;
import com.habibmustofa.simple.annotation.Id;
import com.habibmustofa.simple.annotation.Table;

/**
 * Created by Habib Mustofa on 09/11/2017.
 */
@Table
public class Admin {

    @Id
    @Column(primaryKey = true)
    private String id;

    @Column
    private String password;

    public Admin() {
    }

    public Admin(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
