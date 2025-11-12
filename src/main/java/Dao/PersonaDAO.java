package Dao;

import Models.Persona;



/**
 * Data Access Object para la entidad Persona.
 * Gestiona todas las operaciones de persistencia de personas en la base de datos.
 *
 * Características:
 * - Implementa GenericDAO<Persona> para operaciones CRUD estándar
 * - Usa PreparedStatements en TODAS las consultas (protección contra SQL injection)
 * - Proporciona búsquedas especializadas
 *
 * Patrón: DAO con try-with-resources para manejo automático de recursos JDBC
 */
public class PersonaDAO implements GenericDAO<Persona> {
    
    
}