package resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.MovieNotFoundException;

public class MovieDatabase implements IMovieDatabase{
	private static final Map<Integer, Movie> MOVIEDB = new HashMap<>();
	private static int nextMovieId = 0;
	
	@Override
	public Movies getMovies() {
		Movies movies = new Movies();
		movies.setMovies(new ArrayList<>(MOVIEDB.values()));
		return movies;
	}

	@Override
	public Movie getMovie(int id) {
		Movie movie = MOVIEDB.get(id);
		if (movie == null) {
			throw new MovieNotFoundException("Movie not found");
		}
		return movie;
	}

	@Override
	public Result addMovie(Movie movie) {
		int id = MovieDatabase.nextMovieId;
		MovieDatabase.nextMovieId++;
		Result result = new Result();
		result.setId(id);
		MOVIEDB.put(id, movie);
		return result;
	}

	@Override
	public void updateMovie(int id, Movie movie) {
		MOVIEDB.remove(id);
		MOVIEDB.put(id, movie);
	}

	@Override
	public void deleteMovie(int id) {
		MOVIEDB.remove(id);
	}

	@Override
	public List<Integer> findMovies(int year, String field) {
		
		List<Integer> movies = findMoviesFromYear(year);
		if ("Title".equals(field)) {
			System.out.println("ordering by title");
			movies = orderMoviesByTitle(movies);
		} else if ("Director".equals(field)){
			System.out.println("ordering by director");
			movies = orderMoviesByDirector(movies);
		} else {
			System.out.println(field);
		}
		return movies;
	}

	private List<Integer> orderMoviesByDirector(List<Integer> movies) {
		List<Integer> sortedMovies = new ArrayList<>(movies);
		Collections.sort(sortedMovies, new Comparator<Integer>() {
			@Override
			public int compare(Integer id1, Integer id2) {
				return MOVIEDB.get(id1).getDirector().compareTo(
						MOVIEDB.get(id2).getDirector());
			}
		});
		return sortedMovies;
	}

	private List<Integer> orderMoviesByTitle(List<Integer> movies) {
		List<Integer> sortedMovies = new ArrayList<>(movies);
		Collections.sort(sortedMovies, new Comparator<Integer>() {
			@Override
			public int compare(Integer id1, Integer id2) {
				return MOVIEDB.get(id1).getTitle().compareTo(
						MOVIEDB.get(id2).getTitle());
			}
		});
		return sortedMovies;
	}

	private List<Integer> findMoviesFromYear(int year) {
		List<Integer> movies = new ArrayList<>();
		for(Integer id : MOVIEDB.keySet()) {
			if(MOVIEDB.get(id).getYear() == year) {
				movies.add(id);
			}
		}
		return movies;
	}
}
