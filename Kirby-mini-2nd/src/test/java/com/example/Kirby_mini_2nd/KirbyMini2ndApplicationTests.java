/*
package com.example.Kirby_mini_2nd;

import com.example.Kirby_mini_2nd.repository.entity.Follows;
import com.example.Kirby_mini_2nd.repository.entity.FollowsCompositekey;
import com.example.Kirby_mini_2nd.repository.entity.Hashtags;
import com.example.Kirby_mini_2nd.repository.entity.Posts;
import com.example.Kirby_mini_2nd.repository.repo.FollowsRepo;
import com.example.Kirby_mini_2nd.repository.repo.PostsRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class KirbyMini2ndApplicationTests {

	@Autowired
	private FollowsRepo followsRepo;
	private PostsRepo postsRepo; // 지오니


	@Test
	void FindByFollowerId() {
		List<Follows> followsList = followsRepo.findByFollowerId("1234");
		followsList.forEach(e -> System.out.println(e));
	}

	@Test
	void findByFollowing_id(){
		List<Follows> followsList = followsRepo.findByFollowingId("1234");
		followsList.forEach(e -> System.out.println(e));
	}

	@Test
	void SaveFollows() {
		Follows follow = new Follows();
		follow.setFollower_id("user123");
		follow.setFollowing_id("user456");
		follow.setFollow_time(LocalDateTime.now());

		// 데이터 저장 테스트
		followsRepo.save(follow);

		// 저장 후 엔터티가 잘 조회되는지 확인
		assert followsRepo.findById(new FollowsCompositekey("user123", "user456")).isPresent();
	}

	@Test
	void DeleteFollows(){
		// 복합키 생성이후 이 둘로 테스트
		FollowsCompositekey key = new FollowsCompositekey("1234", "jioni");
		Optional<Follows> savedFollow = followsRepo.findById(key);
		assert savedFollow.isPresent();

		followsRepo.deleteById(key);

		// 삭제가 성공적으로 이루어졌는지 확인
		Optional<Follows> deletedFollow = followsRepo.findById(key);
		assert deletedFollow.isEmpty();
	}
	@Test
	@Rollback(value = false)
	void CreatePosts(){
		//게시글 생성
		//ㅇㅅㅇ?DB에 저장!
		//1차때 지원이가 만든 DB 연동하는 부분
		Posts posts = new Posts();
		posts.setUsers("jioni");
		posts.setContents("/image/1.png");
		posts.setLocation("suwon");
		posts.setLikes_count(1);
		// 저장했으니까 findall 해서 봐도 괜찮고 rollback 을 false로 설정해서 db에서 확인해도 좋을거같아!!!!!!!!!!
		postsRepo.save(posts);

	}
	@Test
	@Rollback(value = false)
	void ShowPosts(){
		//메인페이지
		// 페이징 처리하여 5개만 뽑아서 볼 수 있는지 수정해주세요.
		List<Posts> mainPage = postsRepo.findAll();
		mainPage.forEach(e -> System.out.println(e));
		System.out.println(mainPage);
	}
	@Test
	@Rollback(value = false)
	void UpdatePosts(){
		//게시물 업데이트
		Posts posts =new Posts();
		posts= postsRepo.findById(1).get();
		posts.setContents("수정했다~");
		posts.setLocation("seoul");
		posts.setImage_link("/images/2.png");
		postsRepo.save(posts);
	}
	@Test
	@Rollback(value = false)
	void DeletePosts(){
		//게시물 삭제
		postsRepo.deleteById(1);
	}

	@Test
	@Rollback(value = false)
	void SearchByPost(){
		List<Hashtags>
		//게시물 검색
		//string hashname == hasgid  ->id
		//id == postpk
		// 이 결과를 list
		// code;;;;;;

		//사실 해시태그 검색을 하고싶음ㅎㅎ.. ㅎㅎ 다음에
		// 우리가 posttages 에서 해시태그 id로 검색하면
		//
		// 해당 해시태그 id가 걸려있는 게시글들이 나오겠지?
		// 그럼 그 게시글들의 id를 묶어서 보여주면 될거같아.
		// Lisr<int> <<- post의 id들이 들어와.
		// post img,hashtag,createtime 등등 from post
		// where hashtag_id

	}


}
*/
