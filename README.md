

Sample code

<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- App Bar Layout -->
    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:theme="@style/AppTheme.AppBarOverlay">

        <!-- Collapsing Toolbar Layout -->
        <com.google.android.material.appbar.CollapsingToolbarLayout
            android:id="@+id/collapsingToolbar"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:layout_scrollFlags="scroll|exitUntilCollapsed">

            <!-- Your ViewPager -->
            <androidx.viewpager.widget.ViewPager
                android:id="@+id/viewPager"
                android:layout_width="match_parent"
                android:layout_height="200" <!-- Set your desired height -->
                app:layout_collapseMode="parallax" <!-- Optional: Add parallax effect -->
                />

            <!-- Optional: Add an ImageView or other views for additional collapsing effects -->
            <ImageView
                android:layout_width="match_parent"
                android:layout_height="200" <!-- Set your desired height -->
                android:scaleType="centerCrop"
                android:src="@drawable/your_image" />

        </com.google.android.material.appbar.CollapsingToolbarLayout>

        <!-- Toolbar (optional, based on your design) -->
        <androidx.appcompat.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            app:popupTheme="@style/AppTheme.PopupOverlay" />

    </com.google.android.material.appbar.AppBarLayout>

    <!-- Your RecyclerView (scrolls below the AppBarLayout) -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="?attr/actionBarSize" <!-- Adjust margin based on your toolbar height -->
        app:layout_behavior="@string/appbar_scrolling_view_behavior" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
