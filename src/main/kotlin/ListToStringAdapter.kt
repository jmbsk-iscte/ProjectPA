import javax.xml.bind.annotation.adapters.XmlAdapter

class ListToStringAdapter : XmlAdapter<String, List<Double>>() {
    override fun unmarshal(v: String?): List<Double> {
        return v?.split(",")?.map { it.toDouble() } ?: emptyList()
    }

    override fun marshal(v: List<Double>?): String {
        return v?.joinToString(",") ?: ""
    }
}