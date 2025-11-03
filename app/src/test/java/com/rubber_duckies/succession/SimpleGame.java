package com.rubber_duckies.succession;

import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class SimpleGame {
	static List<Scenario> loadStubScenarios() {
		Scenario s = new Scenario();
		s.title = "Factory Visit";
		s.text = "Workers ask for relief.";
		s.choices = new Choice[3];
		String[] labels = { "Promise aid", "Stay neutral", "Blame Congress" };
		int[][] deltas = { { 3, 2, 1 }, { 1, 0, 2 }, { 2, 1, -1 } };
		for (int i = 0; i < 3; i++) {
			s.choices[i] = new Choice();
			s.choices[i].text = labels[i];
			s.choices[i].effects = new Effects();
			s.choices[i].effects.power = deltas[i][0];
			s.choices[i].effects.heat = deltas[i][1];
			s.choices[i].effects.loyalty = deltas[i][2];
		}
		return List.of(s);
	}

	static void applyChoice(GameState state, Choice c) {
		state.power += c.effects.power;
		state.heat += c.effects.heat;
		state.loyalty = Math.max(0, Math.min(100, state.loyalty + c.effects.loyalty));
	}

	@Test
	public void simulateMainLoop() {
		GameState state = new GameState();
		List<Scenario> deck = loadStubScenarios();

		for (Scenario s : deck) {
			System.out.println("\n" + s.title + "\n" + s.text);
			for (int i = 0; i < s.choices.length; i++)
				System.out.printf("[%d] %s\n", i + 1, s.choices[i].text);

			// simulate a random choice (no user input in test)
			int choice = new Random().nextInt(3);
			applyChoice(state, s.choices[choice]);

			System.out.printf("Chose: %s\n", s.choices[choice].text);
			System.out.printf("Power=%d Heat=%d Loyalty=%d\n",
					state.power, state.heat, state.loyalty);

			if (state.power >= 100) {
				System.out.println("You win.");
				break;
			}
			if (state.heat >= 100) {
				System.out.println("You lose.");
				break;
			}
		}
	}
}
