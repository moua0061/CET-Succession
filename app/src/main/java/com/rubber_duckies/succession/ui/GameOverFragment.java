package com.rubber_duckies.succession.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.rubber_duckies.succession.R;
import com.rubber_duckies.succession.MainActivity;

public class GameOverFragment extends Fragment {

	private boolean isVictory;
	private int power, loyalty, heat;
	private String narrative;

	public static GameOverFragment newInstance(boolean isVictory, int power, int loyalty, int heat,
			String narrative) {
		GameOverFragment f = new GameOverFragment();
		Bundle args = new Bundle();
		args.putBoolean("victory", isVictory);
		args.putInt("power", power);
		args.putInt("loyalty", loyalty);
		args.putInt("heat", heat);
		args.putString("narrative", narrative);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			isVictory = getArguments().getBoolean("victory");
			power = getArguments().getInt("power");
			loyalty = getArguments().getInt("loyalty");
			heat = getArguments().getInt("heat");
			narrative = getArguments().getString("narrative");
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_game_over, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// UI References
		View root = view.findViewById(R.id.layout_root);
		TextView tvHeader = view.findViewById(R.id.tv_header);
		TextView tvNarrative = view.findViewById(R.id.tv_narrative);
		TextView tvPower = view.findViewById(R.id.tv_final_power);
		TextView tvLoyalty = view.findViewById(R.id.tv_final_loyalty);
		TextView tvHeat = view.findViewById(R.id.tv_final_heat);
		Button btnPlayAgain = view.findViewById(R.id.btn_play_again);
		Button btnExit = view.findViewById(R.id.btn_exit_menu);

		// Set Data
		tvPower.setText(String.valueOf(power));
		tvLoyalty.setText(String.valueOf(loyalty));
		tvHeat.setText(String.valueOf(heat));
		tvNarrative.setText(narrative);

		// Apply Theme (Victory vs Defeat)
		if (isVictory) {
			root.setBackgroundResource(R.drawable.bg_victory);
			tvHeader.setText("VICTORY");
			// You can also change button colors dynamically here if needed
		} else {
			root.setBackgroundResource(R.drawable.bg_defeat);
			tvHeader.setText("DEFEAT");
		}

		// Listeners
		btnPlayAgain.setOnClickListener(v -> {

			if (getActivity() != null) {
				// Start a fresh instance of CoreLoopActivity
				Intent intent = new Intent(getActivity(), CoreLoopActivity.class);

				// Clear the back stack so the user can't "back" into the finished game
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(intent);
				getActivity().finish();
			}
		});
		btnExit.setOnClickListener(v ->

		{
			// Go to Main Menu
			if (getActivity() != null) {
				Intent intent = new Intent(getActivity(), MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				getActivity().finish();
			}
		});
	}
}
