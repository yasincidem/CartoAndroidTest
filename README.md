
###  APK: https://drive.google.com/file/d/1YduZnz760k9nzGC0zLHT1Rw9nGeYoTKL/view?usp=sharing




### What's done

✅ | The app also works offline.

✅ | You include unit and functional tests.

✅ | Animations. For example, BottomSheetDialogFragment.

✅ | The app works in landscape mode.

✅ | Good practices, architecture

✅ | Poi filter by title and description

✅ | Immersive mode


***

<p>
  <img src="https://user-images.githubusercontent.com/13544246/103548824-6d9e3a80-4eb7-11eb-965f-056e9252953e.jpeg" width="20%"> 
  <img src="https://user-images.githubusercontent.com/13544246/103548830-6ecf6780-4eb7-11eb-85dc-f8df9f906385.jpeg" width="20%"> 
  <img src="https://user-images.githubusercontent.com/13544246/103548834-70009480-4eb7-11eb-8cc2-e0592c8a60c1.jpeg" width="20%"> 
  <img src="https://user-images.githubusercontent.com/13544246/103548839-7131c180-4eb7-11eb-9fcd-b8e34dd13dfb.jpeg" width="20%"> 
</p>
 <img src="https://user-images.githubusercontent.com/13544246/103548845-71ca5800-4eb7-11eb-935b-8deb1f8b27bb.jpeg" width="50%"> 

***

### Architecture
I wanted to use MVVM architecture in this project and then implemented the schema that is recommended by Google. Background operations handled with coroutines.

<p>
  <img src="https://user-images.githubusercontent.com/13544246/103544362-09787800-4eb1-11eb-8854-84313a07a272.png" width="50%"> 
</p>

***

### Dependency Injection
I used Hilt to implement DI.


[API Module](https://github.com/yasincidem/CartoAndroidTest/blob/master/app/src/main/java/com/yasincidemcarto/androidtest/di/ApiModule.kt)

[Local Module](https://github.com/yasincidem/CartoAndroidTest/blob/master/app/src/main/java/com/yasincidemcarto/androidtest/di/LocalModule.kt)

***

### Permissions
I remove the old way of asking for permissions from MainActivity and replaced it with new Activity Results API

[Link to the appropriate row](https://github.com/yasincidem/CartoAndroidTest/blob/f6040b794dbfb3f84a486828f74922c7208b7e88/app/src/main/java/com/yasincidemcarto/androidtest/ui/activity/MainActivity.kt#L68)

***

### Unit Tests
I used Mockito to mock data, core-testing to test LiveData, kotlinx-coroutines-test to test suspend functions and Truth for assertions.

[ViewModel Test](https://github.com/yasincidem/CartoAndroidTest/blob/master/app/src/test/java/com/yasincidemcarto/androidtest/ui/MainViewModelTest.kt)

[Repository Test](https://github.com/yasincidem/CartoAndroidTest/blob/master/app/src/test/java/com/yasincidemcarto/androidtest/repository/PoiRepositoryTest.kt)

***

### UI Tests
I used Espresso, espresso-idling-resource and tested only the flow when GPS is enabled.

[UI Test](https://github.com/yasincidem/CartoAndroidTest/blob/master/app/src/androidTest/java/com/yasincidemcarto/androidtest/SimpleGpsOnTestCase.kt)

***

### Memory leaks

I added the 3rd party library "Leak Canary" to detect possible memory leaks. I encountered these two memory leaks and resolve it by setting the variable that could cause the leak to null onDismiss and onDestroy callbacks. I used "FragmentContainerView" instead of plain "fragment" but "SupportMapFragment" lead to memory leak so then I changed to plain "fragment"

<p>
  <img src="https://user-images.githubusercontent.com/13544246/103541774-f2d02200-4eac-11eb-8168-8962205e97e4.jpeg" width="20%"> 
  <img src="https://user-images.githubusercontent.com/13544246/103541785-f5cb1280-4eac-11eb-8ac3-c75c00ea691c.jpeg" width="20%"> 
</p>


***

### Chucker
I used Chucker to inspect HTTP requests. 
<p>
  <img src="https://user-images.githubusercontent.com/13544246/103543280-4d6a7d80-4eaf-11eb-810f-6ea71d455691.jpeg" width="20%"> 
</p>

***

### MAD Score

<p>
  <img src="https://user-images.githubusercontent.com/13544246/103542806-93731180-4eae-11eb-9471-b6dc69f5b870.png" width="50%"> 
</p>

