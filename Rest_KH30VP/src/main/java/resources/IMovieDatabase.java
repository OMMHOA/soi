package resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("MovieDatabase")
@Consumes({MediaType.APPLICATION_XML,
	MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML,
	MediaType.APPLICATION_JSON})
public interface IMovieDatabase {
	@GET
	@Path("movies")
	public Movies getMovies();
	
	@GET
	@Path("movies/{id}")
	public Movie getMovie(@PathParam("id") int id);
	
	@POST
	@Path("movies")
	public Result addMovie(Movie movie);
	
	@PUT
	@Path("movies/{id}")
	public void updateMovie(@PathParam("id") int id,
			Movie movie);
	
	@DELETE
	@Path("movies/{id}")
	public void deleteMovie(@PathParam("id") int  id);
	
	@GET
	@Path("movies/find")
	public List<Integer> findMovies(@QueryParam("year") int year, @QueryParam("orderby") String field);
	
	
}
