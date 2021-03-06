  package moviebuddy.data;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.AopTestUtils;

import moviebuddy.MovieBuddyFactory;
import moviebuddy.MovieBuddyProfile;
import moviebuddy.domain.Movie;
import moviebuddy.domain.MovieReader;

@SpringJUnitConfig(MovieBuddyFactory.class) // 위 둘을 한꺼번에 해결해주는 마법사 
@ActiveProfiles(MovieBuddyProfile.XML_MODE)
@TestPropertySource(properties = "movie.metadata=movie_metadata.xml")
public class XmlMovieReaderTest {
	
	@Autowired
	MovieReader movieReader;  //구체적인 타입으로 의존 관계로 주입받음. MovieReader로 받으면 동일한 타입의 빈이 있어서 테스트 실패 
	
	@Test
	void NotEmpty_LoadedMovies() {
		List<Movie> movies = movieReader.loadMovies();
		Assertions.assertEquals(1375, movies.size());
	}
	
	@Test
	void Check_MovieReaderType() {
		Assertions.assertTrue(AopUtils.isAopProxy(movieReader));
		
		MovieReader target = AopTestUtils.getTargetObject(movieReader);
		Assertions.assertTrue(XmlMovieReader.class.isAssignableFrom(target.getClass()));
	}

}
