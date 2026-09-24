package com.yummizi.app.features.home.domain.usecase

import android.util.Log
import com.yummizi.app.core.data.local.entities.Restaurant
import com.yummizi.app.features.restaurant.domain.repository.RestaurantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Domain/UseCase/GetCategorizedFeedUseCase.kt
class GetCategorizedFeedUseCase @Inject constructor(
    private val repository: RestaurantRepository
) {
    operator fun invoke(): Flow<Map<String, List<Restaurant>>> {
        return repository.observeRestaurantsInUserScope().map { list ->
            Log.d("MyActivity", "🗺️  List of restaurant received from the repo before groupBy\n$list\n")

            list.filterNotNull().groupBy { it.sectionType}  // Removing nulls and grouping by section
        }
    }
}

//TODO: Repair the following situations:
//
// Duplicates: If Pizza Hut is both "Nearby" AND "Friends Like," do you send the whole object twice? That doubles your data usage.
//
// Sorting: How does the app know which section comes first? You’d need another attribute like sectionPriority.
//
// Rigidity: If you want to change "Nearby" to "Fastest Delivery," you have to edit every single restaurant object in the list.