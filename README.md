# EntertainMain

This is my first implementation of a project following the MVVM architecture. I learnt various concepts while implementing this project. 
Ofcourse there are many things that I realized I did unoptimally so still a long way to go. 


The api used to fetch movie data: [MoviesDatabase](https://rapidapi.com/SAdrian/api/moviesdatabase/)


Different new Views/Layouts that I used here:
- DrawerLayout
- NavigationView
- RecyclerView using GridLayoutManager
- TabLayout with Tabs
- ToolbarLayout and AppBar
- Toolbar's SearchView as an expandable action

---

#### The concepts I learnt while doing this project:
- Architecture Components
- MVVM architecture 
- Creating viewmodels
- Proper way to pass context to viewmodels
- The Retrofit Library to make api calls
- Room Database 
- Using DiffUtil to manage adapter list 
- Detecting the end of RecyclerView when scrolling
- Handling pagination with Api calls and ViewModel
- Navigating between fragments via BottomNavigationView as well as DrawerLayout
- Searching items using the SearchView 
- Implementing debouncing (delay) when typing
- Storing images received from API

### App Features
1. Browse Top Rated Movies 
> ![image](https://user-images.githubusercontent.com/107396507/190429785-498ba7ce-9dfa-4857-90d5-c3206052c9d2.png)

2. See Details of the Movie
> ![image](https://user-images.githubusercontent.com/107396507/190430919-34c1eabd-ff79-4d8c-a2ab-dfcb48cb35ce.png)

3. Search For Any Movies 
> ![image](https://user-images.githubusercontent.com/107396507/190430313-b5cd0dda-a243-45a6-ade8-2c9fcde13d58.png)

4. Add movie to favorites to see their details even when offline
> ![image](https://user-images.githubusercontent.com/107396507/190431145-68f202d2-ba27-4f10-97c1-22e40585a1b3.png)

5. Add movies to watchlist
> ![image](https://user-images.githubusercontent.com/107396507/190431546-a5247a3b-826c-4592-a7d9-4cf284de37b8.png)

 
### Known Issues
1. Movies cannot be added to favorites/saved while the image is being loaded
1. Sometimes the image will not be saved while adding the movie to favorites
1. Keyboard does not collapse when scrolling RecyclerView of SearchActivity
1. Marking an item as watched or deleting items from watchlist temporarliy removes the last item of recycler view
1. Cannot store cast info when adding movies to favorite
1. Change theme not implemented
1. The Anime section of app has not been implemented (hopefully for a later day)


#### This is all purely for learning purposes !
