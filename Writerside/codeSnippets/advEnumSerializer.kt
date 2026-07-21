// Pattern maison : companion object Serializer, enum codé par entier.
@Serializable(MyEnum.Serializer::class)
enum class MyEnum(val id: Int) {
    FOO(1), BAR(2), BAZ(5);

    companion object Serializer : KSerializer<MyEnum> {
        override val descriptor =
            PrimitiveSerialDescriptor("MyEnum", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: MyEnum) =
            encoder.encodeInt(value.id)

        // Robuste même si les ids ne sont pas contigus.
        override fun deserialize(decoder: Decoder): MyEnum {
            val id = decoder.decodeInt()
            return entries.first { it.id == id }
        }
    }
}
