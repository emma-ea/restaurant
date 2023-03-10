import com.emma_ea.restaurants.restaurants.data.remote.RemoteRestaurant
import com.emma_ea.restaurants.restaurants.domain.Restaurant

object DummyContent {
    fun getDomainResults() = arrayListOf(
        Restaurant(0, "title0", "description0", false),
        Restaurant(1, "title1", "description1", false),
        Restaurant(2, "title2", "description2", false),
        Restaurant(3, "title3", "description3", false)
    )

    fun getRemoteRestaurants() = getDomainResults()
        .map {
            RemoteRestaurant(
                it.id,
                it.title,
                it.description
            )
        }
}