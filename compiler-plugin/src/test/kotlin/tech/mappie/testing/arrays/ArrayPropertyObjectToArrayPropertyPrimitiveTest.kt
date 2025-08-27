package tech.mappie.testing.arrays

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ArrayPropertyObjectToArrayPropertyPrimitiveTest {
    data class Input(val text: Array<InnerInput>)
    data class InnerInput(val value: String)

    data class Output(val text: Array<String>)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map array explicit with implicit via should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.arrays.ArrayPropertyObjectToArrayPropertyPrimitiveTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::text fromProperty from::text
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, String>() {
                    override fun map(from: InnerInput) = from.value
                }
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

            assertThat(mapper.map(Input(arrayOf(InnerInput("A"), InnerInput("B")))))
                .isEqualTo(Output(arrayOf("A", "B")))
        }
    }

    @Test
    fun `map via forArray should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.arrays.ArrayPropertyObjectToArrayPropertyPrimitiveTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::text fromProperty from::text via InnerMapper.forArray
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, String>() {
                    override fun map(from: InnerInput) = from.value
                }
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

            assertThat(mapper.map(Input(arrayOf(InnerInput("A"), InnerInput("B")))))
                .isEqualTo(Output(arrayOf("A", "B")))
        }
    }
}

