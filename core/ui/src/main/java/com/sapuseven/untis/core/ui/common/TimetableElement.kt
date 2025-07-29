import androidx.compose.runtime.Composable
import com.sapuseven.untis.core.model.Element

@Composable
fun ElementItem(
	element: Element,
	content: @Composable (shortName: String, longName: String, isAllowed: Boolean) -> Unit
) {
	content(element.shortName, element.longName, element.timetableAllowed)
}
