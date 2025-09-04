# Rick-Morty

🔹 Kullandığım Teknolojiler & Mimariler

-Jetpack Compose (UI)
Ekranların (ListScreen, DetailScreen) tamamen Compose ile yazılmıştır.
LazyColumn, OutlinedTextField, AsyncImage (Coil) gibi modern Compose bileşenleri var.
StateFlow + collectAsState ile reactive state yönetimi yapıyorum.

-Navigation Compose
NavHost ile list → detail/{id} geçişi yapılıyor.
Argümanlı navigation (navArgument("id")) kullanıyorum.

-Dependency Injection → Hilt
@HiltViewModel, @Module, @Provides, @InstallIn(SingletonComponent::class) kullanıldı.
NetworkModule, DataStoreModule, RepositoryModule ile katmanları ayrıştırıldı.

-Networking → Retrofit + OkHttp + Moshi
Retrofit ile Rick & Morty API’den veri çekiyoruz.
Moshi + @JsonClass(generateAdapter = true) ile JSON parse ediliyor.
OkHttpClient ve HttpLoggingInterceptor debug için eklendi.

-Architecture Layering (Clean Architecture tarzı)
Data Katmanı: DTO’lar (CharacterDto, CharacterResponse), RmApi, CharacterRepositoryImpl.
Domain Katmanı: Characters entity’si + UseCase’ler (GetCharactersUseCase, GetCharacterDetailUseCase, ToggleFavoriteUseCase).
UI Katmanı: ViewModel + Compose ekranları.
UiState sealed class ile Loading/Success/Error yönetiyoruz.

-DataStore (Preferences)
Favori karakterleri saklamak için DataStore<Preferences> kullanılmış.
FavoritesStore içinde toggleFavorite() fonksiyonu var.

-Coroutines & Flow
Tüm veri çağrıları suspend fonksiyonlar ile yapılıyor.
StateFlow, MutableStateFlow, stateIn ile reactive state management.
debounce mantığı için delay(350) ile search input throttling var.

-Coil (Compose için image loader)
Karakter görsellerini AsyncImage ile çekiyoruz.


📱 App Özeti
  Modern Compose UI ile yazılmış.
  Clean Architecture + Hilt kullanılmış.
  Retrofit + Moshi + OkHttp ile network katmanı.
  DataStore (Preferences) ile basit local persistence (favoriler).
  StateFlow + Coroutines ile reactive state management.
  Navigation Compose ile ekran geçişleri.
  Coil ile image loading.
