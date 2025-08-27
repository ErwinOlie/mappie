package tech.mappie.api

/**
 * Base mapper class for array mappers. Cannot be instantiated, but can be created by using the field [ObjectMappie.forArray].
 */
public sealed class ArrayMappie<out TO> : Mappie<Array<TO>>
