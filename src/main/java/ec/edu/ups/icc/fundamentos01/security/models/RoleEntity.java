package ec.edu.ups.icc.fundamentos01.security.models;

import java.util.HashSet;
import java.util.Set;

import ec.edu.ups.icc.fundamentos01.core.entities.BaseModel;
import ec.edu.ups.icc.fundamentos01.users.models.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")  
public class RoleEntity extends BaseModel {
    
    @Column(nullable = false, unique = true, length = 50)
    @Enumerated(EnumType.STRING)  // Guardar nombre del enum, no el número
    private RoleName name;

    /**
     * Descripción del rol (opcional)
     * 
     * Ejemplo: "Usuario estándar con permisos básicos"
     */
    @Column(length = 200)
    private String description;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<UserEntity> users = new HashSet<>();

    // ============== CONSTRUCTORES ==============

    /**
     * Constructor vacío (REQUERIDO por JPA)
     * JPA usa reflexión para crear instancias
     */
    public RoleEntity() {
    }

    /**
     * Constructor con nombre de rol
     * Útil para crear roles en DataInitializer
     */
    public RoleEntity(RoleName name) {
        this.name = name;
    }

    /**
     * Constructor completo
     * Útil para crear roles con descripción
     */
    public RoleEntity(RoleName name, String description) {
        this.name = name;
        this.description = description;
    }

    // ============== GETTERS Y SETTERS ==============
    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }
    
}
