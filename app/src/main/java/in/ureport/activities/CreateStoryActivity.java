package in.ureport.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.fragments.CreateStoryFragment;
import in.ureport.fragments.MarkersFragment;
import in.ureport.listener.OnCloseDialogListener;
import in.ureport.listener.SelectionResultListener;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.GameficationManager;
import in.ureport.managers.UserViewManager;
import in.ureport.models.Marker;
import in.ureport.models.Story;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class CreateStoryActivity extends AppCompatActivity implements CreateStoryFragment.StoryCreationListener, SelectionResultListener<Marker> {

    public static final String EXTRA_USER = "user";

    private CreateStoryFragment createStoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_create_story);
        setupView();

        if(savedInstanceState == null) {
            addCreateStoryFragment();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    private void setupView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addCreateStoryFragment() {
        createStoryFragment = new CreateStoryFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, createStoryFragment)
                .commit();
    }

    @Override
    public void onAddMarkers(List<Marker> selectedMarkers) {
        MarkersFragment markersFragment;
        if(selectedMarkers != null && selectedMarkers.size() > 0)
            markersFragment = MarkersFragment.newInstance((ArrayList<Marker>)selectedMarkers);
        else
            markersFragment = new MarkersFragment();

        markersFragment.setSelectionResultListener(this);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.content, markersFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onStoryCreated(Story story) {
        UserViewManager userViewManager = new UserViewManager(this);
        userViewManager.showStoryPublishingWarning(new OnCloseDialogListener() {
            @Override
            public void onCloseGamefication() {
                showPointsEarningAndClose();
            }
        });
    }

    private void showPointsEarningAndClose() {
        GameficationManager gameficationManager = new GameficationManager(this);
        gameficationManager.showGameficationAlert(new OnCloseDialogListener() {
            @Override
            public void onCloseGamefication() {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public void onSelectionResult(List<Marker> markers) {
        createStoryFragment.setSelectedMarkers(markers);
    }

}
