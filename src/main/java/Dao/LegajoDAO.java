package Dao;


/**
 * Data Access Object para la entidad Domicilio.
 * Gestiona todas las operaciones de persistencia de domicilios en la base de datos.
 *
 * Características:
 * - Implementa GenericDAO<Legajo> para operaciones CRUD estándar
 * - Usa PreparedStatements en TODAS las consultas (protección contra SQL injection)
 *
 * Diferencias con PersonaDAO:
 * 
 * 
 * 
 *
 * Patrón: DAO con try-with-resources para manejo automático de recursos JDBC
 */
public class LegajoDAO implements GenericDAO<Legajo> {
   
}