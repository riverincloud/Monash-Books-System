/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.entity;

import javax.persistence.Entity;

/**
 *
 * @author Di
 */
@Entity
public class Administrator extends Users {

    private static final long serialVersionUID = 1L;

    /**
     * Constructors
     */
    public Administrator() {
    }

    public Administrator(String email, String name, String mobile) {
        super(email, name, mobile);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Administrator)) {
            return false;
        }
        Administrator other = (Administrator) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mbsystem.models.Administrator[ id=" + id + " ]";
    }

}
