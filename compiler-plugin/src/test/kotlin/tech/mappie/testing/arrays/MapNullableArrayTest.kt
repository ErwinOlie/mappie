package tech.mappie.testing.arrays

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

class MapNullableArrayTest {

    data class Input(val value: LocalDateTime)

    data class Output(val value: LocalDate)

    @TempDir
    lateinit var directory: File

    @Test
    fun `mapNullableArray implicit succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.arrays.MapNullableArrayTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.mapNullableArray(arrayOf(Input(LocalDateTime.MIN), Input(LocalDateTime.MAX)))!!)
                .containsExactly(Output(LocalDate.MIN), Output(LocalDate.MAX))

            assertThat(mapper.mapNullableArray(null))
                .isEqualTo(null)
        }
    }
}
