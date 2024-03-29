package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.lang.Math.*;

import javax.annotation.Nonnull;


import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;
import com.google.common.graph.*;

import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.*;

public class gptTwelve implements Ai {

	@Nonnull
	@Override
	public String name() {
		return "gptTwelve";
	}

	@Nonnull
	@Override
	public Move pickMove(
			@Nonnull Board board,
			Pair<Long, TimeUnit> timeoutPair) {
		var moves = board.getAvailableMoves().asList();
		int scoreMax = Integer.MIN_VALUE;
		Move bestMove = moves.get(new Random().nextInt(moves.size()));

		//keep track of current mrX location to stop him from going back and forth:
		int lastMrX = moves.get(0).source();


		for (Move move : moves) {
			if (move.source() == lastMrX) {
				continue;
			}

			Board.GameState state = (Board.GameState) board;
			state = state.advance(move);
			int newScore = score(state);
			if (newScore > scoreMax) {
				scoreMax = newScore;
				bestMove = move;
			}
		}
		return bestMove;
	}

	public int score(Board board) {

		int totalScore = 0;

		// create list of detectives
		List<Piece.Detective> detectiveList = new ArrayList<>();
		for (Piece piece : board.getPlayers()) {
			if (piece.isDetective()) {
				detectiveList.add((Piece.Detective) piece);
			}
		}

		//get mrX location
		List<Move> moves = new ArrayList<>(board.getAvailableMoves());
		int mrXLocation = moves.get(0).source();



		//find the total distance of detectives from mrX using BFS method
		int distanceScore = 0;
		for (Piece.Detective detective : detectiveList) {
			int detectiveLocation = board.getDetectiveLocation(detective).get();
			distanceScore = distanceScore + bfsDistance(mrXLocation, detectiveLocation, board);
		}

//		calculate a 'freedom of movement score'
		int freedomScore = 0;

		//create list of detective locations
		List<Integer> detectiveLocations = new ArrayList<>();
		for (Piece.Detective detective : detectiveList) {
			detectiveLocations.add(board.getDetectiveLocation(detective).get());
		}

		List<Integer> adjacentNodes = new ArrayList<>(board.getSetup().graph.adjacentNodes(mrXLocation));

		for (int dLoc : detectiveLocations) {
			if (!adjacentNodes.contains(dLoc)) {
				freedomScore++;
			}
		}
		System.out.println(distanceScore);

		totalScore = distanceScore*2 + freedomScore;

		return totalScore;

	}

	public int bfsDistance(int mrXLocation, int detectiveLocation, Board board) {

		List<Integer> visited = new ArrayList<>();
		Queue<Integer> notVisited = new LinkedList<>();

		visited.add(mrXLocation);
		notVisited.add(mrXLocation);

		int distance = 0;
		int currentDepthNodeCount = 1;
		int nextDepthNodeCount = 0;
		boolean nodeFound = false;



		while (!notVisited.isEmpty() && !nodeFound) {
			int from = notVisited.remove();
			currentDepthNodeCount--;

			for (int node : board.getSetup().graph.adjacentNodes(from)) {
				if (!visited.contains(node)) {
					visited.add(node);
					notVisited.add(node);
					nextDepthNodeCount++;
					if (node == detectiveLocation) {
						nodeFound = true;
						break;
					}
				}
			}

			if (currentDepthNodeCount == 0) {
				currentDepthNodeCount = nextDepthNodeCount;
				nextDepthNodeCount = 0;
				distance++;
			}


		}

		//square root distance in order to weight detectives closer to mrX more heavily
		//than those further away
		distance = (int) Math.ceil(Math.sqrt(distance));
		return distance;
	}
}
