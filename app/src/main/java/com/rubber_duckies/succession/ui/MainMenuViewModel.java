package com.rubber_duckies.succession.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
//import com.rubber_duckies.succession.GameStateManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainMenuViewModel extends ViewModel {

    // Reference to GameStateManager (inject via dependency injection in real app)
    //private GameStateManager gameStateManager;

    private final MutableLiveData<Boolean> saveGameExists = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public LiveData<Boolean> getSaveGameExists() {
        return saveGameExists;
    }

    /**
     * Checks if a saved game exists in the database
     * Updates saveGameExists LiveData based on result
     */
    public void checkForSavedGame() {
        executorService.execute(() -> {
            try {
                //db query GameStateManager for existing saved data
                //boolean hasSave = gameStateManager.hasSavedGame();
                //saveGameExists.postValue(hasSave);
            } catch (Exception e) {
                //assume no saved exists
                //saveGameExists.postValue(false);
            }
        });
    }

    /**
     * Starts a new game by resetting the game state
     * Called when user taps START button
     */
    public void onStartNewGame() {
        executorService.execute(() -> {
            //gameStateManager.resetGame();
        });
    }

    /**
     * Loads existing saved game
     * Called when user taps CONTINUE button
     */
    public void onContinueGame() {
        executorService.execute(() -> {
            //gameStateManager.loadSavedGame();
        });
    }

    /**
     * Handles app exit logic
     * Called when user taps EXIT button
     */
    public void onExitApp() {
        // Any cleanup logic before exit can go here
        // The actual app closing is handled by the Fragment/Activity
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}